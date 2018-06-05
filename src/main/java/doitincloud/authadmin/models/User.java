package doitincloud.authadmin.models;

import doitincloud.authadmin.supports.Newable;
import doitincloud.authadmin.supports.ValidPassword;
import doitincloud.authadmin.supports.ValidPhoneNumber;
import doitincloud.authadmin.supports.ValidTermSet;
import doitincloud.commons.Utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Entity
@Table(name="security_user_details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Newable {

    @Id
    @GenericGenerator(name = "user_id", strategy = "doitincloud.authadmin.supports.IdGenerator")
    @GeneratedValue(generator = "user_id")
    @Column(name="user_id")
    @JsonProperty("user_id")
    private String userId;

    @Email(message = "Email should be valid")
    @NotEmpty
    private String username;

    @JsonIgnore
    @Column(name="password")
    private String password;

    @Transient
    @ValidPassword
    @JsonProperty("plaint_text_password")
    private String plainTextPassword;

    @NotEmpty
    @JsonProperty("first_name")
    @Column(name="first_name")
    private String firstName;

    @NotEmpty
    @JsonProperty("last_name")
    @Column(name="last_name")
    private String lastName;

    @ValidPhoneNumber
    @JsonProperty("phone_number")
    @Column(name="phone_number")
    private String phoneNumber;

    private Boolean enabled = true;

    @JsonProperty("account_non_locked")
    @Column(name="account_non_locked")
    private Boolean accountNonLocked = true;

    @JsonProperty("credentials_non_expired")
    @Column(name="credentials_non_expired")
    private Boolean credentialsNonExpired = true;

    @JsonProperty("expires_at")
    @Column(name="expires_at")
    private Long expiresAt;

    @JsonProperty("created_at")
    @Column(name="created_at", insertable=false, updatable=false)
    private Date createdAt;

    @JsonProperty("updated_at")
    @Column(name="updated_at", insertable=false, updatable=false)
    private Date updatedAt;

    @JsonIgnore
    @Transient
    private Map<String, Object> mapValue = new LinkedHashMap<>();

    @JsonIgnore
    @Transient
    private boolean isNew = false;

    @Override
    public void renew() {
        isNew = true;
    }

    public User() {
        mapValue.put("authorities", Arrays.asList("ROLE_USER"));
    }

    public User(String username) {
        this();
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getPlainTextPassword() {
        if ((isNew || userId == null) && plainTextPassword == null) {
            plainTextPassword = Utils.generatePassword(16);
            password = Utils.encodePassword(plainTextPassword);
        }
        return plainTextPassword;
    }

    public void setPlainTextPassword(String plainText) {
        plainTextPassword = plainText;
        if (plainText == null) {
            return;
        }
        password = Utils.encodePassword(plainTextPassword);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Transient
    @JsonProperty("account_non_expired")
    public boolean isAccountNonExpired() {
        if (expiresAt == null) {
            return true;
        }
        return System.currentTimeMillis() < expiresAt;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.expiresAt = System.currentTimeMillis();
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Transient
    @ValidTermSet("user_role")
    @JsonProperty("authorities")
    public Set<String> getAuthorities() {
        return Utils.getSetProperty(mapValue, "authorities");
    }

    public void setAuthorities(Set<String> authorities) {
        Utils.setSetProperty(mapValue, "authorities", authorities);
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
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

    @JsonIgnore
    @Transient
    public Object getAdditionalProperty(String name) {
        return mapValue.get(name);
    }

    public void setAdditionalProperty(String name, Object value) {
        mapValue.put(name, value);
    }

    public void removeAdditionalProperty(String name) {
        mapValue.remove(name);
    }
}
