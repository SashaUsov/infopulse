package factory;

public class SecondLevelTestClass implements SimpleTestInterface {
    @Override
    public String doAction() {
        return "classes.SecondLevelTestClass injected";
    }
}
