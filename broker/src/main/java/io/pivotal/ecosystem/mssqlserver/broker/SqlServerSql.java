package io.pivotal.ecosystem.mssqlserver.broker;

import io.pivotal.ecosystem.mssqlserver.broker.connector.SqlServerServiceInfo;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

public class SqlServerSql implements Sqlinator {

    private JdbcTemplate jdbcTemplate;

    public SqlServerSql(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createDb(String db) {
        jdbcTemplate.execute("use [master]; exec sp_configure 'contained database authentication', 1 reconfigure; CREATE DATABASE ["
                + db + "]; ALTER DATABASE ["
                + db + "] SET CONTAINMENT = PARTIAL");
    }

    public void deleteDb(String db) {
        jdbcTemplate.execute("ALTER DATABASE "
                + db + " SET SINGLE_USER WITH ROLLBACK IMMEDIATE; DROP DATABASE "
                + db);
    }

    public boolean databaseExists(String db) {
        jdbcTemplate.execute("USE [MASTER];");
        return jdbcTemplate.queryForObject("SELECT count(*) FROM sys.databases WHERE name = ?",
                new Object[]{db}, Integer.class) > 0;
    }

    public void userCreate(Map<String, Object> userCredentials) {
        jdbcTemplate.execute("USE [" + userCredentials.get(SqlServerServiceInfo.DATABASE) + "]; CREATE USER ["
                + userCredentials.get(SqlServerServiceInfo.USERNAME)
                + "] WITH PASSWORD='" + userCredentials.get(SqlServerServiceInfo.PASSWORD)
                + "', DEFAULT_SCHEMA=[dbo]; EXEC sp_addrolemember 'db_owner', '"
                + userCredentials.get(SqlServerServiceInfo.USERNAME) + "'; USE [MASTER]");
    }

    public void userDelete(String db, String uid) {
        jdbcTemplate.execute("USE [" + db + "]; DROP USER IF EXISTS " + uid + "; USE [MASTER]");
    }

    public boolean userExists(String db, String uid) {
        boolean b = jdbcTemplate.queryForObject("USE [" + db + "]; SELECT count(name) FROM sys.database_principals WHERE name = ?", new Object[]{uid}, Integer.class) > 0;
        jdbcTemplate.execute("USE [MASTER]");
        return b;
    }
}
