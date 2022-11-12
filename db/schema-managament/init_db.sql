CREATE database "experimental-db";
--specify different password in runtime, when deploying or whatever!
CREATE USER "experimental-user" WITH PASSWORD '';
GRANT CONNECT ON DATABASE "experimental-db" TO "experimental-user";
GRANT ALL ON DATABASE "experimental-db" TO "experimental-user";