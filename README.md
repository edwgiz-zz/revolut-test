# revolut-test

Project based on 
- Jetty as embedded servlet container,
- Jersey as REST framework,
- EclipseLink as JPA implementation
- Debry as in-memory database

Build system is Maven. Below command tests application and creates all-in-one jar:
> mvn package

The jar runs as standalone application on 8080 port:
> java -jar target/revolut-test-1.0-SNAPSHOT.jar


See for REST API details a test code at https://github.com/edwgiz/revolut-test/blob/master/src/test/java/ru/edwgiz/test/rest/framework/WebAppTest.java
