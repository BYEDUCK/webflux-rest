# Demo application based on Spring Framework Guru Udemy course: "Spring Framework 5: Beginner to Guru"
## Technology stack:

- SpringBoot 2.1.7 RELEASE
- Spring WebFlux
- Kotlin 1.2.71
- Java 8
- Reactive MongoDB

### Tests:

- Groovy 2.5
- Spock-Spring 1.3
- Embedded MongoDB

## Short description:

Demo app created for learning purposes. REST API providing basic operations on two entities:
- Category
- Vendor

## Created endpoints:

/api/v1/categories:

- GET / get all categories
   - 200 OK
- GET / with query param "desc" find category by description
   - 204 NO CONTENT if desc is blank
   - 404 NOT FOUND if not found
   - 200 OK if found
- GET /{id} find category by id
    - 204 NO CONTENT if not found
    - 200 OK if found
- POST / add new category
    - 400 BAD REQUEST if description is blank
    - 400 BAD REQUEST if category with given description already exists
    - 201 CREATED if created
- PUT /{id} update category with given id
    - 400 BAD REQUEST if description is blank
    - 404 NOT FOUND if no category found with given id
    - 400 BAD REQUEST if category with given description already exists
    - 200 OK if updated
- DELETE /{id} delete category with given id
    - 200 OK

/api/v1/vendors (little behind):

- GET / get all vendors
    - 200 OK
- GET /{id} find vendor by id
    - 200 OK
- POST / add new vendor
    - 400 BAD REQUEST if last name is blank
    - 201 CREATED if created
    
## Docker

In order to run the app in docker container, run:

`docker-compose up`

And when you want to rebuild images:

`docker-compose up --build`

App will be available on port 48080 on your localhost.

But firstly make sure to build application itself:

`./gradlew bootJar`

## Build status

[![CircleCI](https://circleci.com/gh/BYEDUCK/webflux-rest/tree/circleci.svg?style=svg)](https://circleci.com/gh/BYEDUCK/webflux-rest/tree/circleci)