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
 * @since 0.5.0
 */
public class Profiles {
	
	public enum ApplicationMode {
		WEB,
		CLI
	}
	
	public static final String WEB_PROFILE = "mode_web";
	public static final String CLI_PROFILE = "mode_cli";
	
	public static String getApplicationModeProfile(ApplicationMode mode){
		switch (mode){
			case WEB:
				return WEB_PROFILE;
			case CLI:
				return CLI_PROFILE;
			default:
				return null;
		}
	}

	public static String[] getApplicationProfiles(Database database, Schema schema, Security security){
		String dbProfile = Database.getProfile(database);
		String schemaProfile = Schema.getProfile(schema);
		String securityProfile = Security.getProfile(security);
		return new String[]{dbProfile, schemaProfile, securityProfile};
	}

	public static String[] getApplicationProfiles(Database database, Schema schema){
		String dbProfile = Database.getProfile(database);
		String schemaProfile = Schema.getProfile(schema);
		return new String[]{dbProfile, schemaProfile};
	}

	public static String[] getApplicationProfiles(AutoConfigureCentromere annotation){
		return getApplicationProfiles(annotation.database(), annotation.schema(), annotation.webSecurity());
	}
	
}
