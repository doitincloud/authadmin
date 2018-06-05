package doitincloud.authadmin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import doitincloud.authadmin.supports.Newable;
import doitincloud.authadmin.supports.ValidPassword;
import doitincloud.authadmin.supports.ValidPhoneNumber;
import doitincloud.authadmin.supports.ValidTermSet;
import org.hibernate.annotations.GenericGenerator;

import doitincloud.commons.Utils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Entity
@Table(name="oauth2_client_details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Client implements Newable {

    @Id
    @GenericGenerator(name = "client_id", strategy = "doitincloud.authadmin.supports.IdGenerator")
    @GeneratedValue(generator = "client_id")
    @Column(name="client_id")
    @JsonProperty("client_id")
    private String clientId;

    @NotEmpty(message = "client name can not be empty")
    @JsonProperty("client_name")
    @Column(name="client_name")
    private String clientName;

    @JsonIgnore
    @Column(name="client_secret")
    private String clientSecret;

    @Transient
    @ValidPassword
    @JsonProperty("plain_text_secret")
    private String plainTextSecret;

    @NotEmpty(message = "contact name can not be empty")
    @JsonProperty("contact_name")
    @Column(name="contact_name")
    private String contactName;

    @Email
    @NotEmpty(message = "contact email can not be empty")
    @JsonProperty("contact_email")
    @Column(name="contact_email")
    private String contactEmail;

    @ValidPhoneNumber
    @JsonProperty("contact_phone_number")
    @Column(name="contact_phone_number")
    private String contactPhoneNumber;

    @Max(value=31557600, message = "max 1 year in seconds")
    @Min(value=900, message = "min 15 minutes in seconds")
    @JsonProperty("access_token_validity_seconds")
    @Column(name="access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;

    @Max(value=157788000, message = "max 5 years in seconds")
    @Min(value=3600, message = "min 1 hour in seconds")
    @JsonProperty("refresh_token_validity_seconds")
    @Column(name="refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;

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
    private boolean isNew =  false;

    @Override
    public void renew() {
        isNew = true;
    }

    public Client() {
        mapValue.put("authorities", Arrays.asList("ROLE_CLIENT"));
        mapValue.put("resource_ids", Arrays.asList("oauth2-resource"));
        mapValue.put("authorized_grant_types", Arrays.asList("password"));
        mapValue.put("scope", Arrays.asList("read"));
        mapValue.put("auto_approve_scopes", Arrays.asList("read"));
    }

    public Client(String name) {
        this();
        clientName = name;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String id) {
        this.clientId = id;
    }

    public void setClientName(String name) {
        this.clientName = name;
    }

    public String getClientName() {
        return clientName;
    }

    @JsonIgnore
    @Transient
    public boolean isSecretRequired() {
        return clientSecret == null || clientSecret.length() != 0;
    }

    @JsonIgnore
    @Column(name="client_secret")
    @Access(AccessType.PROPERTY)
    public String getClientSecret() {
        if (clientSecret == null) {
            String text = getPlainTextSecret();
            if (text != null) {
                clientSecret = Utils.encodePassword(text);
            }
        }
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Transient
    public String getPlainTextSecret() {
        if ((isNew || clientId == null) && plainTextSecret == null) {
            plainTextSecret = Utils.generatePassword(16);
            clientSecret = Utils.encodePassword(plainTextSecret);
        }
        return plainTextSecret;
    }

    public void setPlainTextSecret(String text) {
        plainTextSecret = text;
        if (text == null) {
            return;
        }
        clientSecret = Utils.encodePassword(plainTextSecret);
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactEmail(String email) {
        this.contactEmail = email;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    @JsonIgnore
    @Transient
    public boolean isScoped() {
        Set<String> scope = getScope();
        if (scope == null || scope.size() ==0) {
            return false;
        } else {
            return true;
        }
    }

    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public boolean isAutoApprove(String scope) {
        Set<String> set = getAutoApproveScopes();
        if (set == null || set.size() == 0) {
            return false;
        }
        return set.contains(scope);
    }

    @Transient
    @ValidTermSet("resource_id")
    @JsonProperty("resource_ids")
    public Set<String> getResourceIds() {
        return Utils.getSetProperty(mapValue,"resource_ids");
    }

    public void setResourceIds(Set<String> set) {
        Utils.setSetProperty(mapValue, "resource_ids", set);
    }

    @Transient
    @ValidTermSet("scope")
    @JsonProperty("scope")
    public Set<String> getScope() {
        return Utils.getSetProperty(mapValue, "scope");
    }

    public void setScope(Set<String> set) {
        Utils.setSetProperty(mapValue,"scope", set);
    }

    @Transient
    @ValidTermSet("grant_type")
    @JsonProperty("authorized_grant_types")
    public Set<String> getAuthorizedGrantTypes() {
        return Utils.getSetProperty(mapValue, "authorized_grant_types");
    }

    public void setAuthorizedGrantTypes(Set<String> set) {
        Utils.setSetProperty(mapValue,"authorized_grant_types", set);
    }

    @Transient
    @ValidTermSet("scope")
    @JsonProperty("auto_approve_scopes")
    public Set<String> getAutoApproveScopes() {
        return Utils.getSetProperty(mapValue, "auto_approve_scopes");
    }

    public void setAutoApproveScopes(Set<String> set) {
        Utils.setSetProperty(mapValue,"auto_approve_scopes", set);
    }

    @Transient
    @JsonProperty("registered_redirect_uri")
    public Set<String> getRegisteredRedirectUri() {
        return Utils.getSetProperty(mapValue, "registered_redirect_uri");
    }

    public void setRegisteredRedirectUri(Set<String> set) {
        Utils.setSetProperty(mapValue,"registered_redirect_uri", set);
    }

    @Transient
    @ValidTermSet("client_role")
    @JsonProperty("authorities")
    public Set<String> getAuthorities() {
        return Utils.getSetProperty(mapValue, "authorities");
    }

    public void setAuthorities(Set<String> authorities) {
        Utils.setSetProperty(mapValue, "authorities", authorities);
    }

    private static Set<String> existedPropertySet = new HashSet<>(Arrays.asList(
            "resource_ids",
            "scope",
            "authorized_grant_types",
            "auto_approve_scopes",
            "registered_redirect_uri",
            "authorities"
    ));

    @Transient
    @JsonProperty("additional_information")
    private Map<String, Object> additionalInformation;

    @Transient
    public Map<String, Object> getAdditionalInformation() {
        if (additionalInformation == null) {
            additionalInformation = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry: mapValue.entrySet()) {
                String key = entry.getKey();
                if (!existedPropertySet.contains(key)) {
                    additionalInformation.put(key, entry.getValue());
                }
            }
        }
        return additionalInformation;
    }

    public void setAdditionalInformation(Map<String, Object> map) {
        additionalInformation = map;
    }

    public void clearAdditionalInformation() {
        if (additionalInformation != null) {
            additionalInformation.clear();
        }
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
    @Access(AccessType.PROPERTY)
    @Column(name="map_value")
    public String getMapValue() {
        if (additionalInformation != null && additionalInformation.size() > 0) {
            for (Map.Entry<String, Object> entry: additionalInformation.entrySet()) {
                mapValue.put(entry.getKey(), entry.getValue());
            }
        }
        return Utils.toJson(mapValue);
    }

    public void setMapValue(String value) {
        if (value == null || value.length() == 0) {
            this.mapValue.clear();
            return;
        }
        Map<String, Object> map = Utils.toMap(value);
        for (Map.Entry<String, Object> entry: map.entrySet()) {
            String key = entry.getKey();
            if (!existedPropertySet.contains(key)) {
                if (additionalInformation == null) {
                    additionalInformation = new LinkedHashMap<>();
                }
                additionalInformation.put(key, entry.getValue());
            } else {
                mapValue.put(key, entry.getValue());
            }
        }
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