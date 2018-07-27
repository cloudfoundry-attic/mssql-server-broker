package io.pivotal.ecosystem.mssqlserver.broker;

import java.util.Map;

public interface Sqlinator {

    void createDb(String db);

    void deleteDb(String db);

    boolean databaseExists(String db);

    void userCreate(Map<String, Object> userCredentials);

    void userDelete(String db, String uid);

    boolean userExists(String db, String uid);
}
