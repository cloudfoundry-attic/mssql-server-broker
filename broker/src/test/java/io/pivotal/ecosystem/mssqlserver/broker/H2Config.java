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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
class H2Config {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    private ServiceBindingRepository serviceBindingRepository;

    @Bean
    public Sqlinator sqlinator(JdbcTemplate jdbcTemplate) {
        return new H2Sql(jdbcTemplate);
    }

    @Bean
    public SqlServerClient sqlServerClient(Sqlinator sqlinator) {
        return new SqlServerClient(dbUrl, sqlinator);
    }

    @Bean
    public InstanceService instanceService(SqlServerClient sqlServerClient) {
        return new InstanceService(sqlServerClient, serviceInstanceRepository);
    }

    @Bean
    public BindingService bindingService(SqlServerClient sqlServerClient) {
        return new BindingService(sqlServerClient, serviceInstanceRepository, serviceBindingRepository);
    }

}