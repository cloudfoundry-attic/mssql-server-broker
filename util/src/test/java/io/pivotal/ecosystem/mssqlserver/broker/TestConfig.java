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

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import io.pivotal.ecosystem.servicebroker.model.ServiceBinding;
import io.pivotal.ecosystem.servicebroker.model.ServiceInstance;
import io.pivotal.ecosystem.mssqlserver.broker.connector.SqlServerServiceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
class TestConfig {

    static final String SI_ID = "deleteme";
    static final String SD_ID = "sqlserver";
    static final String PLAN_ID = "oneNodeCluster";

    private static final String ORG_GUID = "anOrgGuid";
    private static final String SPACE_GUID = "aSpaceGuid";
    private static final String APP_GUID = "anAppGuid";

    static final String USER_ID = "aUser";

    @Bean
    DataSource dataSource() {
        SQLServerConnectionPoolDataSource dataSource = new SQLServerConnectionPoolDataSource();
        dataSource.setURL(dbUrl);
        return dataSource;
    }

    @Bean
    SqlServerClient sqlServerClient(DataSource ds, String dbUrl) {
        return new SqlServerClient(ds, dbUrl);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Bean
    public String dbUrl() {
        return dbUrl;
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