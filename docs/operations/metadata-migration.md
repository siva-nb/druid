---
id: metadata-migration
title: "Metadata Migration"
---

<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->


If you have been running an evaluation Druid cluster using the built-in Derby metadata storage and wish to migrate to a
more production-capable metadata store such as MySQL or PostgreSQL, this document describes the necessary steps.

## Shut down cluster services

To ensure a clean migration, shut down the non-coordinator services to ensure that metadata state will not
change as you do the migration.

When migrating from Derby, the coordinator processes will still need to be up initially, as they host the Derby database.

## Exporting metadata

Druid provides an [Export Metadata Tool](../operations/export-metadata.md) for exporting metadata from Derby into CSV files
which can then be imported into your new metadata store.

The tool also provides options for rewriting the deep storage locations of segments; this is useful
for [deep storage migration](../operations/deep-storage-migration.md).

Run the `export-metadata` tool on your existing cluster, and save the CSV files it generates. After a successful export, you can shut down the coordinator.

## Initializing the new metadata store

### Create database

Before importing the existing cluster metadata, you will need to set up the new metadata store.

The [MySQL extension](../development/extensions-core/mysql.md) and [PostgreSQL extension](../development/extensions-core/postgresql.md) docs have instructions for initial database setup.

### Update configuration

Update your Druid runtime properties with the new metadata configuration.

### Create Druid tables

**If you have set `druid.metadata.storage.connector.createTables` to `true` (which is the default), and your metadata connect user has DDL privileges, you can disregard this section as Druid will create metadata tables automatically on start up.**

Druid provides a `metadata-init` tool for creating Druid's metadata tables. After initializing the Druid database, you can run the commands shown below from the root of the Druid package to initialize the tables.

In the example commands below:

- `lib` is the Druid lib directory
- `extensions` is the Druid extensions directory
- `base` corresponds to the value of `druid.metadata.storage.tables.base` in the configuration, `druid` by default.
- The `--connectURI` parameter corresponds to the value of `druid.metadata.storage.connector.connectURI`.
- The `--user` parameter corresponds to the value of `druid.metadata.storage.connector.user`.
- The `--password` parameter corresponds to the value of `druid.metadata.storage.connector.password`.

#### MySQL

```bash
${DRUID_ROOT}/examples/bin/init-metadata.sh -d mysql -u <username> -p <password> -c <mysql-uri>
```

#### PostgreSQL

```bash
${DRUID_ROOT}/examples/bin/init-metadata.sh -d postgresql -u <username> -p <password> -c <postgresql-uri>
```

### Update Druid tables to latest compatible schema

The same command as above can be used to update Druid metadata tables to the latest version. If any table already exists, it is not created again but any ALTER statements that may be required are still executed.

### Import metadata

After initializing the tables, please refer to the [import commands](../operations/export-metadata.md#importing-metadata) for your target database.

### Restart cluster

After importing the metadata successfully, you can now restart your cluster.
