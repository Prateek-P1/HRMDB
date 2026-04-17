# HRMS Central Database (Hibernate + SQLite)

This module is the shared Database Layer for all HRMS subsystems.

It uses Hibernate ORM and, by default, a local SQLite database file for development so you can test everything locally (no cloud DB required yet).

## Build

Prerequisites:
- Java 17+ (the project compiles to Java 17)
- Maven **or** the included Maven Wrapper script

Build with wrapper (recommended):
```bash
./mvnw.cmd -DskipTests compile
```

Build with system Maven:
```bash
mvn -DskipTests compile
```

## Local Database Testing (SQLite)

The default connection is configured in `src/main/resources/hibernate.cfg.xml`:
- `hibernate.connection.url = jdbc:sqlite:hrms.db`

So when you run any code that initializes Hibernate, it will create/update a file named `hrms.db` in the project root.

### Option A: CLI Smoke Test

Runs a short connectivity + query check:
```bash
./mvnw.cmd -DskipTests org.codehaus.mojo:exec-maven-plugin:3.5.0:java "-Dexec.mainClass=com.hrms.db.tools.DatabaseSmokeTest"
```

This smoke test also seeds a small amount of deterministic dummy data (`SMOKE_*` rows) if it is missing, so you can visually confirm tables are being populated.

### Option B: GUI Diagnostics Dashboard

Run the main class:
- `com.hrms.db.gui.DBAdminDashboard`

Then click **Run Diagnostics**.

### Inspect the database file

Open `hrms.db` using any SQLite client:
- DB Browser for SQLite
- DBeaver
- SQLite CLI (`sqlite3`)

Tip: To see generated SQL in the console, set `hibernate.show_sql` to `true` in `hibernate.cfg.xml`.

## How subsystem teams use this module

Subsystem teams should:
1. Initialize once via the facade (`HRMSDatabaseFacade.initialize()`)
2. Obtain the factory via `HRMSDatabaseFacade.getRepositories()`
3. Request their specific repository interface from `RepositoryFactory`