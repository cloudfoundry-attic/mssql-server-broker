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
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity(name = "instances")
public class ServiceInstance implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String planId;

    private String serviceDefinitionId;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "serviceinstance_parms", joinColumns = @JoinColumn(name = "id"))
    private final Map<String, String> parameters = new HashMap<>();

    public ServiceInstance() {
        super();
    }

    public ServiceInstance(CreateServiceInstanceRequest request) {
        this();
        setId(request.getServiceInstanceId());
        setPlanId(request.getPlanId());
        setServiceDefinitionId(request.getServiceDefinitionId());
        setParameters(request.getParameters());
    }

    public void setParameters(Map<String, Object> params) {
        if (params != null) {
            parameters.clear();
            for (String key : params.keySet()) {
                getParameters().put(key, params.get(key).toString());
            }
        }
    }
}