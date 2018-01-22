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
import io.pivotal.ecosystem.mssqlserver.broker.connector.SqlServerServiceInfo;
import org.springframework.cloud.servicebroker.model.BrokerApiVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"io.pivotal.ecosystem.servicebroker", "io.pivotal.cf.servicebroker", "io.pivotal.ecosystem.mssqlserver.broker"})
@Profile("cloud")
public class CloudConfig {

    @Bean
    public DataSource datasource(Environment env) {
        SQLServerConnectionPoolDataSource dataSource = new SQLServerConnectionPoolDataSource();

        dataSource.setURL(dbUrl(env));
        dataSource.setUser(env.getProperty(SqlServerServiceInfo.USER_KEY));
        dataSource.setPassword(env.getProperty(SqlServerServiceInfo.PW_KEY));

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource datasource) {
        return new JdbcTemplate(datasource);
    }

    @Bean
    public String dbUrl(Environment env) {
        return SqlServerServiceInfo.URI_SCHEME + "://" + env.getProperty(SqlServerServiceInfo.HOST_KEY) + ":" + Integer.parseInt(env.getProperty(SqlServerServiceInfo.PORT_KEY));
    }

    @Bean
    public BrokerApiVersion brokerApiVersion() {
        return new BrokerApiVersion();
    }
}