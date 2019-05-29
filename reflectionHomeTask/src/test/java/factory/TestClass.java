package factory;

import interfaces.SimpleInterface;

public class TestClass {
    private SimpleInterface imp;

    public TestClass(SimpleInterface imp) {
        this.imp = imp;
    }
}
