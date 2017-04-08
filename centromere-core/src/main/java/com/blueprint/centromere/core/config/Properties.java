/*
 * Copyright 2017 the original author or authors
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
 * Global environmental property names, for easy access.
 *
 * @author woemler
 * @since 0.5.0
 */
public class Properties {

  private static final String CENTROMERE_PREFIX = "centromere.";
  private static final String APP_PREFIX = CENTROMERE_PREFIX + "app.";
  private static final String API_PREFIX = CENTROMERE_PREFIX + "api.";
  private static final String IMPORT_PREFIX = CENTROMERE_PREFIX + "import.";
  private static final String SECURITY_PREFIX = CENTROMERE_PREFIX + "security.";
  private static final String DB_PREFIX = CENTROMERE_PREFIX + "db.";
  private static final String LOGGING_PREFIX = CENTROMERE_PREFIX + "logging.";
  private static final String WEB_PREFIX = CENTROMERE_PREFIX + "web.";

  public static final String APP_VERSION = APP_PREFIX + "version";

  public static final String SKIP_INVALID_RECORDS = IMPORT_PREFIX + "skip-invalid-records";
  public static final String SKIP_INVALID_SAMPLES = IMPORT_PREFIX + "skip-invalid-samples";
  public static final String SKIP_INVALID_GENES = IMPORT_PREFIX + "skip-invalid-genes";
  public static final String SKIP_INVALID_FILES = IMPORT_PREFIX + "skip-invalid-files";
  public static final String SKIP_EXISTING_FILES = IMPORT_PREFIX + "skip-existing-files";
  public static final String TEMP_DIR = IMPORT_PREFIX + "temp-dir";
  
  public static final String DB_HOST = DB_PREFIX + "host";
  public static final String DB_NAME = DB_PREFIX + "name";
  public static final String DB_PORT = DB_PREFIX + "port";
  public static final String DB_USER = DB_PREFIX + "username";
  public static final String DB_PASSWORD = DB_PREFIX + "password";

}
