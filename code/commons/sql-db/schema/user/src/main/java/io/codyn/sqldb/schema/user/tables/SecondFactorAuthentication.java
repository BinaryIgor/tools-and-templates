/*
 * This file is generated by jOOQ.
 */
package io.codyn.sqldb.schema.user.tables;


import io.codyn.sqldb.schema.user.Keys;
import io.codyn.sqldb.schema.user.User;
import io.codyn.sqldb.schema.user.tables.records.SecondFactorAuthenticationRecord;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SecondFactorAuthentication extends TableImpl<SecondFactorAuthenticationRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>user.second_factor_authentication</code>
     */
    public static final SecondFactorAuthentication SECOND_FACTOR_AUTHENTICATION = new SecondFactorAuthentication();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SecondFactorAuthenticationRecord> getRecordType() {
        return SecondFactorAuthenticationRecord.class;
    }

    /**
     * The column <code>user.second_factor_authentication.user_id</code>.
     */
    public final TableField<SecondFactorAuthenticationRecord, UUID> USER_ID = createField(DSL.name("user_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>user.second_factor_authentication.email</code>.
     */
    public final TableField<SecondFactorAuthenticationRecord, String> EMAIL = createField(DSL.name("email"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>user.second_factor_authentication.code</code>.
     */
    public final TableField<SecondFactorAuthenticationRecord, String> CODE = createField(DSL.name("code"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>user.second_factor_authentication.sent_at</code>.
     */
    public final TableField<SecondFactorAuthenticationRecord, Instant> SENT_AT = createField(DSL.name("sent_at"), SQLDataType.INSTANT.nullable(false), this, "");

    /**
     * The column <code>user.second_factor_authentication.expires_at</code>.
     */
    public final TableField<SecondFactorAuthenticationRecord, Instant> EXPIRES_AT = createField(DSL.name("expires_at"), SQLDataType.INSTANT.nullable(false), this, "");

    private SecondFactorAuthentication(Name alias, Table<SecondFactorAuthenticationRecord> aliased) {
        this(alias, aliased, null);
    }

    private SecondFactorAuthentication(Name alias, Table<SecondFactorAuthenticationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>user.second_factor_authentication</code> table reference
     */
    public SecondFactorAuthentication(String alias) {
        this(DSL.name(alias), SECOND_FACTOR_AUTHENTICATION);
    }

    /**
     * Create an aliased <code>user.second_factor_authentication</code> table reference
     */
    public SecondFactorAuthentication(Name alias) {
        this(alias, SECOND_FACTOR_AUTHENTICATION);
    }

    /**
     * Create a <code>user.second_factor_authentication</code> table reference
     */
    public SecondFactorAuthentication() {
        this(DSL.name("second_factor_authentication"), null);
    }

    public <O extends Record> SecondFactorAuthentication(Table<O> child, ForeignKey<O, SecondFactorAuthenticationRecord> key) {
        super(child, key, SECOND_FACTOR_AUTHENTICATION);
    }

    @Override
    public Schema getSchema() {
        return User.USER_SCHEMA;
    }

    @Override
    public UniqueKey<SecondFactorAuthenticationRecord> getPrimaryKey() {
        return Keys.SECOND_FACTOR_AUTHENTICATION_PKEY;
    }

    @Override
    public List<UniqueKey<SecondFactorAuthenticationRecord>> getKeys() {
        return Arrays.<UniqueKey<SecondFactorAuthenticationRecord>>asList(Keys.SECOND_FACTOR_AUTHENTICATION_PKEY, Keys.SECOND_FACTOR_AUTHENTICATION_EMAIL_KEY);
    }

    @Override
    public List<ForeignKey<SecondFactorAuthenticationRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SecondFactorAuthenticationRecord, ?>>asList(Keys.SECOND_FACTOR_AUTHENTICATION__SECOND_FACTOR_AUTHENTICATION_USER_ID_FKEY);
    }

    private transient io.codyn.sqldb.schema.user.tables.User _user;

    public io.codyn.sqldb.schema.user.tables.User user() {
        if (_user == null)
            _user = new io.codyn.sqldb.schema.user.tables.User(this, Keys.SECOND_FACTOR_AUTHENTICATION__SECOND_FACTOR_AUTHENTICATION_USER_ID_FKEY);

        return _user;
    }

    @Override
    public SecondFactorAuthentication as(String alias) {
        return new SecondFactorAuthentication(DSL.name(alias), this);
    }

    @Override
    public SecondFactorAuthentication as(Name alias) {
        return new SecondFactorAuthentication(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SecondFactorAuthentication rename(String name) {
        return new SecondFactorAuthentication(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SecondFactorAuthentication rename(Name name) {
        return new SecondFactorAuthentication(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<UUID, String, String, Instant, Instant> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
