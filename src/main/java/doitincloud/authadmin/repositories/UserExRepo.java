package doitincloud.authadmin.repositories;

import org.springframework.data.rest.core.annotation.RestResource;

public interface UserExRepo {

    @RestResource(path="authority-options", rel = "authority-options")
    String[] getAuthorityOptions();
}
