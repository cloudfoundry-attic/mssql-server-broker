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

import io.pivotal.ecosystem.servicebroker.model.ServiceBinding;
import io.pivotal.ecosystem.servicebroker.model.ServiceInstance;
import io.pivotal.ecosystem.servicebroker.service.CatalogService;
import io.pivotal.ecosystem.servicebroker.service.ServiceBindingRepository;
import io.pivotal.ecosystem.servicebroker.service.ServiceInstanceRepository;
import io.pivotal.ecosystem.mssqlserver.broker.connector.SqlServerServiceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"io.pivotal.ecosystem.mssqlserver.broker", "io.pivotal.ecosystem.servicebroker.service"})
class TestConfig {

    static final String SI_ID = "deleteme";
    private static final String SD_ID = "sqlserver";
    private static final String PLAN_ID = "oneNodeCluster";

    private static final String ORG_GUID = "anOrgGuid";
    private static final String SPACE_GUID = "aSpaceGuid";
    private static final String APP_GUID = "anAppGuid";

    private static final String USER_ID = "aUser";

    @MockBean
    public ServiceInstanceRepository serviceInstanceRepository;

    @MockBean
    public ServiceBindingRepository serviceBindingRepository;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Bean
    public String dbUrl() {
        return dbUrl;
    }

    @Bean
    public CatalogService catalogService() {
        return new CatalogService();
    }

    @Bean
    ServiceInstance serviceInstance() {
        Map<String, Object> m = new HashMap<>();
        m.put(SqlServerServiceInfo.DATABASE, SI_ID);

        CreateServiceInstanceRequest req = new CreateServiceInstanceRequest(SD_ID, PLAN_ID, ORG_GUID, SPACE_GUID, m);
        req.withServiceInstanceId(SI_ID);
        req.withAsyncAccepted(true);

        return new ServiceInstance(req);
    }

    @Bean
    ServiceBinding serviceBinding() {
        Map<String, Object> m = new HashMap<>();
        m.put(SqlServerServiceInfo.USERNAME, USER_ID);
        m.put(SqlServerServiceInfo.DATABASE, SI_ID);

        CreateServiceInstanceBindingRequest req = new CreateServiceInstanceBindingRequest(SD_ID, PLAN_ID, APP_GUID, null, m);
        req.withServiceInstanceId(SI_ID);

        return new ServiceBinding(req);
    }
}