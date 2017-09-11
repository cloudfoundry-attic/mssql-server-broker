/*
 * Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.
 * <p>
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.ecosystem.sqlserver;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import io.pivotal.ecosystem.servicebroker.model.ServiceBinding;
import io.pivotal.ecosystem.servicebroker.model.ServiceInstance;
import io.pivotal.ecosystem.sqlserver.connector.SqlServerServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Slf4j
public class SqlServerClientTest {

    @Autowired
    private SqlServerClient client;

    @Autowired
    private ServiceBinding serviceBindingWithParms;

    @Autowired
    private ServiceBinding serviceBindingNoParms;

    @Autowired
    private ServiceInstance serviceInstanceWithParams;

    @Autowired
    private ServiceInstance serviceInstanceNoParams;

    @Test
    public void testCreateAndDeleteWithParms() throws SQLException {
        testCreateAndDeleteDatabase(serviceInstanceWithParams, serviceBindingWithParms);
    }

    @Test
    public void testCreateAndDeleteNoParms() throws SQLException {
        testCreateAndDeleteDatabase(serviceInstanceNoParams, serviceBindingNoParms);
    }

    private void testCreateAndDeleteDatabase(ServiceInstance serviceInstance, ServiceBinding binding) throws SQLException {
        String db = client.createDatabase(serviceInstance);
        assertNotNull(db);
        binding.getParameters().put(SqlServerServiceInfo.DATABASE, db);

        Map<String, String> userCredentials = client.createUserCreds(binding);

        String uid = userCredentials.get(SqlServerServiceInfo.USERNAME);
        assertNotNull(uid);

        String pw = userCredentials.get(SqlServerServiceInfo.PASSWORD);
        assertNotNull(pw);

        assertEquals(db, userCredentials.get(SqlServerServiceInfo.DATABASE));

        SQLServerConnectionPoolDataSource dataSource = new SQLServerConnectionPoolDataSource();
        String url = client.getDbUrl(db.toString());
        dataSource.setURL(url);
        dataSource.setUser(userCredentials.get(SqlServerServiceInfo.USERNAME));
        dataSource.setPassword(userCredentials.get(SqlServerServiceInfo.PASSWORD));

        Connection c = dataSource.getConnection();
        assertNotNull(c);
        c.close();

        assertTrue(client.checkUserExists(uid, db.toString()));
        client.deleteUserCreds(uid, db.toString());
        assertFalse(client.checkUserExists(uid, db.toString()));

        client.deleteDatabase(db.toString());
        assertFalse(client.checkDatabaseExists(db.toString()));
    }

    @Test
    public void testDbExists() {
        assertTrue(client.checkDatabaseExists("master"));
        assertFalse(client.checkDatabaseExists("kjfhskfjd"));
    }


    @Test
    public void testClean() {
        assertEquals("dsfuwe98fy2Yd9y2", client.clean("dsfuwe98fy2Yd9y2"));
        assertEquals("dsfuwe98fy2Yd9y2", client.clean("dsfuw  e98f& ()$%^$ <> y2Yd9y2"));
        assertEquals("", client.clean(""));
        assertEquals("", client.clean("        "));
        assertEquals("", client.clean(null));
        assertEquals("dfjhgkjhfdgtjhowefiTT", client.clean("dfjhgkjhfd<>&gt;// \\ jhowefi TT "));
    }
}