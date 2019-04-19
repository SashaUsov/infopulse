package connectPool;

import exc.ConnectionPoolIsEmptyException;
import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ConnectionPoolTest {

    @Test(expected = NullPointerException.class)
    @SneakyThrows
    public void shouldThrowNullPointerExceptionAfterConnectionClosed() {
        ConnectionPool connectionPool = new ConnectionPool(1);
        final Connection connectionOne = connectionPool.getConnection();
        connectionOne.close();
        connectionOne.setSavepoint();
    }

    @Test
    @SneakyThrows
    public void shouldCreateAndGetOneConnection() {
        ConnectionPool connectionPool =  new ConnectionPool(1);
        final Connection connection = connectionPool.getConnection();

        assertNotNull(connection);
    }

    @Test(expected = ConnectionPoolIsEmptyException.class)
    @SneakyThrows
    public void shouldThrowConnectionPoolIsEmptyExceptionOnTheSecondConnectionRequest() {
        ConnectionPool connectionPool =  new ConnectionPool(1);
        final Connection connectionOne = connectionPool.getConnection();
        final Connection connectionTwo = connectionPool.getConnection();
    }

    @Test
    @SneakyThrows
    public void connectionShouldBeNotEquals(){
        ConnectionPool connectionPool =  new ConnectionPool(2);
        final Connection connectionOne = connectionPool.getConnection();
        final Connection connectionTwo = connectionPool.getConnection();

        assertNotEquals(connectionOne, connectionTwo);
    }

    @Test(expected = ConnectionPoolIsEmptyException.class)
    @SneakyThrows
    public void shouldThrowsConnectionPoolIsEmptyExceptionInMultiThreaded() {
        ConnectionPool connectionPool =  new ConnectionPool(2);

        try {

            final ExecutorService executorService = Executors.newFixedThreadPool(3);
            final Future<?> submit = executorService.submit(() -> {
                System.out.println(connectionPool.getConnection());
            });
            final Future<?> submit1 = executorService.submit(() -> {
                System.out.println(connectionPool.getConnection());
            });
            final Future<?> submit2 = executorService.submit(() -> {
                System.out.println(connectionPool.getConnection());
            });

            executorService.shutdown();
            submit.get();
            submit1.get();
            submit2.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw e.getCause();
        }
    }
}