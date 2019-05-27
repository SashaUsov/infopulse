package ijector;

import classes.FirstLevelClass;
import classes.SecondLevelClass;
import interfaces.SimpleInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DynamicСlassInjections {

    public void injectClass(Object o, String pathToFile) {
        if ("classes.PrimaryClass".equals(o.getClass().getTypeName())) {
            try {
                final Field imp = o.getClass().getDeclaredField("imp");
                imp.setAccessible(true);
                imp.set(o, createInjectedClass(pathToFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SimpleInterface createInjectedClass(String pathToFile) throws Exception {
        String className = getClassNameFromFile(pathToFile);
        if ("classes.SecondLevelClass".equals(className)) {
            Class c = Class.forName("classes.SecondLevelClass");
            Object obj = c.newInstance();
            return (SecondLevelClass) obj;
        } else if ("classes.FirstLevelClass".equals(className)) {
            Class c = Class.forName("classes.FirstLevelClass");
            Object obj = c.newInstance();
            return  (FirstLevelClass) obj;
        } else {
            throw new ClassNotFoundException();
        }
    }

    private String getClassNameFromFile(String pathToFile) throws FileNotFoundException {
        final String textFromFileAsString = getTextFromFileAsString(pathToFile);

        final String[] splitText = textFromFileAsString.split(":");
        return splitText[2].trim();
    }

    private String getTextFromFileAsString(String pathToFile) throws FileNotFoundException {
        String textFromFile = null;

        try {
            Path path = Paths.get(pathToFile);
            textFromFile = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw  new FileNotFoundException();
        }
        return textFromFile;
    }
}
