package factory;

public class FirstLevelTestClass implements SimpleTestInterface {
    @Override
    public String doAction() {
        return "classes.FirstLevelTestClass injected";
    }
}
