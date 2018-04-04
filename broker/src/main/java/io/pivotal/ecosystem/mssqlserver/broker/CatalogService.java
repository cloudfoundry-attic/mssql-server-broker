/*
 Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.

 This program and the accompanying materials are made available under
 the terms of the under the Apache License, Version 2.0 (the "License”);
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package io.pivotal.ecosystem.mssqlserver.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CatalogService implements org.springframework.cloud.servicebroker.service.CatalogService {

    @Override
    public Catalog getCatalog() {

        return Catalog.builder()
                .serviceDefinitions(
                        ServiceDefinition.builder()
                                .id("SQLServer")
                                .name("SQLServer")
                                .description("SQL Server Broker for Cloud Foundry")
                                .bindable(true)
                                .planUpdateable(false)
                                .plans(Plan.builder()
                                        .id("SQLServerSharedInstance")
                                        .name("sharedVM")
                                        .description("mvp service")
                                        .bindable(true)
                                        .free(true)
                                        .build())
                                .build()).build();
    }

    @Override
    public ServiceDefinition getServiceDefinition(String id) {
        if (id == null) {
            return null;
        }

        for (ServiceDefinition sd : getCatalog().getServiceDefinitions()) {
            if (sd.getId().equals(id)) {
                return sd;
            }
        }
        return null;
    }
}