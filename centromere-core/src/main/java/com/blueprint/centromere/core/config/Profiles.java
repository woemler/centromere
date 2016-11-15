/*
 * Copyright 2016 William Oemler, Blueprint Medicines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blueprint.centromere.core.config;

/**
 * @author woemler
 */
public class Profiles {
	
	public static final String SCHEMA_CUSTOM = "schema_custom";
	public static final String SCHEMA_DEFAULT = "schema_default";

	public static final String DB_CUSTOM = "db_custom";
	public static final String DB_MONGODB = "db_mongodb";
	public static final String DB_MYSQL = "db_mysql";

	public static String[] getApplicationProfiles(Database database, Schema schema){
		String dbProfile;
		String schemaProfile;
		switch (database){
			case MONGODB:
				dbProfile = DB_MONGODB;
				break;
			default:
				dbProfile = DB_CUSTOM;
		}
		if (schema.equals(Schema.CUSTOM)){
			schemaProfile = SCHEMA_CUSTOM;
		} else if (schema.equals(Schema.DEFAULT)){
			schemaProfile = SCHEMA_DEFAULT;
		} else {
			throw new ConfigurationException(String.format("The configured profiles are incompatible: database=%s  schema=%s",
					database.toString(), schema.toString()));
		}
		return new String[]{dbProfile, schemaProfile};
	}
	
}
