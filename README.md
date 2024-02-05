# Properties Client

## How to run

1. Install JDK 17+ and `mvn` (Maven).
2. Create client.properties file:
```shell
inputdir=/var/tmp/clientinput
apiurl=http://localhost:8888/api/v1/file
regex=^[a-zA-Z0-9]+$
```
3. Go to repository root.
4. Run:
```shell
 mvn clean install && java -jar target/propsclient-1.0-SNAPSHOT.jar /path/to/client.properties
```

### Note

This was tested on Linux.
