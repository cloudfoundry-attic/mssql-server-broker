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

import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableJpaRepositories
public class Config {

    @Bean
    public String dbUrl(Environment env) {
        return env.getProperty("spring.datasource.url");
    }

    @Bean
    public Sqlinator sqlinator(JdbcTemplate jdbcTemplate) {
        return new SqlServerSql(jdbcTemplate);
    }

    @Bean
    public BrokerApiVersion brokerApiVersion() {
        return new BrokerApiVersion();
    }

    @Bean
    public Catalog catalog() {

        return Catalog.builder()
                .serviceDefinitions(
                        ServiceDefinition.builder()
                                .id("SQLServer")
                                .name("SQLServer")
                                .description("SQL Server Broker for Cloud Foundry")
                                .bindable(true)
                                .instancesRetrievable(true)
                                .bindingsRetrievable(true)
                                .planUpdateable(false)
                                .plans(Plan.builder()
                                        .id("SQLServerSharedInstance")
                                        .name("sharedVM")
                                        .description("mvp service")
                                        .bindable(true)
                                        .free(true)
                                        .build())
                                .build())
                .build();
    }

    @Bean
    public ServiceInstanceService serviceInstanceService(SqlServerClient sqlServerClient, ServiceInstanceRepository serviceInstanceRepository) {
        return new InstanceService(sqlServerClient, serviceInstanceRepository);
    }

    @Bean
    public ServiceInstanceBindingService serviceInstanceBindingService(SqlServerClient sqlServerClient, ServiceInstanceRepository serviceInstanceRepository, ServiceBindingRepository serviceBindingRepository) {
        return new BindingService(sqlServerClient, serviceInstanceRepository, serviceBindingRepository);
    }
}