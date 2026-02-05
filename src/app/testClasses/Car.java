package app.testClasses;

import framework.annotations.AutoWired;
import framework.annotations.Component;

@Component
public class Car {

    private final Engine engine;

    @AutoWired
    public Car(final Engine engine) {
        this.engine = engine;
        System.out.println("car created");
    }

}
