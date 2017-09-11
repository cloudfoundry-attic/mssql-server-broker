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

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.Map;

@Slf4j
public class SqlServerServiceInfoCreator extends CloudFoundryServiceInfoCreator<SqlServerServiceInfo> {

    public SqlServerServiceInfoCreator() {
        super(new Tags(SqlServerServiceInfo.URI_SCHEME), SqlServerServiceInfo.URI_SCHEME);
    }

    @Override
    public SqlServerServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        log.info("Returning sqlserver service info: " + serviceData.toString());

        Map<String, Object> credentials = getCredentials(serviceData);
        String id = getId(serviceData);
        String uri = credentials.get(SqlServerServiceInfo.URI).toString();
        String user = credentials.get(SqlServerServiceInfo.USERNAME).toString();
        String password = credentials.get(SqlServerServiceInfo.PASSWORD).toString();

        return new SqlServerServiceInfo(id, user, password, uri);
    }
}