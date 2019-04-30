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

package com.blueprint.centromere.ws.security;

import java.util.Date;

/**
 * Simple representation of a user authentication token used to verify user credentials in a
 * stateless manner.
 *
 * @author woemler
 */
public class TokenDetails {

    private String token;
    private String username;
    private Date issued;
    private Date expires;

    public TokenDetails() {
    }

    public TokenDetails(String token, String username, Date issueDate, Date expirationDate) {
        this.token = token;
        this.username = username;
        this.issued = new Date(issueDate.getTime());
        this.expires = new Date(expirationDate.getTime());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getIssued() {
        return new Date(issued.getTime());
    }

    public void setIssued(Date issued) {
        this.issued = new Date(issued.getTime());
    }

    public Date getExpires() {
        return new Date(expires.getTime());
    }

    public void setExpires(Date expires) {
        this.expires = new Date(expires.getTime());
    }
}
