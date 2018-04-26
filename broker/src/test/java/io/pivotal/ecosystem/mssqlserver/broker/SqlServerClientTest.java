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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

/**
 * "ignore" this test, or set the correct url in the src/test/resources/application.properties
 * file to test connectivity
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlServerClientTest {

    @Autowired
    private SqlServerClient sqlServerClient;

    @Autowired
    @Qualifier("custom")
    private CreateServiceInstanceRequest createServiceInstanceCustomRequest;

    @Autowired
    @Qualifier("default")
    private CreateServiceInstanceRequest createServiceInstanceDefaultRequest;

    @Autowired
    @Qualifier("custom")
    private CreateServiceInstanceBindingRequest createServiceInstanceBindingCustomRequest;

    @Autowired
    private String dbUrl;

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
    public void testDBCustomLifecycle() {

        assertFalse(sqlServerClient.checkDatabaseExists(TestConfig.SI_ID));

        ServiceInstance si = new ServiceInstance(createServiceInstanceCustomRequest);

        sqlServerClient.createDatabase(si);
        assertTrue(sqlServerClient.checkDatabaseExists(TestConfig.SI_ID));

        assertFalse(sqlServerClient.checkUserExists(TestConfig.USER_ID, TestConfig.SI_ID));

        ServiceBinding sb = new ServiceBinding(createServiceInstanceBindingCustomRequest);

        Map<String, Object> bindingParameters = new HashMap<>();
        for (String s : sb.getParameters().keySet()) {
            bindingParameters.put(s, sb.getParameters().get(s));
        }

        Map<String, String> instanceParameters = new HashMap<>();
        instanceParameters.put(SqlServerServiceInfo.DATABASE, TestConfig.SI_ID);

        //todo deal with all of this back and forth
        sqlServerClient.createUserCreds(instanceParameters, bindingParameters);
        assertTrue(sqlServerClient.checkUserExists(TestConfig.USER_ID, TestConfig.SI_ID));

        sqlServerClient.deleteUserCreds(TestConfig.USER_ID, TestConfig.SI_ID);
        assertFalse(sqlServerClient.checkUserExists(TestConfig.USER_ID, TestConfig.SI_ID));

        sqlServerClient.deleteDatabase(TestConfig.SI_ID);
        assertFalse(sqlServerClient.checkDatabaseExists(TestConfig.SI_ID));
    }

    @Test
    public void testDBDefaultLifecycle() {
        String db = null;
        try {
            ServiceInstance si = new ServiceInstance(createServiceInstanceDefaultRequest);

            db = sqlServerClient.createDatabase(si);
            assertNotNull(db);

            assertTrue(sqlServerClient.checkDatabaseExists(db));
            assertFalse(sqlServerClient.checkUserExists(TestConfig.USER_ID, db));

            //todo deal with all of this back and forth
            si.getParameters().put(SqlServerServiceInfo.DATABASE, db);
            Map<String, Object> creds = sqlServerClient.createUserCreds(si.getParameters(), null);
            assertNotNull(creds);
            String user = creds.get(SqlServerServiceInfo.USERNAME).toString();
            assertNotNull(user);

            assertTrue(sqlServerClient.checkUserExists(user, db));

            sqlServerClient.deleteUserCreds(user, db);
            assertFalse(sqlServerClient.checkUserExists(user, db));

            sqlServerClient.deleteDatabase(db);
            assertFalse(sqlServerClient.checkDatabaseExists(TestConfig.SI_ID));
        } finally {
            if (db != null && sqlServerClient.checkDatabaseExists(db)) {
                sqlServerClient.deleteDatabase(db);
            }
        }
    }


    @Test
    public void testGetDbUrl() {
        assertEquals(dbUrl + ";databaseName=foo", sqlServerClient.getDbUrl("foo"));
        assertEquals(dbUrl, sqlServerClient.getDbUrl(null));
    }

    @Test
    public void testClean() {
        assertEquals("foo", sqlServerClient.clean("foo"));
        assertEquals("foobar", sqlServerClient.clean("&^{ /\ffoo **// b;a} %r$#"));
        assertEquals("", sqlServerClient.clean(""));
        assertEquals("", sqlServerClient.clean(null));
    }
}