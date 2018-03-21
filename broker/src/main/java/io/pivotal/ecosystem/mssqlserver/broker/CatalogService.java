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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class CatalogService implements org.springframework.cloud.servicebroker.service.CatalogService {

    private Catalog catalog;

    @Override
    public Catalog getCatalog() {
        if (catalog != null) {
            return catalog;
        }

        try {
            return loadCatalog();
        } catch (Exception e) {
            log.error("Error retrieving catalog.", e);
            throw new ServiceBrokerException("Unable to retrieve catalog.", e);
        }
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

    private Catalog loadCatalog() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URI u = new ClassPathResource("catalog.json").getURI();
        String s = new String(Files.readAllBytes(Paths.get(u)));
        catalog = mapper.readValue(s, Catalog.class);
        return catalog;
    }
}