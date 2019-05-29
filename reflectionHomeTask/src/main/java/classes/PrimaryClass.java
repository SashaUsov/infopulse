package classes;

import interfaces.SimpleInterface;

public class PrimaryClass {
    private SimpleInterface imp;

    public String doAction() {
        return imp.doAction();
    }
}
