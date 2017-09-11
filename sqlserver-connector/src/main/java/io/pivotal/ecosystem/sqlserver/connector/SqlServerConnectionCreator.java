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

package io.pivotal.ecosystem.sqlserver.connector;

import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;

import javax.sql.DataSource;

@Slf4j
public class SqlServerConnectionCreator extends AbstractServiceConnectorCreator<DataSource, SqlServerServiceInfo> {

    @Override
    public DataSource create(SqlServerServiceInfo serviceInfo, ServiceConnectorConfig serviceConnectorConfig) {
        log.info("creating sqlservice repo wth service info: " + serviceInfo);

        SQLServerConnectionPoolDataSource dataSource = new SQLServerConnectionPoolDataSource();
        dataSource.setURL(serviceInfo.getUri());
        dataSource.setUser(serviceInfo.getUser());
        dataSource.setPassword(serviceInfo.getPassword());

        return dataSource;
    }
}