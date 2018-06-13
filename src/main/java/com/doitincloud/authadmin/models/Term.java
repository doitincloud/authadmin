package com.doitincloud.authadmin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.doitincloud.authadmin.supports.AppCtx;
import com.doitincloud.commons.Utils;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Entity
@Table(name="oauth2_term")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NamedQuery(name = "Term.findAll", query="select d from Term d order by d.type, d.name")
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotEmpty
    private String type;

    @NotEmpty
    private String name;

    @JsonIgnore
    @Transient
    private Map<String, Object> mapValue = new LinkedHashMap<>();

    @JsonProperty("created_at")
    @Column(name="created_at", insertable=false, updatable=false)
    private Date createdAt;

    @JsonProperty("updated_at")
    @Column(name="updated_at", insertable=false, updatable=false)
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    @JsonProperty("role_options")
    public Set<String> getRoleOptions() {
        return Utils.getSetProperty(mapValue, "options");
    }

    public void setRoleOptions(Set<String> options) {
        Utils.setSetProperty(mapValue, "options", options);
    }

    @Transient
    @JsonProperty("visible_by_roles")
    public Set<String> getVisibleByRoles() {
        return Utils.getSetProperty(mapValue, "visible");
    }

    public void setVisibleByRoles(Set<String> roles) {
        Utils.setSetProperty(mapValue, "visible", roles);
    }

    @JsonIgnore
    @Column(name="map_value")
    @Access(AccessType.PROPERTY)
    public String getMapValue() {
        return Utils.toJson(mapValue);
    }

    public void setMapValue(String value) {
        if (value == null || value.length() == 0) {
            this.mapValue.clear();
            return;
        }
        mapValue = Utils.toMap(value);
    }

    @Transient
    @JsonIgnore
    public Map<String, Object> getMap() {
        return mapValue;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PostPersist
    @PostRemove
    @PostUpdate
    public void onChange() {
        Utils.getExcutorService().submit(() -> {
            AppCtx.getCacheOps().setupTermTypeMap();
        });
    }
}
