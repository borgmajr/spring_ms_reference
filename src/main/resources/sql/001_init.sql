-- drop all connections to db
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'spring_ms';

DROP DATABASE IF EXISTS spring_ms;

DROP USER if exists spring_ms_dev;

CREATE USER spring_ms_dev WITH LOGIN NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION PASSWORD 'spring_ms_pw';

CREATE DATABASE spring_ms WITH  OWNER = spring_ms_dev ENCODING = 'UTF8' TABLESPACE = pg_default CONNECTION LIMIT = -1;
