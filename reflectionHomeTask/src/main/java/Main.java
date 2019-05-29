import classes.PrimaryClass;
import factory.Factory;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, InstantiationException, IllegalAccessException {

        Factory f = new Factory();

        PrimaryClass prC = f.createClassFromFile("initFile.txt");

        System.out.println(prC.doAction());
    }
}
