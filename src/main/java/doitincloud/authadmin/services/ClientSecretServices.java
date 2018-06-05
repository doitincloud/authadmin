package doitincloud.authadmin.services;

import doitincloud.authadmin.models.Client;

public interface ClientSecretServices {

    Client resetSecret(String id);

    Client setSecret(String id, String secret);
}
