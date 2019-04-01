# mssql-server-broker
This is a Cloud Foundry service broker for [Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-2016). It currently supports multiple database instances within a SQL Server instances running external to CF.

This version should be considered a beta product, and has been tested against CF Elastic Runtime v1.9

## Prerequisites
The service broker requires an existing SQL Server install. It has been tested against the docker-based version described [here](https://docs.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker).

JDK 8 Required to compile.

## The Modules
### [broker](https://github.com/cf-platform-eng/mssql-server-broker/tree/master/broker)
This module contains the broker code. Its [readme](https://github.com/cf-platform-eng/mssql-server-broker/blob/master/broker/README.md) contains information on how to build, configure, and deploy the broker.

### [connector](https://github.com/cf-platform-eng/mssql-server-broker/tree/master/connector)
This module contains spring-cloud-connector code that can optionally be used by consumers of a brokered service.

### [client-example](https://github.com/cf-platform-eng/mssql-server-broker/tree/master/client-example)
A sample project that demos usage of the broker and the connector. See its [readme](https://github.com/cf-platform-eng/mssql-server-broker/blob/master/client-example/README.md) for more details.
 
### [util](https://github.com/cf-platform-eng/mssql-server-broker/tree/master/util)
Shared utilities for interacting with the SQL Server backend.
