# Use MySQL 8.0 for a consistent database environment
FROM mysql:8.0

# Copy the world database script so MySQL can load it on first startup
COPY world.sql /docker-entrypoint-initdb.d/

# Allow the Java application to connect from another container
ENV MYSQL_ROOT_HOST=%