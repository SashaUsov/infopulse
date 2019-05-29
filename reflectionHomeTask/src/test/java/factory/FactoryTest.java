package factory;

import classes.FirstLevelClass;
import classes.PrimaryClass;
import interfaces.SimpleInterface;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.instanceOf;


import java.io.FileNotFoundException;



public class FactoryTest {

    @SneakyThrows
    @Test(expected = FileNotFoundException.class)
    public void shouldThrowFileNotFoundExceptionIfPathToFileIsNotCorrect () {
        Factory factory = new Factory();
        factory.createClassFromFile("wrongPathToFile.txt");
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionIfPropertiesFileDoesNotHaveFullConditions(){
        Factory factory = new Factory();
        factory.createClassFromFile("initFile1.txt");
    }

    @SneakyThrows
    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowClassNotFoundExceptionIfPropertiesFileContainsIncorrectSourceData () {
        Factory factory = new Factory();
        factory.createClassFromFile("initFile2.txt");
    }

    @SneakyThrows
    @Test(expected = InstantiationException.class)
    public void shouldInstantiationExceptionWhenClassHasNoNullaryConstructor () {
        Factory factory = new Factory();
        factory.createClassFromFile("initFile3.txt");
    }

    @SneakyThrows
    @Test
    public void shouldInjectFirstLevelClass() {
        Factory factory = new Factory();
        PrimaryClass primaryClass = factory.createClassFromFile("initFile.txt");

        final SimpleInterface imp = primaryClass.getImp();

        Assert.assertThat(imp, instanceOf(FirstLevelClass.class));
    }

    @SneakyThrows
    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionBecauseFieldNotAssignableFrom(){
        Factory factory = new Factory();
        factory.createClassFromFile("initFile5.txt");
    }

    @SneakyThrows
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnANullFieldCall() {
        Factory factory = new Factory();
        PrimaryClass classFromFile = factory.createClassFromFile("initFile6.txt");
        classFromFile.doAction();
    }
}