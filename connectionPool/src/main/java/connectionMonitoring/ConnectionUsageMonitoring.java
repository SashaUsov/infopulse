package connectionMonitoring;

import connectPool.ConnectionHolder;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionUsageMonitoring {
    private BlockingQueue<ConnectionHolder> connectionsPool;

    public ConnectionUsageMonitoring(BlockingQueue<ConnectionHolder> connectionsPool) {
        this.connectionsPool = connectionsPool;
    }
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void run() {
        executorService.schedule(this::cleanUpConnectionPool, 1000, TimeUnit.MILLISECONDS);

    }

    private void cleanUpConnectionPool() {
        doWork();
        executorService.schedule(this::cleanUpConnectionPool, 1000, TimeUnit.MICROSECONDS);
    }

    private void doWork() {
        try {
            startMonitoringConnectionUsage();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private void startMonitoringConnectionUsage() throws SQLException {
        final Iterator<ConnectionHolder> iterator = connectionsPool.iterator();
        closeUnusedConnection(iterator);
    }

    private void closeUnusedConnection(Iterator<ConnectionHolder> iterator) throws SQLException {
        if (iterator.hasNext()) {
            final ConnectionHolder connectionHolder = iterator.next();
            if (connectionTimeout(connectionHolder.getLastAccessTime())) {
                iterator.remove();
                connectionHolder.getConnection().close();
            }
        }
    }

    private boolean connectionTimeout(Date lastAccessTime) {
        final long l = Duration.between(lastAccessTime.toInstant(), Instant.now()).toMillis();//.get(ChronoUnit.MILLIS);
        return l > 2000;
                //Duration.between(lastAccessTime.toInstant(), Instant.now()).get(ChronoUnit.MILLIS) > 1000;
    }

}
