# ZD128958 - test for getUpdateCount()

This program is a very simple test for the Arrow JDBC driver. Remember to update the pom.xml for the particular version of the Arrow Flight SQL JDBC driver that you want. 

## Set up:
We use the Voltron Data test fixture for Arrow Flight Server - see this [link](https://voltrondata.com/blog/running-arrow-flight-server-querying-data-jdbc-adbc).

Start the Voltron Data docker container: 
```shell
docker run --name flight-sql \
           --detach \
           --rm \
           --tty \
           --init \
           --publish 31337:31337 \
           --env TLS_ENABLED="1" \
           --env FLIGHT_PASSWORD="flight_password" \
           --env PRINT_QUERIES="1" \
           --pull missing \
           voltrondata/flight-sql:latest
```
Take the program into your IDE - or build this program via the usual process:
```Shell
git clone https://github.com/rcprcp/ZD128958.git
cd ZD128958
mvn clean package
```
The pom.xml file is set up to build a runnable jar file. 

There are a no command line options; everything is hard coded into the connection string and literals in the program.

Then run it: 
```Shell
export JDK_JAVA_OPTIONS=--add-opens=java.base/java.util=ALL-UNNAMED
java -jar target/ZD128958-1.0-SNAPSHOT-jar-with-dependencies.jar
```