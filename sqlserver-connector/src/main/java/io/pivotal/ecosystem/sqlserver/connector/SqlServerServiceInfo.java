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

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cloud.service.ServiceInfo;

@Data
@AllArgsConstructor
public class SqlServerServiceInfo implements ServiceInfo {

    //scheme used by connectors to tag this to sql server
    public static final String URI_SCHEME = "jdbc:sqlserver";

    //keys used to store metadata for service bindings
    public static final String USERNAME = "uid";
    public static final String PASSWORD = "pw";
    public static final String URI = "uri";
    public static final String DATABASE = "db";
    public static final String HOSTNAME = "hostname";
    public static final String PORT = "port";

    //keys used to pull connection information out of env
    public static final String HOST_KEY = "SQL_HOST";
    public static final String PORT_KEY = "SQL_PORT";
    public static final String USER_KEY = "SQLSERVER_USERNAME";
    public static final String PW_KEY = "SQLSERVER_PASSWORD";

    private String id;
    private String user;
    private String password;
    private String uri;
}
