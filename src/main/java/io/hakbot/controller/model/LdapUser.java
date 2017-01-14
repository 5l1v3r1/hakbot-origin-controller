/*
 * This file is part of Hakbot Origin Controller.
 *
 * Hakbot Origin Controller is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Hakbot Origin Controller is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Hakbot Origin Controller. If not, see http://www.gnu.org/licenses/.
 */
package io.hakbot.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

@PersistenceCapable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LdapUser implements Serializable, Principal {

    private static final long serialVersionUID = -4071674443468706162L;

    @PrimaryKey
    @Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
    @JsonIgnore
    private long id;

    @Persistent
    @Unique(name="LDAPUSER_USERNAME_IDX")
    @Column(name="USERNAME")
    private String username;

    @Persistent
    @Unique(name="LDAPUSER_DN_IDX")
    @Column(name="DN", jdbcType="VARCHAR", length=255, allowsNull="false")
    private String dn;

    @Persistent(table="LDAPUSERS_TEAMS", defaultFetchGroup="true")
    @Join(column="LDAPUSER_ID")
    @Element(column="TEAM_ID")
    private Set<Team> teams;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDN() {
        return dn;
    }

    public void setDN(String dn) {
        this.dn = dn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    /**
     * Do not use - only here to satisfy Principal implementation requirement
     * @deprecated use {@link LdapUser#getUsername()}
     */
    @Deprecated
    @JsonIgnore
    public String getName() {
        return getUsername();
    }

}
