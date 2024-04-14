FROM amazoncorretto:21

WORKDIR ./app

COPY ./target/S3WebServices-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD java -jar S3WebServices-0.0.1-SNAPSHOT.jar