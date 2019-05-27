import classes.PrimaryClass;
import ijector.DynamicСlassInjections;

public class Main {
    public static void main(String[] args) {
        DynamicСlassInjections dynamicСlassСreation = new DynamicСlassInjections();

        PrimaryClass mainClass = new PrimaryClass();

        dynamicСlassСreation.injectClass(mainClass, "initFile.txt");

        mainClass.doAction();
    }
}
