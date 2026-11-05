FROM eclipse-temurin:21-alpine
RUN mkdir /app
COPY target/ironbrew-0.0.1-SNAPSHOT.jar app/ironbrew.jar
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser
EXPOSE 8080
ENTRYPOINT ["java","-jar","ironbrew.jar"]