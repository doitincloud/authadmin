package doitincloud.authadmin.services;

import doitincloud.authadmin.models.User;
import doitincloud.authadmin.repositories.UserJpaRepo;
import doitincloud.authadmin.supports.ValidateUtils;
import doitincloud.commons.Utils;
import doitincloud.oauth2.services.TokenRevokeServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.beans.Transient;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

@Service("userPasswordServices")
public class UserPasswordOps implements UserPasswordServices {

    @Autowired
    TokenRevokeServices tokenRevokeServices;

    @Autowired
    private UserJpaRepo userJpaRepo;

    @Transactional
    @Override
    public User resetPassword(String id) {
        Optional<User> optional = userJpaRepo.findByUserId(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("user not found for id: " + id);
        }
        User user = optional.get();
        String password = Utils.generatePassword(16);
        user.setPlainTextPassword(password);
        user.removeAdditionalProperty("password-change-verification-code");
        userJpaRepo.save(user);

        Utils.sendTextMessage(user.getFirstName() + " " + user.getLastName(),
                user.getUsername(),
                "FYI: Your password has been changed.");
        return user;
    }

    @Transactional
    @Override
    public User sendPasswordChangeCode(String id, boolean toPhone) {

        Optional<User> optional = userJpaRepo.findByUserId(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("user not found for id: " + id);
        }
        User user = optional.get();
        String code = Utils.generateNumericCode(6);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("timestamp", System.currentTimeMillis());
        map.put("code", code);
        user.setAdditionalProperty("password-change-verification-code", map);
        userJpaRepo.save(user);

        String to = user.getUsername();
        if (toPhone && user.getPhoneNumber() != null) {
            to = user.getPhoneNumber();
        }
        Utils.sendTextCode(user.getFirstName() + " " + user.getLastName(), to, code);
        return user;
    }

    @Transactional
    @Override
    public User setPassword(String id, String code, String password) {
        List<String> messages = new ArrayList<>();
        if (!ValidateUtils.isPasswordValid(password, messages)) {
            String messageTemplate = messages.stream().collect(Collectors.joining(" "));
            throw new InvalidParameterException(messageTemplate);
        }
        Optional<User> optional = userJpaRepo.findByUserId(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("user not found for id: " + id);
        }
        User user = optional.get();
        Map<String, Object> verification = (Map<String, Object>) user.getAdditionalProperty("password-change-verification-code");
        if (verification == null) {
            throw new InsufficientAuthenticationException("not password change verification found");
        }
        Long timestamp = (Long) verification.get("timestamp");
        if (timestamp - 300000 > System.currentTimeMillis()) { // 5 minutes ago
            user.removeAdditionalProperty("password-change-verification-code");
            userJpaRepo.save(user);
            throw new InsufficientAuthenticationException("password change verification expired");
        }
        String changeCode = (String) verification.get("code");
        if (!code.equals(changeCode)) {
            user.removeAdditionalProperty("password-change-verification-code");
            userJpaRepo.save(user);
            throw new InsufficientAuthenticationException("password change code not match");
        }
        if (!tokenRevokeServices.revoke(user.getUsername(), "username")) {
            throw new InsufficientAuthenticationException("failed to revoke tokens");
        }

        user.setPlainTextPassword(password);
        user.removeAdditionalProperty("password-change-verification-code");
        userJpaRepo.save(user);

        Utils.sendTextMessage(user.getFirstName() + " " + user.getLastName(),
                user.getUsername(),
                "FYI: Your password has been changed.");
        return user;
    }

    @Transactional
    @Override
    public User changePassword(String id, String password, String new_password) {
        List<String> messages = new ArrayList<>();
        if (!ValidateUtils.isPasswordValid(new_password, messages)) {
            String messageTemplate = messages.stream().collect(Collectors.joining(" "));
            throw new InvalidParameterException(messageTemplate);
        }
        Optional<User> optional = userJpaRepo.findByUserId(id);
        if (!optional.isPresent()) {
            throw new ResourceNotFoundException("user not found for id: " + id);
        }
        User user = optional.get();
        PasswordEncoder encoder = Utils.passwordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            throw new InsufficientAuthenticationException("password not match");
        }
        if (!tokenRevokeServices.revoke(user.getUsername(), "username")) {
            throw new InsufficientAuthenticationException("failed to revoke tokens");
        }

        user.setPlainTextPassword(new_password);
        userJpaRepo.save(user);
        user.setPlainTextPassword(null);

        Utils.sendTextMessage(user.getFirstName() + " " + user.getLastName(),
                user.getUsername(),
                "FYI: Your password has been changed.");
        return user;
    }
}
