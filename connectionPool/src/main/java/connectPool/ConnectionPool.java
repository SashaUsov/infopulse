package connectPool;

import connectionMonitoring.ConnectionUsageMonitoring;
import exc.ConnectionPoolIsEmptyException;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
    private BlockingQueue<ConnectionHolder> connectionsPool = new LinkedBlockingDeque<>();
    @Getter
    private int pollSize;
    private long connectionTimeoutInThePool;
    private int usedConnections;
    private Properties dbProps;

    public ConnectionPool(int pollSize, long connectionTimeoutInSecond) {
        this.pollSize = pollSize;
        this.connectionTimeoutInThePool = connectionTimeoutInSecond;
        try {
            createConnectionAndFillThePool();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        ConnectionUsageMonitoring connectionUsageMonitoring = new ConnectionUsageMonitoring(this.connectionsPool);
        connectionUsageMonitoring.run();
    }

    int getCurrentPoolSize() {
        return connectionsPool.size();
    }

    void decrementUsedConnections() {
        usedConnections--;
    }

    void returnConnectionToThePoll(Connection connection) {
        connectionsPool.add(new ConnectionHolder(connection, new Date()));
    }

    @SneakyThrows
    public  Connection getConnection() {
        if (pollSize == usedConnections) {
            throw new ConnectionPoolIsEmptyException("Connection pool is empty!");
        } else if (connectionsPool.isEmpty() && usedConnections < pollSize) {
            usedConnections++;
            return new JdbcConnection(createAndGetConnection(), this);
        } else {
            return getPendingConnection();
        }
    }

    private Connection getPendingConnection() throws InterruptedException {
        final ConnectionHolder connectionHolder = connectionsPool.poll(connectionTimeoutInThePool, TimeUnit.SECONDS);
            usedConnections++;
            return new JdbcConnection(connectionHolder.getConnection(), this);

    }

    private void createConnectionAndFillThePool() throws SQLException, ClassNotFoundException {
        for (int i = 0; i < pollSize; i++) {
            final Connection connection = createAndGetConnection();
            Date connectionCreationTime = new Date();
            connectionsPool.add(new ConnectionHolder(connection, connectionCreationTime));
        }
    }

    private Connection createAndGetConnection() throws ClassNotFoundException, SQLException {
        loadPropertyFile();
        String driver = dbProps.getProperty("jdbc.driver");
        Class.forName(driver);
        String url = dbProps.getProperty("jdbc.url");
        Properties props = setAndGetDataBaseProperties();
        return getConnectionFromDriverManager(url, props);
    }

    private void loadPropertyFile() {
        try (FileInputStream in = new FileInputStream("/Users/samsonov/IdeaProjects/infopulse/connectionPool/src/main/resources/db.properties")) {
            dbProps  = new Properties();
            dbProps.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
        return DriverManager.getConnection(url, props);
    }

    private Properties setAndGetDataBaseProperties() {
        Properties props = new Properties();
        String username = dbProps.getProperty("jdbc.username");
        String password = dbProps.getProperty("jdbc.password");
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("ssl", "false");
        return props;
    }

    void close(Connection connection) throws SQLException {
        if (connection.isValid(0)) {
            closeValidConnection(connection);
        } else {
            connection.close();
        }
    }

    private void closeValidConnection(Connection connection) throws SQLException {
        if (getCurrentPoolSize() == getPollSize()) {
            closeConnectionIfPoolFull(connection);
        }
        putUserConnectionToThePool(connection);
    }

    private void closeConnectionIfPoolFull(Connection connection) throws SQLException {
        decrementUsedConnections();
        connection.close();
    }

    private void putUserConnectionToThePool(Connection connection) {
        decrementUsedConnections();
        returnConnectionToThePoll(connection);
    }
}
