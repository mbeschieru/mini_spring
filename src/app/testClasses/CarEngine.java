package app.testClasses;

import framework.annotations.Component;

@Component
public class CarEngine implements Engine {
    public CarEngine(){
        System.out.println("carEngine created");
    }
}
