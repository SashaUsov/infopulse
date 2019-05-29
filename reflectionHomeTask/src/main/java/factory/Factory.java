package factory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Factory {

    @SuppressWarnings("unchecked")
    public <T> T createClassFromFile(String pathToFile) throws FileNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String[] buildParameters = getBuildParameters(pathToFile);
        Class<?> mainClass = createClassForName(buildParameters[0]);
        Class<?> fieldType = createClassForName(buildParameters[1]);
        Class<?> classToInject = createClassForName(buildParameters[2]);
        final Object mainClassObject = mainClass.newInstance();

        if (!fieldType.isAssignableFrom(classToInject)) throw new RuntimeException();

        final Field[] mainClassFields = mainClass.getDeclaredFields();
        for (Field field : mainClassFields) {
            injectFields(fieldType, classToInject, mainClassObject, field);
        }
        return (T) mainClassObject;
    }

    private void injectFields(Class<?> fieldType, Class<?> classToInject,
                              Object mainClassObject, Field field)
            throws IllegalAccessException, InstantiationException {

        if (fieldType.isAssignableFrom(field.getType())) {
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            field.set(mainClassObject, classToInject.newInstance());
            field.setAccessible(isAccessible);
        }
    }

    private String[] getBuildParameters(String pathToFile) throws FileNotFoundException {
        String[] buildParameters = getTextFromFileAsString(pathToFile).split(":");
        if (buildParameters.length != 3) throw new RuntimeException();
        return buildParameters;
    }

    private String getTextFromFileAsString(String pathToFile) throws FileNotFoundException {
        String textFromFile = null;
        try {
            Path path = Paths.get(pathToFile);
            textFromFile = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        return textFromFile;
    }

    private Class<?> createClassForName(String className) throws ClassNotFoundException {
        return Class.forName(className.trim());
    }

}
