# Closest car charger API

This is a Spring Boot application created to show the integration with other public APIs using java.net.http.HttpClient.

The API exposes an endpoint under `/api` that accepts HTTP `GET` requests with mandatory `address` query parameter.
It returns the closest car charger within 10Km from the provided address.
To simplify things, I built and configured the application to work only with addresses in Dublin, but potentially can work in many other countries.

This project uses Java 11, you need to have jdk 11 installed and configured, or you need to run it in a Docker container.

Follow the steps below if you want to run the Spring Boot app in a Docker container.

First build the docker image.
```build docker image
mvn clean install
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t matteo/closest-car-charger .
```

Run docker image in a Docker container
```run Docker
docker run -p 8080:8080 -t matteo/closest-car-charger
```

Debug the Spring Boot app in a Docker container
```debugging the application in a Docker container
$ docker run -e "JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" -p 8080:8080 -p 5005:5005 -t matteo/closest-car-charger
```

Once the application is up and running, we are ready to test the API
```testing the API with curl
$ curl -s http://127.0.0.1:8080/api?address=39+Dorset+Street+Upper | json_pp

{
    "name":"Dublin International Hostel",
    "distance":0.1910025,
    "address":"Dublin International Hostel, 61 Mountjoy Street, Dublin 7",
    "numberOfPoints":1
}
```

[{
"place_id":14193296,
"lat":"53.3554511",
"lon":"-6.2655209",
"display_name":"39, Dorset Street Upper, Rotunda B ED, Dublin 1, Dublin, County Dublin, Leinster, D01 W2C9, Ireland",
"country_code":"ie"
}]