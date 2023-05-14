set search_path to "public";

CREATE OR REPLACE FUNCTION truncate_tables_owned_by(_username text)
 RETURNS void AS
$func$
BEGIN
   EXECUTE
  (SELECT 'TRUNCATE TABLE '
       || string_agg(quote_ident(schemaname) || '.' || quote_ident(tablename), ', ')
       || ' CASCADE'
   FROM   pg_tables
   WHERE  tableowner = _username
   AND    schemaname in (${schemas})
   );
END
$func$ LANGUAGE plpgsql;