/*
 * Copyright 2018 the original author or authors
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

package com.blueprint.centromere.core.model.impl;

import com.blueprint.centromere.core.model.Ignored;
import com.blueprint.centromere.core.model.Model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Simple {@link UserDetails} implementation for data warehouse user metadata.  Exercise good security
 *   practices: always hash passwords!  Do not store them in plain-text!
 * 
 * @author woemler
 */
@Document(collection = "users")
@AccessType(Type.PROPERTY)
public class User implements Model<String>, UserDetails {
  
  private String userId;
  
	@Indexed(unique = true) 
  @NotEmpty
  private String username;
	
	@Ignored 
  @NotEmpty
  private String password;
	
	private boolean accountNonExpired = true;
	
	private boolean accountNonLocked = true;
	
	private boolean enabled = false;
	
	private boolean credentialsNonExpired = true;
	
	private List<SimpleGrantedAuthority> authorities = new ArrayList<>();

	@Override 
	public boolean isEnabled() {
		return enabled;
	}

	@Override 
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override 
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override 
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Id
  @Override
  public String getId() {
    return userId;
  }

  @Override
  public void setId(String id) {
    this.userId = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override 
	public String getUsername() {
		return username;
	}

	@Override 
	public String getPassword() {
		return password;
	}

	@Override 
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setAuthorities(List<SimpleGrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	
	public void addRole(String role){
		if (authorities == null){
			authorities = new ArrayList<>();
		}
		authorities.add(new SimpleGrantedAuthority(role));
	}

	@Transient
	public List<String> getRoles(){
		List<String> roles = new ArrayList<>();
		for (GrantedAuthority authority: authorities){
			roles.add(authority.getAuthority());
		}
		return roles;
	}
	
	public boolean hasRole(String role){
		for (GrantedAuthority authority: authorities){
			if (authority.getAuthority().equals(role)){
				return true;
			}
		}
		return false;
	}

}
