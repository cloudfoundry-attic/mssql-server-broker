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
import io.pivotal.ecosystem.servicebroker.model.LastOperation;
import io.pivotal.ecosystem.servicebroker.model.ServiceBinding;
import io.pivotal.ecosystem.servicebroker.model.ServiceInstance;
import io.pivotal.ecosystem.servicebroker.service.DefaultServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
class SqlServerBroker extends DefaultServiceImpl {

    private SqlServerClient client;


    public SqlServerBroker(SqlServerClient client) {
        super();
        this.client = client;
    }

    @Override
    public LastOperation createInstance(ServiceInstance instance) {
        log.info("creating database...");

        //user can optionally specify a db name
        String db = client.createDatabase(instance);
        instance.getParameters().put(SqlServerServiceInfo.DATABASE, db);
        log.info("database: " + db + " created.");

        return new LastOperation(LastOperation.CREATE, LastOperation.SUCCEEDED, "create succeeded.");
    }

    @Override
    public LastOperation deleteInstance(ServiceInstance instance) {
        String db = instance.getParameters().get(SqlServerServiceInfo.DATABASE).toString();
        log.info("deleting database: " + db);
        client.deleteDatabase(db);

        return new LastOperation(LastOperation.DELETE, LastOperation.SUCCEEDED, "delete succeeded.");
    }

    @Override
    public LastOperation updateInstance(ServiceInstance instance) {
        log.info("update not yet implemented");
        return new LastOperation(LastOperation.UPDATE, LastOperation.FAILED, "update not implemented.");
    }

    @Override
    public LastOperation createBinding(ServiceInstance instance, ServiceBinding binding) {
        String db = instance.getParameters().get(SqlServerServiceInfo.DATABASE).toString();
        binding.getParameters().put(SqlServerServiceInfo.DATABASE, db);

        Map<String, String> userCredentials = client.createUserCreds(binding);
        binding.getParameters().put(SqlServerServiceInfo.USERNAME, userCredentials.get(SqlServerServiceInfo.USERNAME));
        binding.getParameters().put(SqlServerServiceInfo.PASSWORD, userCredentials.get(SqlServerServiceInfo.PASSWORD));

        log.info("bound app: " + binding.getAppGuid() + " to database: " + db);

        return new LastOperation(LastOperation.BIND, LastOperation.SUCCEEDED, "bind succeeded.");
    }

    @Override
    public LastOperation deleteBinding(ServiceInstance instance, ServiceBinding binding) {
        log.info("unbinding app: " + binding.getAppGuid() + " from database: " + instance.getParameters().get(SqlServerServiceInfo.DATABASE));
        client.deleteUserCreds(binding.getParameters().get(SqlServerServiceInfo.USERNAME).toString(), binding.getParameters().get(SqlServerServiceInfo.DATABASE).toString());
        return new LastOperation(LastOperation.UNBIND, LastOperation.SUCCEEDED, "unbind succeeded.");
    }

    @Override
    public Map<String, Object> getCredentials(ServiceInstance instance, ServiceBinding binding) {
        log.info("returning credentials.");

        Map<String, Object> m = new HashMap<>();
        m.put(SqlServerServiceInfo.URI, client.getDbUrl(binding.getParameters().get(SqlServerServiceInfo.DATABASE).toString()));

        m.put(SqlServerServiceInfo.USERNAME, binding.getParameters().get(SqlServerServiceInfo.USERNAME));
        m.put(SqlServerServiceInfo.PASSWORD, binding.getParameters().get(SqlServerServiceInfo.PASSWORD));
        m.put(SqlServerServiceInfo.DATABASE, binding.getParameters().get(SqlServerServiceInfo.DATABASE));

        return m;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}