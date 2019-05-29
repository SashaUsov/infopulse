package classes;

import interfaces.SimpleInterface;

public class SecondLevelClass implements SimpleInterface {
    @Override
    public String doAction() {
        return "classes.SecondLevelClass injected";
    }
}
