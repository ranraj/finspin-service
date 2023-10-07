package com.yali.finspin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Organisation.
 */
@Document(collection = "organisation")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "organisation")
public class Organisation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("blocked")
    private Boolean blocked;

    @Field("orgAccount")
    @JsonIgnoreProperties(value = { "owner", "org", "projects" }, allowSetters = true)
    private Set<OrgAccount> orgAccounts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Organisation id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Organisation name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBlocked() {
        return this.blocked;
    }

    public Organisation blocked(Boolean blocked) {
        this.setBlocked(blocked);
        return this;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Set<OrgAccount> getOrgAccounts() {
        return this.orgAccounts;
    }

    public void setOrgAccounts(Set<OrgAccount> orgAccounts) {
        if (this.orgAccounts != null) {
            this.orgAccounts.forEach(i -> i.setOrg(null));
        }
        if (orgAccounts != null) {
            orgAccounts.forEach(i -> i.setOrg(this));
        }
        this.orgAccounts = orgAccounts;
    }

    public Organisation orgAccounts(Set<OrgAccount> orgAccounts) {
        this.setOrgAccounts(orgAccounts);
        return this;
    }

    public Organisation addOrgAccount(OrgAccount orgAccount) {
        this.orgAccounts.add(orgAccount);
        orgAccount.setOrg(this);
        return this;
    }

    public Organisation removeOrgAccount(OrgAccount orgAccount) {
        this.orgAccounts.remove(orgAccount);
        orgAccount.setOrg(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organisation)) {
            return false;
        }
        return id != null && id.equals(((Organisation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organisation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", blocked='" + getBlocked() + "'" +
            "}";
    }
}
