/*
 * This file is generated by jOOQ.
 */
package io.codyn.commons.sqldb.schema.project.tables;


import io.codyn.commons.sqldb.schema.project.Keys;
import io.codyn.commons.sqldb.schema.project.Project;
import io.codyn.commons.sqldb.schema.project.tables.records.TaskRecord;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
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
public class Task extends TableImpl<TaskRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>project.task</code>
     */
    public static final Task TASK = new Task();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TaskRecord> getRecordType() {
        return TaskRecord.class;
    }

    /**
     * The column <code>project.task.id</code>.
     */
    public final TableField<TaskRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>project.task.project_id</code>.
     */
    public final TableField<TaskRecord, UUID> PROJECT_ID = createField(DSL.name("project_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>project.task.creator_id</code>.
     */
    public final TableField<TaskRecord, UUID> CREATOR_ID = createField(DSL.name("creator_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>project.task.assignee_id</code>.
     */
    public final TableField<TaskRecord, UUID> ASSIGNEE_ID = createField(DSL.name("assignee_id"), SQLDataType.UUID.nullable(false), this, "");

    /**
     * The column <code>project.task.name</code>.
     */
    public final TableField<TaskRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>project.task.description</code>.
     */
    public final TableField<TaskRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>project.task.version</code>.
     */
    public final TableField<TaskRecord, Long> VERSION = createField(DSL.name("version"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    private Task(Name alias, Table<TaskRecord> aliased) {
        this(alias, aliased, null);
    }

    private Task(Name alias, Table<TaskRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>project.task</code> table reference
     */
    public Task(String alias) {
        this(DSL.name(alias), TASK);
    }

    /**
     * Create an aliased <code>project.task</code> table reference
     */
    public Task(Name alias) {
        this(alias, TASK);
    }

    /**
     * Create a <code>project.task</code> table reference
     */
    public Task() {
        this(DSL.name("task"), null);
    }

    public <O extends Record> Task(Table<O> child, ForeignKey<O, TaskRecord> key) {
        super(child, key, TASK);
    }

    @Override
    public Schema getSchema() {
        return Project.PROJECT_SCHEMA;
    }

    @Override
    public Identity<TaskRecord, Long> getIdentity() {
        return (Identity<TaskRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<TaskRecord> getPrimaryKey() {
        return Keys.TASK_PKEY;
    }

    @Override
    public List<UniqueKey<TaskRecord>> getKeys() {
        return Arrays.<UniqueKey<TaskRecord>>asList(Keys.TASK_PKEY, Keys.TASK_NAME_KEY);
    }

    @Override
    public List<ForeignKey<TaskRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<TaskRecord, ?>>asList(Keys.TASK__TASK_PROJECT_ID_FKEY);
    }

    private transient io.codyn.commons.sqldb.schema.project.tables.Project _project;

    public io.codyn.commons.sqldb.schema.project.tables.Project project() {
        if (_project == null)
            _project = new io.codyn.commons.sqldb.schema.project.tables.Project(this, Keys.TASK__TASK_PROJECT_ID_FKEY);

        return _project;
    }

    @Override
    public Task as(String alias) {
        return new Task(DSL.name(alias), this);
    }

    @Override
    public Task as(Name alias) {
        return new Task(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Task rename(String name) {
        return new Task(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Task rename(Name name) {
        return new Task(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<UUID, UUID, UUID, UUID, String, String, Long> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}