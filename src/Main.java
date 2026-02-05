import app.testClasses.Car;
import framework.context.ApplicationContext;

public class Main {

    public static void main(final String[] args) {
        final ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.scan("app");
        applicationContext.getBean(Car.class);
    }

}
