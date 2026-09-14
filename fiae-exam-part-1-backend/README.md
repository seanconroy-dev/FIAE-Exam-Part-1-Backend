# fiae-exam-part-1-backend

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Postgres setup for persistent whitelist users

Whitelist users persist only when the backend runs against a persistent database (PostgreSQL in production profile).

### Is Postgres free?

Yes, you can start on free tiers:
- Neon (recommended starter option)
- Supabase
- Render Postgres

Free tiers usually include limits (storage, monthly compute hours, and auto-sleep).

### Hosted setup steps (Neon/Supabase/Render)

1. Create a Postgres database in your provider dashboard.
2. Copy host, port, database name, username, and password.
3. Set backend environment variables:
   - `DB_URL=jdbc:postgresql://<HOST>:5432/<DATABASE>`
   - `DB_USER=<USERNAME>`
   - `QUARKUS_DATASOURCE_PASSWORD=<PASSWORD>`
   - `ADMIN_TOKEN=<YOUR_ADMIN_TOKEN>`
4. Run backend in prod profile:
   - `./mvnw quarkus:dev -Dquarkus.profile=prod`
   - or package and run with `-Dquarkus.profile=prod`
5. Flyway runs automatically on startup (`quarkus.flyway.migrate-at-start=true`) and creates required tables.

### GitHub Pages frontend

If your frontend is hosted on GitHub Pages, keep that origin in CORS:

```properties
quarkus.http.cors.origins=https://seanconroy-dev.github.io,http://localhost:4321
```

If the Pages URL changes, add the new exact origin there.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/fiae-exam-part-1-backend-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
