package classes;

import interfaces.SimpleInterface;
import lombok.Getter;

@Getter
public class PrimaryClass {
    private SimpleInterface imp;

    public String doAction() {
        return imp.doAction();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimaryClass that = (PrimaryClass) o;

        return imp.equals(that.imp);
    }

    @Override
    public int hashCode() {
        return imp.hashCode();
    }
}
