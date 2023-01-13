/*
 * This file is generated by jOOQ.
 */
package io.codyn.sqldb.schema.user.tables.records;


import io.codyn.sqldb.schema.user.tables.User;

import java.time.Instant;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserRecord extends UpdatableRecordImpl<UserRecord> implements Record8<UUID, String, String, String, String, Boolean, String, Instant> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>user.user.id</code>.
     */
    public UserRecord setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>user.user.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>user.user.name</code>.
     */
    public UserRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>user.user.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>user.user.email</code>.
     */
    public UserRecord setEmail(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>user.user.email</code>.
     */
    public String getEmail() {
        return (String) get(2);
    }

    /**
     * Setter for <code>user.user.password</code>.
     */
    public UserRecord setPassword(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>user.user.password</code>.
     */
    public String getPassword() {
        return (String) get(3);
    }

    /**
     * Setter for <code>user.user.state</code>.
     */
    public UserRecord setState(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>user.user.state</code>.
     */
    public String getState() {
        return (String) get(4);
    }

    /**
     * Setter for <code>user.user.second_factor_authentication</code>.
     */
    public UserRecord setSecondFactorAuthentication(Boolean value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>user.user.second_factor_authentication</code>.
     */
    public Boolean getSecondFactorAuthentication() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>user.user.external_authentication</code>.
     */
    public UserRecord setExternalAuthentication(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>user.user.external_authentication</code>.
     */
    public String getExternalAuthentication() {
        return (String) get(6);
    }

    /**
     * Setter for <code>user.user.created_at</code>.
     */
    public UserRecord setCreatedAt(Instant value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>user.user.created_at</code>.
     */
    public Instant getCreatedAt() {
        return (Instant) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<UUID, String, String, String, String, Boolean, String, Instant> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<UUID, String, String, String, String, Boolean, String, Instant> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return User.USER.ID;
    }

    @Override
    public Field<String> field2() {
        return User.USER.NAME;
    }

    @Override
    public Field<String> field3() {
        return User.USER.EMAIL;
    }

    @Override
    public Field<String> field4() {
        return User.USER.PASSWORD;
    }

    @Override
    public Field<String> field5() {
        return User.USER.STATE;
    }

    @Override
    public Field<Boolean> field6() {
        return User.USER.SECOND_FACTOR_AUTHENTICATION;
    }

    @Override
    public Field<String> field7() {
        return User.USER.EXTERNAL_AUTHENTICATION;
    }

    @Override
    public Field<Instant> field8() {
        return User.USER.CREATED_AT;
    }

    @Override
    public UUID component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public String component3() {
        return getEmail();
    }

    @Override
    public String component4() {
        return getPassword();
    }

    @Override
    public String component5() {
        return getState();
    }

    @Override
    public Boolean component6() {
        return getSecondFactorAuthentication();
    }

    @Override
    public String component7() {
        return getExternalAuthentication();
    }

    @Override
    public Instant component8() {
        return getCreatedAt();
    }

    @Override
    public UUID value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public String value3() {
        return getEmail();
    }

    @Override
    public String value4() {
        return getPassword();
    }

    @Override
    public String value5() {
        return getState();
    }

    @Override
    public Boolean value6() {
        return getSecondFactorAuthentication();
    }

    @Override
    public String value7() {
        return getExternalAuthentication();
    }

    @Override
    public Instant value8() {
        return getCreatedAt();
    }

    @Override
    public UserRecord value1(UUID value) {
        setId(value);
        return this;
    }

    @Override
    public UserRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public UserRecord value3(String value) {
        setEmail(value);
        return this;
    }

    @Override
    public UserRecord value4(String value) {
        setPassword(value);
        return this;
    }

    @Override
    public UserRecord value5(String value) {
        setState(value);
        return this;
    }

    @Override
    public UserRecord value6(Boolean value) {
        setSecondFactorAuthentication(value);
        return this;
    }

    @Override
    public UserRecord value7(String value) {
        setExternalAuthentication(value);
        return this;
    }

    @Override
    public UserRecord value8(Instant value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public UserRecord values(UUID value1, String value2, String value3, String value4, String value5, Boolean value6, String value7, Instant value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserRecord
     */
    public UserRecord() {
        super(User.USER);
    }

    /**
     * Create a detached, initialised UserRecord
     */
    public UserRecord(UUID id, String name, String email, String password, String state, Boolean secondFactorAuthentication, String externalAuthentication, Instant createdAt) {
        super(User.USER);

        setId(id);
        setName(name);
        setEmail(email);
        setPassword(password);
        setState(state);
        setSecondFactorAuthentication(secondFactorAuthentication);
        setExternalAuthentication(externalAuthentication);
        setCreatedAt(createdAt);
    }
}
