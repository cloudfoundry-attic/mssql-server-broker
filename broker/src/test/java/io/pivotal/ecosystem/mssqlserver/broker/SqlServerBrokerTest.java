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
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.*;
import org.springframework.cloud.servicebroker.model.instance.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

/**
 * test will create and delete a cluster on a SQL Server. @Ignore tests unless you are doing integration testing and
 * have a test SQL Server available. You will need to edit the application.properties file in src/test/resources to
 * add your SQL Server environment data for this test to work.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlServerBrokerTest {

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private BindingService bindingService;

    @Autowired
    private ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    @Qualifier("custom")
    private CreateServiceInstanceRequest createServiceInstanceCustomRequest;

    @Autowired
    @Qualifier("custom")
    private CreateServiceInstanceBindingRequest createServiceInstanceBindingCustomRequest;

    @Autowired
    private GetLastServiceOperationRequest getLastServiceOperationRequest;

    @Autowired
    private GetServiceInstanceRequest getServiceInstanceRequest;

    @Autowired
    private UpdateServiceInstanceRequest updateServiceInstanceRequest;

    @Autowired
    private SqlServerClient sqlServerClient;

    @Autowired
    private GetServiceInstanceBindingRequest getServiceInstanceBindingRequest;

    @Autowired
    private DeleteServiceInstanceBindingRequest deleteServiceInstanceBindingRequest;

    @Autowired
    private DeleteServiceInstanceRequest deleteServiceInstanceRequest;

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

        Optional<ServiceInstance> si = serviceInstanceRepository.findById(TestConfig.SI_ID);
        if (si.isPresent()) {
            serviceInstanceRepository.delete(si.get());
        }
    }

    @Test
    public void testLifecycleCustom() {
        CreateServiceInstanceResponse csir = instanceService.createServiceInstance(createServiceInstanceCustomRequest);
        assertNotNull(csir);
        assertEquals(OperationState.SUCCEEDED.getValue(), csir.getOperation());
        assertFalse(csir.isInstanceExisted());

        GetServiceInstanceResponse gsir = instanceService.getServiceInstance(getServiceInstanceRequest);
        assertNotNull(gsir);
        assertEquals(1, gsir.getParameters().size());

        try {
            instanceService.getLastOperation(getLastServiceOperationRequest);
        } catch (UnsupportedOperationException e) {
            //expected
        }

        try {
            instanceService.updateServiceInstance(updateServiceInstanceRequest);
        } catch (UnsupportedOperationException e) {
            //expected
        }

        CreateServiceInstanceAppBindingResponse csiabr = (CreateServiceInstanceAppBindingResponse) bindingService.createServiceInstanceBinding(createServiceInstanceBindingCustomRequest);
        assertNotNull(csiabr);
        assertFalse(csiabr.isBindingExisted());
        Map<String, Object> m = csiabr.getCredentials();
        assertNotNull(m);
        assertEquals(4, m.size());

        GetServiceInstanceAppBindingResponse gsiabr = (GetServiceInstanceAppBindingResponse) bindingService.getServiceInstanceBinding(getServiceInstanceBindingRequest);
        assertNotNull(gsiabr);
        Map<String, Object> m2 = gsiabr.getCredentials();
        assertNotNull(m2);
        assertEquals(4, m.size());
        assertEquals("aUser", m2.get(SqlServerServiceInfo.USERNAME));
        assertNotNull(m2.get(SqlServerServiceInfo.URI));
        assertTrue(m2.get(SqlServerServiceInfo.URI).toString().startsWith("jdbc:sqlserver://"));
        assertEquals("deleteme", m2.get(SqlServerServiceInfo.DATABASE));

        bindingService.deleteServiceInstanceBinding(deleteServiceInstanceBindingRequest);
        try {
            bindingService.getServiceInstanceBinding(getServiceInstanceBindingRequest);
        } catch (ServiceInstanceBindingDoesNotExistException e) {
            //expected
        }

        DeleteServiceInstanceResponse dsir = instanceService.deleteServiceInstance(deleteServiceInstanceRequest);
        assertNotNull(dsir);
        assertEquals(OperationState.SUCCEEDED.getValue(), dsir.getOperation());

        try {
            instanceService.getServiceInstance(getServiceInstanceRequest);
        } catch (ServiceInstanceDoesNotExistException e) {
            //expected
        }
    }
}