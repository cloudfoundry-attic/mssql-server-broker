# client-example
A simple spring boot application that makes use of mssql-server-broker and the connector.

The app connects to SQL Server on startup, initializes a schema, loads some data, and exposes some REST endpoints. Because it makes use of the connector library there is very little configuration needed for it to accomplish this.

## Instructions to run the demo
1. Follow the instructions in the [broker](https://github.com/cf-platform-eng/mssql-server-broker/tree/master/broker) to push and register the broker.
1. Create a cf service instance for the demo using the broker:
  ```bash  
  cf create-service SQLServer sharedVM sql-test 
  ```  
3. Push the demo:
  ```bash
  cd client-example
  cf push
  ```
4. Get the url of the demo app:
  ```bash
  cf a
  Getting apps in org your-org / space your-space as admin...
  OK
  
  name                       requested state   instances   memory   disk   urls
  client-example             started           1/1         512M     1G     client-example.your.domain
  ```
5. Test out the demo in a browser. Try out some endpoints:
```
http://<url of the demo>/quote/
http://<url of the demo>/quote/MSFT
```
