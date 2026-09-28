# SET09803 DevOps Coursework - Group 3
Population Reporting System built with Java 17, Maven, MySQL, and Docker.
## Project Structure & File Guide
* `Dockerfile` - Builds the Java application container image.
* `docker-compose.yml` - Spins up the MySQL database container (`world`) and the app container.
* `pom.xml` - Manages project dependencies (MySQL Connector) and Maven configurations.
* `world.sql` - Sample database schema and dataset executed automatically during database initialization.
* `src/` - Application source code.