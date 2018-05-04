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

package io.pivotal.ecosystem.mssqlserver.broker;

import lombok.Data;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity(name = "bindings")
public class ServiceBinding implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String serviceInstanceId;

    private String planId;

    private String serviceDefinitionId;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "binding_parms", joinColumns = @JoinColumn(name = "id"))
    private final Map<String, String> parameters = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "binding_creds", joinColumns = @JoinColumn(name = "id"))
    private final Map<String, String> credentials = new HashMap<>();

    public ServiceBinding() {
        super();
    }

    public ServiceBinding(CreateServiceInstanceBindingRequest request) {
        this();
        setId(request.getBindingId());
        setServiceDefinitionId(request.getServiceInstanceId());
        setPlanId(request.getPlanId());
        setServiceInstanceId(request.getServiceInstanceId());

        setParameters(request.getParameters());
    }

    //todo can we not?
    public void setParameters(Map<String, Object> params) {
        if (params != null) {
            parameters.clear();
            for (String key : params.keySet()) {
                getParameters().put(key, params.get(key).toString());
            }
        }
    }

    public void setCredentials(Map<String, Object> params) {
        if (params != null) {
            credentials.clear();
            for (String key : params.keySet()) {
                getCredentials().put(key, params.get(key).toString());
            }
        }
    }
}