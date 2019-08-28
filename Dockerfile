FROM gradle:5.6.0-jdk8
MAINTAINER mateuszbajdak@gmail.com
RUN mkdir app
WORKDIR app
COPY ./build/libs/webflux-rest-0.0.1-SNAPSHOT.jar .
CMD [ "java", "-jar", "webflux-rest-0.0.1-SNAPSHOT.jar"]
#TODO make MongoDB image - run those two with docker-compose
#TODO expose ports