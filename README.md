# E-commerce Pro
E-commerce Pro is a Spring Boot web application which uses a PostgreSQL database and runs in a Docker container. 

The application has a REST API that allows users to perform CRUD (create, read, update, delete) operations on a data model representing a simple e-commerce platform. The RESTful API is defined using OpenAPI 3 specification.  

# Getting started

1. Download and save the project locally then navigate your favorite terminal to that location.

2. Start the application with Docker Compose using:
```shell
docker compose -f "compose.yaml" up -d --build
```

Note: the Docker Daemon must be running to start the application locally.

## Getting Started with Functional Testing the Application
1. Create a Product
```shell
curl -X 'POST' \
  'http://localhost:8080/api/products' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "description": "Some new product",
  "price": 100
}'
```
Sample response body:
```json
{
  "id": 1,
  "description": "A New Product",
  "price": 1000,
  "created": "2024-03-04T04:40:16.139115434",
  "updated": "2024-03-04T04:40:16.139149784"
}
```
2. Create an Order
```shell
curl -X 'POST' \
  'http://localhost:8080/api/orders' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "status": "New"
}'
```
Sample response body:
```json
{
  "id":1,
  "status":"New",
  "created":"2024-03-04T04:42:03.498824764","updated":"2024-03-04T04:42:03.498848624"
}
```
3. Create an Order-Item using the Id's generated from step 1 and 2
```shell
curl -X 'POST' \
  'http://localhost:8080/api/order-items' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "quantity": 1,
  "order": {
    "id": 1
  },
  "product": {
    "id": 1
  }
}'
```
Sample response body:
```json
{
  "id": 1,
  "quantity": 1,
  "created": "2024-03-04T04:43:24.107156482",
  "updated": "2024-03-04T04:43:24.107177802",
  "order": {
    "id": 1,
    "status": "New",
    "created": "2024-03-04T04:42:03.498825",
    "updated": "2024-03-04T04:42:03.498849"
  },
  "product": {
    "id": 1,
    "description": "A New Product",
    "price": 1000,
    "created": "2024-03-04T04:40:16.139115",
    "updated": "2024-03-04T04:40:16.13915"
  }
}
```
4. (Optionally) find Order-Items by Order Id
```shell
curl -X 'GET' \
  'http://localhost:8080/api/order-items/search/order-id/1' \
  -H 'accept: application/json'
```
Response body:
```json
[
  {
    "id": 1,
    "quantity": 1,
    "created": "2024-03-04T04:43:24.107156",
    "updated": "2024-03-04T04:43:24.107178",
    "order": {
      "id": 1,
      "status": "New",
      "created": "2024-03-04T04:42:03.498825",
      "updated": "2024-03-04T04:42:03.498849"
    },
    "product": {
      "id": 1,
      "description": "A New Product",
      "price": 1000,
      "created": "2024-03-04T04:40:16.139115",
      "updated": "2024-03-04T04:40:16.13915"
    }
  }
]
```

API documentation is available for all of these calls and more from 
[Swagger UI](http://localhost:8080/swagger-ui/index.html)

# Project Structure
This project's source code is organized into the following folders under: 

```
src\main\java\com\splawrence\ecommercepro\
```
### config
- Contains configuration for OpenAPI documentation
### controller
  - Contains Spring MVC web controllers that handle REST API calls.
### model
  - Contains entities which represent the data model.
### repository
  - Contains Spring Data JPA repository interfaces which are used for querying the PostgreSQL database.
### exception
  - Contains all user defined Exception classes.

# Testing
Test classes are located here:
```
src\test\java\com\splawrence\ecommercepro\
```

To run tests, run the following:
```
mvn test
```


# Local development
To run E-commerce Pro from source you will need to satisfy the following prerequisites:
1. Java 17 is installed

```shell
java -version
openjdk version "17.0.4.1"
```

2. Maven 3 is installed

```shell
mvn -v
Apache Maven 3.8.5
```

3. Docker is installed

```shell
docker --version
Docker version 24.0.6, build ed223bc
```

Once these are satisfied the application can be built. 

# Building the project with Maven
Before the application can run, it must be built using Maven:

```shell
mvn clean package
```

# Building and running with Docker Compose
Run the following to build the application and start it up
```shell
docker compose -f "compose.yaml" up -d --build
```

# Initial Data Load
An initial data load is ran on container start up if it has not been run previously. 

The scripts for the initial load are here:
[init.sql](init.sql)
# Local debugging in VSCode
To attach the VSCode Debugger to the container instance of the application, create a debugging configuration matching this:

[launch.json](.vscode\launch.json)
```shell
{
    "configurations": [
        {
            "type": "java",
            "name": "Spring Boot-EcommerceProApplication<ecommercepro>",
            "request": "attach",
            "hostName": "localhost",
            "port": 8081
        },
    ]
}
```