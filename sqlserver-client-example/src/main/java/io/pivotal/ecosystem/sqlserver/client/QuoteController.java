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

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/quote")
class QuoteController {

    public QuoteController(QuoteRepository quoteRepository) {
        super();
        this.quoteRepository = quoteRepository;
    }

    private QuoteRepository quoteRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Quote> findAllQuotes() {
        List<Quote> q = new ArrayList<>();
        for (Quote quote : quoteRepository.findAll()) {
            q.add(quote);
        }
        return q;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Quote saveQuote(@RequestBody Quote quote) {
        return quoteRepository.save(quote);
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public void deleteQuote(@RequestBody Quote quote) {
        quoteRepository.delete(quote);
    }

    @RequestMapping("/{symbol}")
    public Quote findBySymbol(@PathVariable String symbol) {
        return quoteRepository.findBySymbol(symbol);
    }

}