public class Main {
    public static void main(String[] args) {
        Automobile car = new Automobile("M716EM147");
        MethodInvoker.invokeAnnotatedHidden(car);
        System.out.println(car);
    }
}
