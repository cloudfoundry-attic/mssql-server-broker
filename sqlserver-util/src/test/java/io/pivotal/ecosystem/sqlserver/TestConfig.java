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
import io.pivotal.ecosystem.servicebroker.model.ServiceBinding;
import io.pivotal.ecosystem.servicebroker.model.ServiceInstance;
import io.pivotal.ecosystem.sqlserver.connector.SqlServerServiceInfo;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
class TestConfig {

    @Bean
    public SqlServerClient client(Environment env) {
        return new SqlServerClient(datasource(env), dbUrl(env));
    }

    @Bean
    public DataSource datasource(Environment env) {
        SQLServerConnectionPoolDataSource dataSource = new SQLServerConnectionPoolDataSource();

        dataSource.setURL(dbUrl(env));
        dataSource.setUser(env.getProperty(SqlServerServiceInfo.USER_KEY));
        dataSource.setPassword(env.getProperty(SqlServerServiceInfo.PW_KEY));

        return dataSource;
    }

    @Bean
    public String dbUrl(Environment env) {
        return SqlServerServiceInfo.URI_SCHEME + "://" + env.getProperty(SqlServerServiceInfo.HOST_KEY) + ":" + Integer.parseInt(env.getProperty(SqlServerServiceInfo.PORT_KEY));
    }

    @Bean
    public ServiceBinding serviceBindingWithParms() {
        Map<String, Object> params = new HashMap<>();
        params.put(SqlServerServiceInfo.DATABASE, "testDb");
        params.put(SqlServerServiceInfo.USERNAME, "testUser");
        params.put(SqlServerServiceInfo.PASSWORD, "testPassw0rd");
        CreateServiceInstanceBindingRequest request = new CreateServiceInstanceBindingRequest(null, null, null, null, params);
        return new ServiceBinding(request);
    }

    @Bean
    public ServiceBinding serviceBindingNoParms() {
        return new ServiceBinding(new CreateServiceInstanceBindingRequest());
    }

    @Bean
    public ServiceInstance serviceInstanceWithParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(SqlServerServiceInfo.DATABASE, "testDb");
        return new ServiceInstance(new CreateServiceInstanceRequest(null,null, null, null, params));
    }

    @Bean
    public ServiceInstance serviceInstanceNoParams() {
        return new ServiceInstance(new CreateServiceInstanceRequest());
    }
}