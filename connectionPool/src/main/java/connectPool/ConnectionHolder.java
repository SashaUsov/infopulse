package connectPool;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.util.Date;

@Setter
@Getter
public class ConnectionHolder {
    private Connection connection;
    private Date lastAccessTime;

    public ConnectionHolder(Connection connection, Date lastAccessTime) {
        this.connection = connection;
        this.lastAccessTime = lastAccessTime;
    }
}
