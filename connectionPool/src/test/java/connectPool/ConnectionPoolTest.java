package connectPool;

import exc.ConnectionPoolIsEmptyException;
import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Connection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConnectionPoolTest {

    @Test(expected = NullPointerException.class)
    @SneakyThrows
    public void shouldThrowNullPointerExceptionAfterConnectionClosed() {
        ConnectionPool connectionPool = new ConnectionPool(1, 5);
        final Connection connectionOne = connectionPool.getConnection();
        connectionOne.close();
        connectionOne.setSavepoint();
    }

    @Test
    @SneakyThrows
    public void shouldCreateAndGetOneConnection() {
        ConnectionPool connectionPool =  new ConnectionPool(1, 5);
        final Connection connection = connectionPool.getConnection();

        assertNotNull(connection);
    }

    @Test(expected = ConnectionPoolIsEmptyException.class)
    @SneakyThrows
    public void shouldThrowConnectionPoolIsEmptyExceptionOnTheSecondConnectionRequest() {
        ConnectionPool connectionPool =  new ConnectionPool(1, 5);
        final Connection connectionOne = connectionPool.getConnection();
        final Connection connectionTwo = connectionPool.getConnection();
    }

    @Test
    @SneakyThrows
    public void connectionShouldBeNotEquals(){
        ConnectionPool connectionPool =  new ConnectionPool(2, 5);
        final Connection connectionOne = connectionPool.getConnection();
        final Connection connectionTwo = connectionPool.getConnection();

        assertNotEquals(connectionOne, connectionTwo);
    }

    @Test(expected = ConnectionPoolIsEmptyException.class)
    @SneakyThrows
    public void shouldThrowsConnectionPoolIsEmptyExceptionInMultiThreaded() {
        ConnectionPool connectionPool =  new ConnectionPool(2, 5);

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

    @SneakyThrows
    @Test
    public void shouldCloseUnusedConnectionsAndMakeConnectionPoolEmpty() {
        ConnectionPool connectionPool =  new ConnectionPool(3, 5);
        final Connection connectionOne = connectionPool.getConnection();

        Thread.sleep(2500);

        connectionOne.close();

        assertTrue(connectionPool.getCurrentSize() == 1);
    }

    @SneakyThrows
    @Test
    public void shouldNotCloseUnusedConnectionsInConnectionPoolWaitingTimeNotExceeded() {
        ConnectionPool connectionPool =  new ConnectionPool(3, 5);
        final Connection connectionOne = connectionPool.getConnection();

        Thread.sleep(500);

        connectionOne.close();

        assertTrue(connectionPool.getCurrentSize() == 3);
    }

    @SneakyThrows
    @Test
    public void shouldReturnConnectionAfterConnectionPoolClearsTheConnectionMonitor() {
        ConnectionPool connectionPool =  new ConnectionPool(3, 5);
        final Connection connectionOne = connectionPool.getConnection();

        Thread.sleep(2500);

        final Connection connectionTwo = connectionPool.getConnection();

        assertNotNull(connectionTwo);
    }

    @SneakyThrows
    @Test(expected = ConnectionPoolIsEmptyException.class)
    public void shouldThrowNullPointerExceptionAfterClearingConnectionPoolAndRequestingAnExcessiveConnection() {
        ConnectionPool connectionPool =  new ConnectionPool(2, 5);

        Thread.sleep(2500);

        final Connection connectionOne = connectionPool.getConnection();
        final Connection connectionTwo = connectionPool.getConnection();
        final Connection excessConnection = connectionPool.getConnection();

    }
}