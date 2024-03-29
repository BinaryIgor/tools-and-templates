/*
 * This file is generated by jOOQ.
 */
package io.codyn.sqldb.schema.user.tables.records;


import io.codyn.sqldb.schema.user.tables.ActivationToken;

import java.time.Instant;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ActivationTokenRecord extends UpdatableRecordImpl<ActivationTokenRecord> implements Record5<UUID, String, String, Instant, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>user.activation_token.user_id</code>.
     */
    public ActivationTokenRecord setUserId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>user.activation_token.user_id</code>.
     */
    public UUID getUserId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>user.activation_token.token</code>.
     */
    public ActivationTokenRecord setToken(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>user.activation_token.token</code>.
     */
    public String getToken() {
        return (String) get(1);
    }

    /**
     * Setter for <code>user.activation_token.status</code>.
     */
    public ActivationTokenRecord setStatus(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>user.activation_token.status</code>.
     */
    public String getStatus() {
        return (String) get(2);
    }

    /**
     * Setter for <code>user.activation_token.expires_at</code>.
     */
    public ActivationTokenRecord setExpiresAt(Instant value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>user.activation_token.expires_at</code>.
     */
    public Instant getExpiresAt() {
        return (Instant) get(3);
    }

    /**
     * Setter for <code>user.activation_token.type</code>.
     */
    public ActivationTokenRecord setType(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>user.activation_token.type</code>.
     */
    public String getType() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<UUID, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<UUID, String, String, Instant, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<UUID, String, String, Instant, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return ActivationToken.ACTIVATION_TOKEN.USER_ID;
    }

    @Override
    public Field<String> field2() {
        return ActivationToken.ACTIVATION_TOKEN.TOKEN;
    }

    @Override
    public Field<String> field3() {
        return ActivationToken.ACTIVATION_TOKEN.STATUS;
    }

    @Override
    public Field<Instant> field4() {
        return ActivationToken.ACTIVATION_TOKEN.EXPIRES_AT;
    }

    @Override
    public Field<String> field5() {
        return ActivationToken.ACTIVATION_TOKEN.TYPE;
    }

    @Override
    public UUID component1() {
        return getUserId();
    }

    @Override
    public String component2() {
        return getToken();
    }

    @Override
    public String component3() {
        return getStatus();
    }

    @Override
    public Instant component4() {
        return getExpiresAt();
    }

    @Override
    public String component5() {
        return getType();
    }

    @Override
    public UUID value1() {
        return getUserId();
    }

    @Override
    public String value2() {
        return getToken();
    }

    @Override
    public String value3() {
        return getStatus();
    }

    @Override
    public Instant value4() {
        return getExpiresAt();
    }

    @Override
    public String value5() {
        return getType();
    }

    @Override
    public ActivationTokenRecord value1(UUID value) {
        setUserId(value);
        return this;
    }

    @Override
    public ActivationTokenRecord value2(String value) {
        setToken(value);
        return this;
    }

    @Override
    public ActivationTokenRecord value3(String value) {
        setStatus(value);
        return this;
    }

    @Override
    public ActivationTokenRecord value4(Instant value) {
        setExpiresAt(value);
        return this;
    }

    @Override
    public ActivationTokenRecord value5(String value) {
        setType(value);
        return this;
    }

    @Override
    public ActivationTokenRecord values(UUID value1, String value2, String value3, Instant value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ActivationTokenRecord
     */
    public ActivationTokenRecord() {
        super(ActivationToken.ACTIVATION_TOKEN);
    }

    /**
     * Create a detached, initialised ActivationTokenRecord
     */
    public ActivationTokenRecord(UUID userId, String token, String status, Instant expiresAt, String type) {
        super(ActivationToken.ACTIVATION_TOKEN);

        setUserId(userId);
        setToken(token);
        setStatus(status);
        setExpiresAt(expiresAt);
        setType(type);
    }
}
