# ms-sql-server-broker
This is a Cloud Foundry service broker for [Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-2016). It currently supports multiple database instances within a sharedVM via a [bosh release](https://github.com/cf-platform-eng/ms-sql-server-bosh-release), or SQL Server instances running external to CF.

This version should be considered a beta product, and has been tested against CF Elastic Runtime v1.9

## Prerequisites
The service broker requires an existing SQL Server install. It has been tested against the docker-based version described [here](https://docs.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker).

## The Modules
### [sqlserver-broker](https://github.com/cf-platform-eng/ms-sql-server-broker/tree/master/sqlserver-broker)
This module contains the broker code. Its [readme](https://github.com/cf-platform-eng/ms-sql-server-broker/blob/master/sqlserver-broker/README.md) contains information on how to build, configure, and deploy the broker.

### [sqlserver-connector](https://github.com/cf-platform-eng/ms-sql-server-broker/tree/master/sqlserver-connector)
This module contains spring-cloud-connector code that can optionally be used by consumers of a brokered service.

### [sqlserver-client-example](https://github.com/cf-platform-eng/ms-sql-server-broker/tree/master/sqlserver-client-example)
A sample project that demos usage of the broker and the connector. See its [readme](https://github.com/cf-platform-eng/ms-sql-server-broker/blob/master/sqlserver-client-example/README.md) for more details.
 
### [sqlserver-util](https://github.com/cf-platform-eng/ms-sql-server-broker/tree/master/sqlserver-util)
Shared utilities for interacting with the SQL Server backend.
