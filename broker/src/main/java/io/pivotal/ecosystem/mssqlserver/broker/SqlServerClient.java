/*
 * Copyright (C) 2017-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.pivotal.ecosystem.mssqlserver.broker;

import io.pivotal.ecosystem.mssqlserver.broker.connector.SqlServerServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
class SqlServerClient {

    private JdbcTemplate jdbcTemplate;
    private String url;

    SqlServerClient(DataSource dataSource, String dbUrl) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.url = dbUrl;
    }

    String createDatabase(ServiceInstance instance) {
        String db = createDbName(instance.getParameters().get(SqlServerServiceInfo.DATABASE));
        jdbcTemplate.execute("use [master]; exec sp_configure 'contained database authentication', 1 reconfigure; CREATE DATABASE [" + db + "]; ALTER DATABASE [" + db + "] SET CONTAINMENT = PARTIAL");
        log.info("Database: " + db + " created successfully...");
        return db;
    }

    void deleteDatabase(String db) {
        jdbcTemplate.execute("ALTER DATABASE " + db + " SET SINGLE_USER WITH ROLLBACK IMMEDIATE; DROP DATABASE " + db);
        log.info("Database: " + db + " deleted successfully...");
    }

    boolean checkDatabaseExists(String db) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM sys.databases WHERE name = ?", new Object[]{db}, Integer.class) > 0;
    }

    String getDbUrl(Object db) {
        if (db == null) {
            return this.url;
        }
        return this.url + ";databaseName=" + db;
    }

    //todo how to protect dbs etc. from bad actors?
    private String getRandomishId() {
        return clean(UUID.randomUUID().toString());
    }

    /**
     * jdbcTemplate helps protect against sql injection, but also clean strings up just in case
     */
    String clean(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    private String checkString(String s) throws ServiceBrokerException {
        if (s.equals(clean(s))) {
            return s;
        }
        throw new ServiceBrokerException("Name must contain only alphanumeric characters.");
    }

    private String createUserId(Object o) {
        if (o != null) {
            return checkString(o.toString());
        }
        return "u" + getRandomishId();
    }

    private String createPassword(Object o) {
        if (o != null) {
            return checkString(o.toString());
        }
        return "P" + getRandomishId();
    }

    private String createDbName(Object o) {
        if (o != null) {
            return checkString(o.toString());
        }
        return "d" + getRandomishId();
    }

    Map<String, Object> createUserCreds(Map<String, String> instanceParameters, Map<String, Object> bindingParameters) {
        Map<String, Object> userCredentials = new HashMap<>();

        userCredentials.put(SqlServerServiceInfo.DATABASE, instanceParameters.get(SqlServerServiceInfo.DATABASE));
        userCredentials.put(SqlServerServiceInfo.URI, getDbUrl(instanceParameters.get(SqlServerServiceInfo.DATABASE)));

        if (bindingParameters != null) {
            //users can optionally pass in uids and passwords
            userCredentials.put(SqlServerServiceInfo.USERNAME, createUserId(bindingParameters.get(SqlServerServiceInfo.USERNAME)));
            userCredentials.put(SqlServerServiceInfo.PASSWORD, createPassword(bindingParameters.get(SqlServerServiceInfo.PASSWORD)));
        } else {
            userCredentials.put(SqlServerServiceInfo.USERNAME, createUserId(null));
            userCredentials.put(SqlServerServiceInfo.PASSWORD, createPassword(null));
        }

        log.debug("creds: " + userCredentials.toString());

        jdbcTemplate.execute("USE [" + userCredentials.get(SqlServerServiceInfo.DATABASE) + "]; CREATE USER ["
                + userCredentials.get(SqlServerServiceInfo.USERNAME)
                + "] WITH PASSWORD='" + userCredentials.get(SqlServerServiceInfo.PASSWORD)
                + "', DEFAULT_SCHEMA=[dbo]; EXEC sp_addrolemember 'db_owner', '"
                + userCredentials.get(SqlServerServiceInfo.USERNAME) + "'");

        log.info("Created user: " + userCredentials.get(SqlServerServiceInfo.USERNAME));
        return userCredentials;
    }

    void deleteUserCreds(String uid, String db) {
        jdbcTemplate.execute("use " + db + "; DROP USER IF EXISTS " + uid);
    }

    boolean checkUserExists(String uid, String db) {
        return jdbcTemplate.queryForObject("use " + db + "; SELECT count(name) FROM sys.database_principals WHERE name = ?", new Object[]{uid}, Integer.class) > 0;
    }
}