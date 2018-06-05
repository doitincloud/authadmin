package doitincloud.authadmin.services;

import doitincloud.authadmin.models.Client;
import doitincloud.authadmin.repositories.ClientJpaRepo;
import doitincloud.authadmin.supports.ValidateUtils;
import doitincloud.commons.Utils;
import doitincloud.oauth2.services.TokenRevokeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("clientSecretServices")
public class ClientSecretOps implements ClientSecretServices {

    @Autowired
    TokenRevokeServices tokenRevokeServices;

    @Autowired
    private ClientJpaRepo clientJpaRepo;

    @Override
    public Client resetSecret(String id) {
        Optional<Client> optional = clientJpaRepo.findById(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("client not found for id: " + id);
        }
        Client client = optional.get();
        if (!tokenRevokeServices.revoke(id, "client_id")) {
            throw new InsufficientAuthenticationException("failed to revoke tokens");
        }

        String secret = Utils.generatePassword(16);
        client.setPlainTextSecret(secret);
        clientJpaRepo.save(client);

        String to = client.getContactPhoneNumber();
        Utils.sendTextMessage(client.getContactName(), client.getContactEmail(),
                "FYI: Your client access secret have been changed.");

        return client;
    }

    @Transactional
    @Override
    public Client setSecret(String id, String secret) {
        List<String> messages = new ArrayList<>();
        if (!ValidateUtils.isPasswordValid(secret, messages)) {
            String messageTemplate = messages.stream().collect(Collectors.joining(" "));
            throw new InvalidParameterException(messageTemplate);
        }
        Optional<Client> optional = clientJpaRepo.findByClientId(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("client not found for id: " + id);
        }
        Client client = optional.get();
        if (!tokenRevokeServices.revoke(id, "client_id")) {
            throw new InsufficientAuthenticationException("failed to revoke tokens");
        }

        client.setPlainTextSecret(secret);
        clientJpaRepo.save(client);
        client.setPlainTextSecret(null);

        String to = client.getContactPhoneNumber();
        Utils.sendTextMessage(client.getContactName(), client.getContactEmail(),
                "FYI: Your client access secret have been changed.");

        return client;
    }
}
