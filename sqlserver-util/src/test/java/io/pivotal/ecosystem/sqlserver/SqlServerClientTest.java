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

import io.pivotal.ecosystem.servicebroker.model.ServiceBinding;
import io.pivotal.ecosystem.servicebroker.model.ServiceInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * "ignore" this test, or set the correct url in the src/test/resources/application.properties
 * file to test connectivity
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
//@Ignore
public class SqlServerClientTest {

    @Autowired
    private SqlServerClient sqlServerClient;

    @Autowired
    private ServiceInstance serviceInstance;

    @Autowired
    private ServiceBinding serviceBinding;

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
    public void testDBLifecycle() {

        assertFalse(sqlServerClient.checkDatabaseExists(TestConfig.SI_ID));

        sqlServerClient.createDatabase(serviceInstance);
        assertTrue(sqlServerClient.checkDatabaseExists(TestConfig.SI_ID));

        assertFalse(sqlServerClient.checkUserExists(TestConfig.USER_ID, TestConfig.SI_ID));
        sqlServerClient.createUserCreds(serviceBinding);
        assertTrue(sqlServerClient.checkUserExists(TestConfig.USER_ID, TestConfig.SI_ID));

        sqlServerClient.deleteUserCreds(TestConfig.USER_ID, TestConfig.SI_ID);
        assertFalse(sqlServerClient.checkUserExists(TestConfig.USER_ID, TestConfig.SI_ID));

        sqlServerClient.deleteDatabase(TestConfig.SI_ID);
        assertFalse(sqlServerClient.checkDatabaseExists(TestConfig.SI_ID));
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