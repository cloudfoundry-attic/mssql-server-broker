/*
 * Copyright (C) 2017-Present Pivotal Software, Inc. All rights reserved.
 * <p>
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * <p>
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * <p>
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.ecosystem.sqlserver;

import io.pivotal.ecosystem.servicebroker.model.LastOperation;
import io.pivotal.ecosystem.servicebroker.model.ServiceBinding;
import io.pivotal.ecosystem.servicebroker.model.ServiceInstance;
import io.pivotal.ecosystem.sqlserver.connector.SqlServerServiceInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * test will create and delete a cluster on a SQL Server. @Ignore tests unless you are doing integration testing and have a test
 * SQL Server available. You will need to edit the application.properties file in src/test/resources to add your SQL Server environment data
 * for this test to work.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlServerBrokerTest {

    @Autowired
    private SqlServerBroker sqlServerBroker;

    @Autowired
    private ServiceInstance serviceInstance;

    @Autowired
    private ServiceBinding serviceBinding;

    @Autowired
    private SqlServerClient sqlServerClient;

    @Before
    public void setUp() {
        reset();
    }

    @After
    public void cleanUp() {
        reset();
    }

    private void reset() {
        if (sqlServerClient.checkDatabaseExists(TestConfig.SI_ID)) {
            sqlServerClient.deleteDatabase(TestConfig.SI_ID);
        }
    }

    @Test
    public void testLifecycle() {
        LastOperation lo = sqlServerBroker.createInstance(serviceInstance);
        assertNotNull(lo);
        assertEquals(LastOperation.SUCCEEDED, lo.getState());
        assertEquals(LastOperation.CREATE, lo.getOperation());

        lo = sqlServerBroker.createBinding(serviceInstance, serviceBinding);
        assertNotNull(lo);
        assertEquals(LastOperation.SUCCEEDED, lo.getState());
        assertEquals(LastOperation.BIND, lo.getOperation());

        Map<String, Object> m = sqlServerBroker.getCredentials(serviceInstance, serviceBinding);
        assertNotNull(m);
        assertEquals("aUser", m.get(SqlServerServiceInfo.USERNAME));
        assertNotNull(SqlServerServiceInfo.PASSWORD);
        assertEquals("jdbc:sqlserver://localhost:1433;user=sa;password=Pass1234!;databaseName=deleteme", m.get(SqlServerServiceInfo.URI));
        assertEquals("deleteme", m.get(SqlServerServiceInfo.DATABASE));

        lo = sqlServerBroker.deleteBinding(serviceInstance, serviceBinding);
        assertNotNull(lo);
        assertEquals(LastOperation.SUCCEEDED, lo.getState());
        assertEquals(LastOperation.UNBIND, lo.getOperation());

        lo = sqlServerBroker.deleteInstance(serviceInstance);
        assertNotNull(lo);
        assertEquals(LastOperation.SUCCEEDED, lo.getState());
        assertEquals(LastOperation.DELETE, lo.getOperation());
    }

    @Test
    public void TestIsAsync() {
        assertFalse(sqlServerBroker.isAsync());
    }
}