package io.pivotal.ecosystem.mssqlserver.broker;

import io.pivotal.ecosystem.mssqlserver.broker.connector.SqlServerServiceInfo;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

public class H2Sql implements Sqlinator {

    private JdbcTemplate jdbcTemplate;

    public H2Sql(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createDb(String db) {
        jdbcTemplate.execute("CREATE SCHEMA if not exists " + db);
        jdbcTemplate.execute("SET SCHEMA PUBLIC");
    }

    public void deleteDb(String db) {
        jdbcTemplate.execute("DROP SCHEMA if exists " + db);
    }

    public boolean databaseExists(String db) {
        try {
            jdbcTemplate.execute("SET SCHEMA " + db);
            jdbcTemplate.execute("SET SCHEMA PUBLIC");
            return true;
        } catch (Throwable t) {
            jdbcTemplate.execute("SET SCHEMA PUBLIC");
            return false;
        }
    }

    public void userCreate(Map<String, Object> userCredentials) {
        jdbcTemplate.execute("SET SCHEMA " + userCredentials.get(SqlServerServiceInfo.DATABASE) + "; CREATE USER " +
                userCredentials.get(SqlServerServiceInfo.USERNAME) + " PASSWORD '" +
                userCredentials.get(SqlServerServiceInfo.PASSWORD) + "'");
        jdbcTemplate.execute("SET SCHEMA PUBLIC");
    }

    public void userDelete(String db, String uid) {
        jdbcTemplate.execute("SET SCHEMA " + db + "; DROP USER IF EXISTS " + uid);
        jdbcTemplate.execute("SET SCHEMA PUBLIC");
    }

    public boolean userExists(String db, String uid) {
        try {
            jdbcTemplate.execute("SET SCHEMA " + db + "; COMMENT ON USER " + uid + " IS 'user used for testing'");
            jdbcTemplate.execute("SET SCHEMA PUBLIC");
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
