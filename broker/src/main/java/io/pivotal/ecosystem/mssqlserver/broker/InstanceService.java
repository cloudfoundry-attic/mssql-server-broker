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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.model.instance.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class InstanceService implements ServiceInstanceService {

    private SqlServerClient sqlServerClient;
    private ServiceInstanceRepository serviceInstanceRepository;

    public InstanceService(SqlServerClient sqlServerClient, ServiceInstanceRepository serviceInstanceRepository) {
        super();
        this.sqlServerClient = sqlServerClient;
        this.serviceInstanceRepository = serviceInstanceRepository;
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest createServiceInstanceRequest) {
        if (serviceInstanceRepository.existsById(createServiceInstanceRequest.getServiceInstanceId())) {
            throw new ServiceInstanceExistsException(createServiceInstanceRequest.getServiceInstanceId(), createServiceInstanceRequest.getServiceDefinitionId());
        }

        ServiceInstance si = new ServiceInstance(createServiceInstanceRequest);

        log.info("creating database...");

        //user can optionally specify a db name
        String db = sqlServerClient.createDatabase(si);
        si.getParameters().put(SqlServerServiceInfo.DATABASE, db);
        log.info("database: " + db + " created.");

        log.info("saving service instance to repo: " + si.getId());
        serviceInstanceRepository.save(si);

        log.info("registered service instance: " + createServiceInstanceRequest.getServiceInstanceId());
        return CreateServiceInstanceResponse.builder()
                .operation(OperationState.SUCCEEDED.getValue())
                .build();
    }

    @Override
    public GetServiceInstanceResponse getServiceInstance(GetServiceInstanceRequest request) {
        log.info("retrieving service instance...");
        Optional<ServiceInstance> si = serviceInstanceRepository.findById(request.getServiceInstanceId());
        if (!si.isPresent()) {
            throw new ServiceInstanceDoesNotExistException(request.getServiceInstanceId());
        }

        Map<String, Object> m = new HashMap<>();
        for (String s : si.get().getParameters().keySet()) {
            m.put(s, si.get().getParameters().get(s));
        }

        return GetServiceInstanceResponse.builder()
                .serviceDefinitionId(si.get().getServiceDefinitionId())
                .planId(si.get().getPlanId())
                .parameters(m)
                .build();
    }

    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest deleteServiceInstanceRequest) {
        Optional<ServiceInstance> si = serviceInstanceRepository.findById(deleteServiceInstanceRequest.getServiceInstanceId());
        if (!si.isPresent()) {
            throw new ServiceInstanceDoesNotExistException(deleteServiceInstanceRequest.getServiceInstanceId());
        }

        String db = si.get().getParameters().get(SqlServerServiceInfo.DATABASE);

        log.info("deleting database: " + db);
        sqlServerClient.deleteDatabase(db);
        serviceInstanceRepository.delete(si.get());

        return DeleteServiceInstanceResponse.builder()
                .operation(OperationState.SUCCEEDED.getValue())
                .build();
    }
}