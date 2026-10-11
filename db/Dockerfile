FROM mysql/mysql-server:latest

# Copy initialization SQL script into MySQL auto-execution directory
COPY world.sql /docker-entrypoint-initdb.d/

# Set root password and grant access
ENV MYSQL_ROOT_PASSWORD=D@to0000
ENV MYSQL_ROOT_HOST=%