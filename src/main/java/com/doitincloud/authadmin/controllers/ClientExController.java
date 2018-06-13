package com.doitincloud.authadmin.controllers;

import com.doitincloud.authadmin.services.ClientSecretServices;
import com.doitincloud.oauth2.supports.MissingRequiredDataException;
import com.doitincloud.authadmin.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.*;

import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Map;

@RestController
public class ClientExController {

    @Autowired
    private ClientSecretServices clientSecretServices;

    @Autowired
    EntityLinks entityLinks;

    @RequestMapping(value = "/v1/clients/{id}/reset-secret", method = RequestMethod.GET)
    @ResponseBody
    public Client resetSecret(
            @PathVariable("id") String id) {
        return clientSecretServices.resetSecret(id);
    }

    @RequestMapping(value = "/v1/clients/{id}/set-secret", method = RequestMethod.POST)
    @ResponseBody
    public Client setSecret(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> map) {
        if (!map.containsKey("secret")) {
            throw new MissingRequiredDataException("missing secret");
        }
        String secret = (String) map.get("secret");
        return clientSecretServices.setSecret(id, secret);
    }

    @Bean
    public ResourceProcessor<Resource<Client>> clientProcessor() {
        return new ResourceProcessor<Resource<Client>>() {
            @Override
            public Resource<Client> process(Resource<Client> resource) {
                String clientId = resource.getContent().getClientId();
                Link resetSecretLink = linkTo(
                        methodOn(ClientExController.class).resetSecret(clientId)).
                        withRel("reset-secret");
                resource.add(resetSecretLink);
                Link setSecretLink = linkTo(
                        methodOn(ClientExController.class).setSecret(clientId, null)).
                        withRel("set-secret");
                resource.add(setSecretLink);
                return resource;
            }
        };
    }
}
