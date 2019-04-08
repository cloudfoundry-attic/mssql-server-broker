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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "ms")
@Data
class MsConfig {

    private String jdbcUrl;
    private String useranme;
    private String password;

    @Bean
    @ConfigurationProperties(prefix = "ms")
    public DataSource dataSource() {
        return DataSourceBuilder.create().url(jdbcUrl).username(useranme).password(password).build();
    }

    @Bean
    public String dbUrl() {
        return jdbcUrl;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SqlServerClient sqlServerClient(Sqlinator sqlinator) {
        return new SqlServerClient(jdbcUrl, sqlinator);
    }

    @Bean
    public Sqlinator sqlinator(JdbcTemplate jdbcTemplate) {
        return new SqlServerSql(jdbcTemplate);
    }
}