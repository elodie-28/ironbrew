# Ironbrew
Ironbrew is a website where users can manage homebrew content for Ironsworn and Ironsworn: Starforged. They have the opportunity to export that content to the datasworn format to use it in other applications. It uses the Spring Boot framework.

# Installation
You need Java 21 and Docker to launch the application.

Once you've cloned the repository, run the following command to generate the jar.

```bash
mvn clean package -DskipTests
```

Once you've done so, run the following command. This will build a PostgreSQL database, Mailpit to test email sending and the application.

```bash
docker compose -f docker-compose.dev.yml up -d --build
```

Now you can visit http://localhost:8080 and see the application!

If you're developing, you can launch the `postgres-db` and the `mailpit` services and then the application. Don't forget to create a `.env` file based on the `.env.example` file.

```bash
docker compose -f docker-compose.dev.yml up -d --build postgres-db

docker compose -f docker-compose.dev.yml up -d --build mailpit

mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

# Contributing

TODO