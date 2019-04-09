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
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BindingService implements ServiceInstanceBindingService {

    private SqlServerClient sqlServerClient;
    private ServiceInstanceRepository serviceInstanceRepository;
    private ServiceBindingRepository serviceBindingRepository;

    public BindingService(SqlServerClient sqlServerClient, ServiceInstanceRepository serviceInstanceRepository, ServiceBindingRepository serviceBindingRepository) {
        super();
        this.sqlServerClient = sqlServerClient;
        this.serviceInstanceRepository = serviceInstanceRepository;
        this.serviceBindingRepository = serviceBindingRepository;
    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest createServiceInstanceBindingRequest) {
        Optional<ServiceInstance> sio = serviceInstanceRepository.findById(createServiceInstanceBindingRequest.getServiceInstanceId());
        if (!sio.isPresent()) {
            throw new ServiceInstanceDoesNotExistException(createServiceInstanceBindingRequest.getServiceInstanceId());
        }

        ServiceInstance serviceInstance = sio.get();
        ServiceBinding serviceBinding = new ServiceBinding(createServiceInstanceBindingRequest);
        serviceBinding.setCredentials(sqlServerClient.createUserCreds(serviceInstance.getParameters(), createServiceInstanceBindingRequest.getParameters()));
        serviceBindingRepository.save(serviceBinding);

        Map<String, Object> creds = new HashMap<>();
        for (String s : serviceBinding.getCredentials().keySet()) {
            creds.put(s, serviceBinding.getCredentials().get(s));
        }

        return CreateServiceInstanceAppBindingResponse.builder()
                .credentials(creds)
                .build();
    }

    @Override
    public GetServiceInstanceBindingResponse getServiceInstanceBinding(GetServiceInstanceBindingRequest getServiceInstanceBindingRequest) {
        Optional<ServiceBinding> sb = serviceBindingRepository.findById(getServiceInstanceBindingRequest.getBindingId());
        if (!sb.isPresent()) {
            throw new ServiceInstanceBindingDoesNotExistException(getServiceInstanceBindingRequest.getBindingId());
        }

        Map<String, Object> creds = new HashMap<>();
        for (String s : sb.get().getCredentials().keySet()) {
            creds.put(s, sb.get().getCredentials().get(s));
        }

        Map<String, Object> parms = new HashMap<>();
        for (String s : sb.get().getParameters().keySet()) {
            creds.put(s, sb.get().getParameters().get(s));
        }

        return GetServiceInstanceAppBindingResponse.builder()
                .credentials(creds)
                .parameters(parms)
                .build();
    }

    @Override
    public DeleteServiceInstanceBindingResponse deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest deleteServiceInstanceBindingRequest) {
        Optional<ServiceInstance> si = serviceInstanceRepository.findById(deleteServiceInstanceBindingRequest.getServiceInstanceId());
        if (!si.isPresent()) {
            throw new ServiceInstanceDoesNotExistException(deleteServiceInstanceBindingRequest.getServiceInstanceId());
        }

        Optional<ServiceBinding> sb = serviceBindingRepository.findById(deleteServiceInstanceBindingRequest.getBindingId());
        if (!sb.isPresent()) {
            throw new ServiceInstanceBindingDoesNotExistException(deleteServiceInstanceBindingRequest.getBindingId());
        }

        log.info("deleting binding: " + deleteServiceInstanceBindingRequest.getBindingId() + " for service instance: " + deleteServiceInstanceBindingRequest.getServiceInstanceId());
        sqlServerClient.deleteUserCreds(sb.get().getCredentials().get(SqlServerServiceInfo.USERNAME), si.get().getParameters().get(SqlServerServiceInfo.DATABASE));
        serviceBindingRepository.delete(sb.get());

        return DeleteServiceInstanceBindingResponse.builder()
                .build();
    }
}