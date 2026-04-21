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

## Distributing this module to other teams

If another team is integrating by copying a JAR manually (not using Maven/Gradle dependency resolution), they must also have the runtime dependencies on their classpath. Otherwise they may see errors like:
- `java.lang.NoClassDefFoundError: jakarta/transaction/SystemException`
- `java.lang.NoClassDefFoundError: jakarta/xml/bind/JAXBException`

Generate distribution artifacts:
```bash
./mvnw.cmd -DskipTests clean package
```

After packaging, you will have:
- `target/hrms-database-1.0-SNAPSHOT.jar` (module classes only)
- `target/hrms-database-1.0-SNAPSHOT-all.jar` (fat JAR with runtime dependencies included)
- `target/lib/` (runtime dependency JARs)

Recommended options to share:
- **Single-file option:** share `target/hrms-database-1.0-SNAPSHOT-all.jar`
- **Folder option:** share `target/hrms-database-1.0-SNAPSHOT.jar` + the entire `target/lib/` folder

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

## HTTP Gateway For Other Teams

This repo now also includes an embedded HTTP gateway so other teams can call the database layer over a port instead of linking the JAR directly.

Main class:
- `com.hrms.db.server.HRMSHttpGatewayServer`

Default port:
- `18080`

Optional environment variable:
- `HRMS_HTTP_PORT`

Run with Maven Wrapper:
```bash
./mvnw.cmd -DskipTests org.codehaus.mojo:exec-maven-plugin:3.5.0:java "-Dexec.mainClass=com.hrms.db.server.HRMSHttpGatewayServer"
```

Useful endpoints:
- `GET /api/health`
- `GET /api/dashboard`
- `GET /api/repositories`
- `GET /api/errors`
- `POST /api/invoke`

The root URL `/` serves a small browser-based admin frontend for health checks, repository discovery, and manual method invocation.

Example invoke payload:
```json
{
  "repository": "payroll",
  "method": "fetchEmployeeData",
  "args": ["SMOKE_EMP_001", "2026-04"]
}
```
