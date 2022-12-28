CREATE DATABASE "system-template";
--specify different password in runtime, when deploying or whatever!
CREATE USER "system-template-user" WITH PASSWORD 'system-template-password';
GRANT CONNECT ON DATABASE "system-template" TO "system-template-user";
GRANT ALL ON DATABASE "system-template" TO "system-template-user";

--read-only user
CREATE USER "system-template-user-reader" WITH PASSWORD 'system-template-password-reader';
GRANT CONNECT ON DATABASE "system-template" TO "system-template-user-reader";