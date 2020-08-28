FROM alpine:3.12
RUN apk add --no-cache openjdk11-jre
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","org.example.ClosestCarChargerApplication"]