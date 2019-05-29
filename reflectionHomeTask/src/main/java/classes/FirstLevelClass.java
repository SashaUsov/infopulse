package classes;

import interfaces.SimpleInterface;

public class FirstLevelClass implements SimpleInterface {
    @Override
    public String doAction() {
        return "classes.FirstLevelClass injected";
    }
}
