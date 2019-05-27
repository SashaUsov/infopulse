package classes;

import interfaces.SimpleInterface;

public class SecondLevelClass implements SimpleInterface {
    @Override
    public void doAction() {
        System.out.println("classes.SecondLevelClass injected");
    }
}
