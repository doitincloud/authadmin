package com.doitincloud.authadmin.services;

import com.doitincloud.authadmin.models.Client;

public interface ClientSecretServices {

    Client resetSecret(String id);

    Client setSecret(String id, String secret);
}
