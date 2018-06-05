package doitincloud.authadmin.controllers;

import doitincloud.authadmin.models.User;
import doitincloud.authadmin.services.UserPasswordServices;
import doitincloud.oauth2.supports.MissingRequiredDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserExController {

    @Autowired
    private UserPasswordServices userPasswordServices;

    @RequestMapping(value = "/v1/users/{id}/reset-password", method = RequestMethod.GET)
    @ResponseBody
    public User resetPassword(
            @PathVariable("id") String id) {
        return userPasswordServices.resetPassword(id);
    }

    @RequestMapping(value = "/v1/users/{id}/send-password-change-code", method = RequestMethod.GET)
    @ResponseBody
    public User sendPasswordChangeCode(
            @PathVariable("id") String id,
            @PathVariable Optional<Boolean> toPhoneOpt) {
        Boolean toPhone = false;
        if (toPhoneOpt.isPresent()) {
            toPhone = toPhoneOpt.get();
        }
        return userPasswordServices.sendPasswordChangeCode(id, toPhone);
    }

    @RequestMapping(value = "/v1/users/{id}/set-password", method = RequestMethod.POST)
    @ResponseBody
    public User setPassword(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> map) {
        if (!map.containsKey("code")) {
            throw new MissingRequiredDataException("missing code");
        }
        String code  = (String) map.get("code");
        if (!map.containsKey("password")) {
            throw new MissingRequiredDataException("missing password");
        }
        String password  = (String) map.get("password");
        return userPasswordServices.setPassword(id, code, password);
    }

    @RequestMapping(value = "/v1/users/{id}/change-password", method = RequestMethod.POST)
    @ResponseBody
    public User changePassword(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> map) {
        if (!map.containsKey("password")) {
            throw new MissingRequiredDataException("missing password");
        }
        String password  = (String) map.get("password");
        if (!map.containsKey("new-password")) {
            throw new MissingRequiredDataException("missing new-password");
        }
        String newPassword = (String) map.get("new-password");
        return userPasswordServices.changePassword(id, password, newPassword);
    }

    @Bean
    public ResourceProcessor<Resource<User>> userProcessor() {
        return new ResourceProcessor<Resource<User>>() {
            @Override
            public Resource<User> process(Resource<User> resource) {
                String userId = resource.getContent().getUserId();
                Link resetPasswordLink = linkTo(
                        methodOn(UserExController.class).resetPassword(userId)).
                        withRel("reset-password");
                resource.add(resetPasswordLink);
                Link sendPasswordChangeCodeLink = linkTo(
                        methodOn(UserExController.class).sendPasswordChangeCode(userId, null)).
                        withRel("send-password-change-code");
                resource.add(sendPasswordChangeCodeLink);
                Link setPasswordLink = linkTo(
                        methodOn(UserExController.class).setPassword(userId, null)).
                        withRel("set-password");
                resource.add(setPasswordLink);
                Link changePasswordLink = linkTo(
                        methodOn(UserExController.class).changePassword(userId, null)).
                        withRel("change-password");
                resource.add(changePasswordLink);
                return resource;
            }
        };
    }

}
