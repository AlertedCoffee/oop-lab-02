import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class MethodInvoker {
    private MethodInvoker() {
    }

    public static void invokeAnnotatedHidden(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Нужен объект, у которого вызываются методы");
        }

        Method[] methods = target.getClass().getDeclaredMethods();

        for (Method method : methods) {
            Repeat repeat = method.getAnnotation(Repeat.class);
            if (repeat == null) {
                continue;
            }

            int modifiers = method.getModifiers();
            if (!Modifier.isProtected(modifiers) && !Modifier.isPrivate(modifiers)) {
                System.out.printf(
                        "Пропуск %s: аннотация есть, видимость %s%n",
                        method.getName(),
                        visibility(modifiers));
                continue;
            }
            if (Modifier.isStatic(modifiers)) {
                throw new IllegalStateException("Статические методы не вызываются: " + method.getName());
            }

            int times = repeat.value();
            if (times < 0) {
                throw new IllegalArgumentException("Отрицательное число повторов у " + method.getName());
            }

            method.setAccessible(true);
            System.out.printf(
                    "%s %s — %d раз(а)%n",
                    visibility(modifiers),
                    method.getName(),
                    times);

            for (int i = 0; i < times; i++) {
                Object[] args = argumentsFor(method.getParameterTypes());
                try {
                    method.invoke(target, args);
                } catch (Exception e) {
                    throw new IllegalStateException("Ошибка в " + method.getName(), e);
                }
            }
        }
    }

    private static String visibility(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return "public";
        }
        if (Modifier.isProtected(modifiers)) {
            return "protected";
        }
        if (Modifier.isPrivate(modifiers)) {
            return "private";
        }
        return "package";
    }

    private static Object[] argumentsFor(Class<?>[] types) {
        Object[] args = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            args[i] = argumentFor(types[i]);
        }
        return args;
    }

    private static Object argumentFor(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.TRUE;
        }
        if (type == byte.class || type == Byte.class) {
            return (byte) 1;
        }
        if (type == short.class || type == Short.class) {
            return (short) 1;
        }
        if (type == int.class || type == Integer.class) {
            return 2;
        }
        if (type == long.class || type == Long.class) {
            return 1000L;
        }
        if (type == float.class || type == Float.class) {
            return 11.7f;
        }
        if (type == double.class || type == Double.class) {
            return 11.7d;
        }
        if (type == char.class || type == Character.class) {
            return 'A';
        }
        if (type == String.class) {
            return "значение";
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            if (constants.length == 0) {
                throw new IllegalArgumentException("У перечисления нет констант: " + type.getName());
            }
            return constants[0];
        }
        if (type.isArray()) {
            return Array.newInstance(type.getComponentType(), 0);
        }
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    "Нельзя создать аргумент типа " + type.getName() + " без null",
                    e);
        }
    }
}
