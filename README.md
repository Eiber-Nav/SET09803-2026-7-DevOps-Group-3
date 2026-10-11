# Badges
[![LICENSE](https://img.shields.io/github/license/Eiber-Nav/SET09803-2026-7-DevOps-Group-3.svg?style=flat-square)](https://github.com/Eiber-Nav/SET09803-2026-7-DevOps-Group-3/blob/master/LICENSE)
[![Releases](https://img.shields.io/github/release/Eiber-Nav/SET09803-2026-7-DevOps-Group-3/all.svg?style=flat-square)](https://github.com/Eiber-Nav/SET09803-2026-7-DevOps-Group-3/releases)
# SET09803 DevOps Coursework - Group 3
Population Reporting System built with Java 17, Maven, MySQL, and Docker.
## Project Structure & File Guide
* **`Dockerfile`** - Builds the Java application container image.
* **`docker-compose.yml`** - Spins up the MySQL database container (`world`) and the app container.
* **`pom.xml`** - Manages project dependencies (MySQL Connector) and Maven configurations.
* **`db/`** - Contains database initialization assets, including the sample schema and dataset executed automatically during container start up.
* **`src/`** - Application source code (`src/main` for production code and `src/test` for JUnit 5 unit tests).
* **`.github/workflows/`** - Contains GitHub Actions CI/CD pipeline automation for continuous integration and deployment testing.
* **`docs/use-cases/`** - Stores project documentation and use case specifications.
* **`CODE_OF_CONDUCT.md`** - Sets expectations for team collaboration and professional standards.
* **`Group3.xlsx`** - Group contribution tracking document.
* **`LICENSE`** - Apache-2.0 open-source license documentation.
