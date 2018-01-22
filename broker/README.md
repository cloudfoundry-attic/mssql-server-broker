# broker
A cloud foundry service broker for Microsoft SQL Server.

## Using ms-sql-server-broker
1. mssql-server-broker requires a redis datastore. To set this up:
  ```bash
  cf create-service p-redis shared-vm redis-for-mssql-server
  ```
2. The broker makes use of spring-security to protect itself against unauthorized meddling. To set its username and password edit the [manifest.yml](https://github.com/cf-platform-eng/mssql-server-broker/blob/master/broker/manifest.yml) file as needed for your CF install (you probably don't want to check this in!).

1. check out and build the project
  ```bash
  git clone git@github.com:cf-platform-eng/mssql-server-broker.git
  cd mssql-server-broker
  mvn clean install  
  ```
4. Push the broker to cf:
  ```bash
  cd broker
  cf push
  ```
5. Register the broker. The broker makes use of spring-security to protect itself against unauthorized meddling. For more information, please see [here](https://github.com/cloudfoundry-community/spring-boot-cf-service-broker#security).
  ```bash
  cf create-service-broker SQLServer userNameFromManifestFile passwordFromManifestFile https://uri.of.your.broker.app
  ```
6. See the broker:
  ```bash
  cf service-brokers
  Getting service brokers as admin...
  
  name                          url
  SQLServer                     https://your-broker-url
  ...
  ```
7. Enable access to the broker:
  ```bash
  cf service-access
  Getting service access as admin...
  ...
  broker: mssql-server-broker
     service          plan      access   orgs
     SQLServer        sharedVM  none
  ...
  
  
  cf enable-service-access SQLServer
  Enabling access to all plans of service SQLServer for all orgs as admin...


  cf marketplace
  Getting services from marketplace in org your-org / space your-space as you...
  OK
  
  service          plans           description
  SQLServer        sharedVM        SQL Server Broker for Cloud Foundry
  ...
  ```
  
## Managing the broker
Please refer to [this documentation](https://docs.cloudfoundry.org/services/managing-service-brokers.html) for general information on how to manage service brokers.

### Creating a service instance
Using the broker to create a service instance results in the creation of a new [contained](https://docs.microsoft.com/en-us/sql/relational-databases/databases/contained-databases) database with a random database name.
  ```bash
  cf create-service SQLServer sharedVM aSqlServerService
  ```
Optionally, users can provide an alphanumeric name for the database as follows:
  ```bash
  cf create-service SQLServer sharedVM aSqlServerService -c '{"db" : "aDatabaseName"}'
  ```
### Deleting a service instance
Deleting a service instance results in the immediate deletion of the corresponding database.
  ```bash
  cf delete-service aSqlServerService
  ```
### Binding to a service
Once a service instance (contained database) has been created, users can bind application to it in the usual fashion. The binding process includes the creation of random database-level credentials that are tied to the binding.
  ```bash
  cf bind-service anApplicartion aSqlServerService
  ```
Optionally, users can provide an alphanumeric user names and passwords for the binding as follows:
  ```bash
  cf bind-service anApplicartion aSqlServerService -c '{"uid" : "aUserId", "pw" : "aValidSqlServerPassword"}'
  ```
