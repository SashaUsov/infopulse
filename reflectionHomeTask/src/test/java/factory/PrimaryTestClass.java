package factory;

public class PrimaryTestClass {
    private SimpleTestInterface imp;

    public String doAction() {
        return imp.doAction();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimaryTestClass that = (PrimaryTestClass) o;

        return imp.equals(that.imp);
    }

    @Override
    public int hashCode() {
        return imp.hashCode();
    }
}
