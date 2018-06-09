package doitincloud.authadmin.repositories;

import org.springframework.data.rest.core.annotation.RestResource;

public interface ClientExRepo {

    @RestResource(path="authority-options", rel = "authority-options")
    String[] getAuthorityOptions();

    @RestResource(path="resource-id-options", rel = "resource-id-options")
    String[] getResourceIdOptions();

    @RestResource(path="grant-type-options", rel = "grant-type-options")
    String[] getGrantTypeOptions();

    @RestResource(path="scope-options", rel = "scope-options")
    String[] getScopeOptions();

    @RestResource(path="auto-approve-scope-options", rel = "auto-approve-scope-options")
    String[] getAutoApproveScopeOptions();
}
