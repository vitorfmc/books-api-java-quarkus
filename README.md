# Google Books Integration API (Java+Quarkus+GraalVM+AWS Cloud)

**LAST UPDATE:** 12/2020

Follow me: https://www.linkedin.com/in/vitor-cordeiro-921a5697/

---

### 1. Introduction

This project main objective is provide a [CRUD](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) API service that integrates with [Google Books API](https://developers.google.com/books/docs/v1/using) to generate search data for study purposes.

---

### 2. How it works


The application works as a rest-api, which stores data from a book in a DynamoDB.
The integration with Google takes place at the moment of persistence, in which the application searches for additional information on Google to popularize the Book entity.

![Integration Diagram](https://raw.githubusercontent.com/vitorfmc/books-api-java-quarkus/master/help/diagram_001.jpg)


## 3. Technologies

* Quakus Framework (https://quarkus.io/);
* GraalVM;
* Swagger;
* Java 11 (Amazon Distribution);
* AWS: Api Gateway, Lambda, DynamoDB;

## 4. Executing and Deploying

#### 4.1 Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

#### 4.2 Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-google-books-integration-api-1.0.0-SNAPSHOT-runner.jar` file in the `/build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar build/quarkus-google-books-integration-api-1.0.0-SNAPSHOT-runner.jar`.

#### 4.3 Send to AWS

To deploy the application throw cloudformation, use the script: 
```shell script
./infra/deploy-samcli.sh dev
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/quarkus-google-books-integration-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.


## 5. Documentation so far

**5.1 Google books:** 
https://developers.google.com/books/docs/v1/using (Last Visit: 02/12/2020)

**5.2 Postman requests (Import and use in Postman):** 
[postman_collection.json](https://raw.githubusercontent.com/vitorfmc/books-api-java-quarkus/master/help/postman_collection.json)

**5.3 API Documentation:**

**NOTE:** If you are running the API, you can access the documentation using the path: **${APPLICATION_DOMAIN}**/swagger-ui/

POST::
```
curl --location --request POST 'http://localhost:8080/book' \
--header 'Content-Type: application/json' \
--header 'x-api-key: {{x-api-key}}' \
--data-raw '{
    "title":{{title}},
    "libraryCode":{{libraryCode}},
    "catalogingDate":"2020-11-01"
}'
```

PUT:
```
curl --location --request PUT 'http://localhost:8080/book/test' \
--header 'Content-Type: application/json' \
--header 'x-api-key: {{x-api-key}}' \
--data-raw '{
    "title":"Malorie",
    "catalogingDate":"2020-11-01"
}'
```

GET:
```
curl --location -g --request GET 'http://localhost:8080/book?libraryCode={{libraryCode}}' \
--header 'x-api-key: {{x-api-key}}'
```

DELETE:
```
curl --location -g --request DELETE 'http://localhost:8080/book/{{libraryCode}}' \
--header 'x-api-key: {{x-api-key}}'
```

## 6. TO DO:

* Missing some unit and integration tests.

## 7. References:
* Reference for studies: https://quarkus.io/guides/
