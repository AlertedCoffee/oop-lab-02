public class Main {
    public static void main(String[] args) {
        Automobile car = MethodInvoker.create(Automobile.class);
        MethodInvoker.invokeAnnotatedHidden(car);
        System.out.println(car);
    }
}
