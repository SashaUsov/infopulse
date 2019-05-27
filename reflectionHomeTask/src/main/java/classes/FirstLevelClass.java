package classes;

import interfaces.SimpleInterface;

public class FirstLevelClass implements SimpleInterface {
    @Override
    public void doAction() {
        System.out.println("classes.FirstLevelClass injected");
    }
}
