/*
 * Copyright 2016 the original author or authors
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
public enum Security {
    NONE,
    SECURE_WRITE,
    SECURE_READ_WRITE;

    public static final String NONE_PROFILE = "security_none";
    public static final String SECURE_WRITE_PROFILE = "security_secure_write";
    public static final String SECURE_READ_WRITE_PROFILE = "security_secure_read_write";

    public static String getProfile(Security security){
        switch (security){
            case SECURE_WRITE:
                return SECURE_WRITE_PROFILE;
            case SECURE_READ_WRITE:
                return SECURE_READ_WRITE_PROFILE;
            default:
                return NONE_PROFILE;
        }
    }

}
