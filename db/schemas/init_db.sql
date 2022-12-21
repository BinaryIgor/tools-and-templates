CREATE database "system-template-db";
--specify different password in runtime, when deploying or whatever!
CREATE USER "system-template-db-user" WITH PASSWORD 'system-template-db-password';
GRANT CONNECT ON DATABASE "system-template-db" TO "system-template-db-user";
GRANT ALL ON DATABASE "system-template-db" TO "system-template-db-user";