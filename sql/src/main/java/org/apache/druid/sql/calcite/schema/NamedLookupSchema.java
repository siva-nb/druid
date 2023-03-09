/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.sql.calcite.schema;

import com.google.inject.Inject;
import org.apache.calcite.schema.Schema;
import org.apache.druid.server.security.ResourceType;
import org.apache.druid.sql.calcite.planner.PlannerConfig;

import javax.annotation.Nullable;

/**
 * The schema for Druid lookup tables to be accessible via SQL.
 */
public class NamedLookupSchema implements NamedSchema
{
  public static final String NAME = "lookup";

  private final LookupSchema lookupSchema;
  private final PlannerConfig plannerConfig;

  @Inject
  public NamedLookupSchema(PlannerConfig plannerConfig, LookupSchema lookupSchema)
  {
    this.plannerConfig = plannerConfig;
    this.lookupSchema = lookupSchema;
  }

  @Override
  public String getSchemaName()
  {
    return NAME;
  }

  @Override
  public Schema getSchema()
  {
    return lookupSchema;
  }

  @Nullable
  @Override
  public String getSchemaResourceType(String resourceName)
  {
    if (plannerConfig.isAuthorizeLookUpsDirectly()) {
      return ResourceType.LOOKUP;
    }
    return null;
  }
}
