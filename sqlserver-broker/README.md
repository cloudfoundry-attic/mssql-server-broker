# sqlserver-broker
A cloud foundry service broker for Microsoft SqlServer.

## Using sqlserver-broker
1. sqlserver-broker requires a redis datastore. To set this up:
  ```bash
  cf create-service p-redis shared-vm redis-for-sqlserver
  ```
2. Edit the [manifest.yml](https://github.com/cf-platform-eng/ms-sql-server-broker/blob/master/sqlserver-broker/manifest.yml) file as needed for your SqlServer installation.
1. check out and build the project
  ```bash
  git clone git@github.com:cf-platform-eng/ms-sql-server-broker.git
  cd ms-sql-server-broker
  mvn clean install  
  ```
4. Push the broker to cf:
  ```bash
  cf push
  ```
5. Register the broker. The broker makes use of spring-security to protect itself against unauthorized meddling. For more information, please see [here](https://github.com/cloudfoundry-community/spring-boot-cf-service-broker#security).
  ```bash
  cf create-service-broker SqlServer user passwordFromTheBrokerLog https://uri.of.your.broker.app
  ```
6. See the broker:
  ```bash
  cf service-brokers
  Getting service brokers as admin...
  
  name                          url
  ...
  sqlserver-broker              https://your-broker-url
  ...
  ```
7. Enable access to the broker:
  ```bash
  cf service-access
  Getting service access as admin...
  ...
  broker: sqlserver-broker
     service          plan      access   orgs
     SqlServer        sharedVM  none
  ...
  
  
  cf enable-service-access SqlServer
  Enabling access to all plans of service SqlServer for all orgs as admin...


  cf marketplace
  Getting services from marketplace in org your-org / space your-space as you...
  OK
  
  service          plans           description
  SqlServer        sharedVM        SqlServer Broker for Pivotal Cloud Foundry
  ...
  ```
  
## Managing the broker
Please refer to [this documentation](https://docs.cloudfoundry.org/services/managing-service-brokers.html) for general information on how to manage service brokers.

### Creating a service instance
Using the broker to create a service instance results in the creation of a new [contained](https://docs.microsoft.com/en-us/sql/relational-databases/databases/contained-databases) database with a random database name.
  ```bash
  cf create-service SqlServer sharedVM aSqlServerService
  ```
Optionally, users can provide an alphanumeric name for the database as follows:
  ```bash
  cf create-service SqlServer sharedVM aSqlServerService -c '{"db" : "aDatabaseName"}'
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
