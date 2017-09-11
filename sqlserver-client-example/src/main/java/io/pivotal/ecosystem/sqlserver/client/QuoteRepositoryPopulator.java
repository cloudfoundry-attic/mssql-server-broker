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

package io.pivotal.ecosystem.sqlserver.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuoteRepositoryPopulator implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private QuoteRepository quoteRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (quoteRepository != null && quoteRepository.count() == 0) {
            populate(quoteRepository);
        }
    }

    private void populate(QuoteRepository repository) {
        try {
            URI u = new ClassPathResource("quotes.json").getURI();
            byte[] jsonData = Files.readAllBytes(Paths.get(u));

            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<Quote> q = objectMapper.readValue(jsonData,
                    new TypeReference<List<Quote>>() {
                    });
            repository.save(q);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}