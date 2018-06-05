package doitincloud.authadmin.supports;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import doitincloud.authadmin.models.Term;

import doitincloud.commons.Utils;
import org.passay.*;

import java.util.*;

public class ValidateUtils {

    public static boolean isPhoneNumberValid(String number, String defaultRegion, List<String> messages) {
        if (number == null || number.length() == 0) {
            if (messages != null) {
                messages.add("Phone number can not be empty.");
            }
            return false;
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneNumberUtil.parse(number, defaultRegion);
            if (phoneNumberUtil.isValidNumber(numberProto)) {
                return true;
            }
            if (messages != null) {
                messages.add("invalid phone number");
            }
        } catch (NumberParseException e) {
            if (messages != null) {
                messages.add(e.getMessage());
            }
        }
        return false;
    }

    public static boolean isPasswordValid(String password, List<String> messages) {
        if (password == null || password.length() == 0) {
            // password can be empty
            return true;
        }
        PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(
                // at least 6 characters
                new LengthRule(6, 32),
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // no whitespace
                new WhitespaceRule()
        ));
        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        if (messages != null) {
            List<String> list = passwordValidator.getMessages(result);
            messages.addAll(list);
        }
        return false;
    }

    public static boolean isTermSetValid(Set<String> set, String type, List<String> messages) {
        if (set == null || set.size() == 0) {
            if (messages != null) {
                messages.add(type + " can not be empty.");
            }
            return false;
        }
        Map<String, Term> termMap = getTermTypeMap(type);
        if (termMap.size() == 0) {
            if (messages != null) {
                messages.add("unknown type " + type + " not found from database.");
            }
            return false;
        }
        Set<String> roles = AuthCtx.getUserAuthorities();
        Map<String, Object> userRolesOptions = getUserRolesOptions(roles);

        for (String name : set) {
            Term term = termMap.get(name);
            if (term == null) {
                if (messages == null) {
                    return false;
                } else {
                    messages.add("unknown entry " + name + " not found from database.");
                }
            } else if (!userRolesOptions.containsKey("ALL")) {
                if (type.equals("scope")) {
                    Set<String> scopes = AuthCtx.getScope();
                    if (scopes == null || !scopes.contains(name)) {
                        if (messages == null) {
                            return false;
                        } else {
                            messages.add(name + " not allowed by current access token.");
                        }
                    }
                } else if (type.equals("resource_id")) {
                    Set<String> resourceIds = AuthCtx.getResourceIds();
                    if (resourceIds == null || !resourceIds.contains(name)) {
                        if (messages == null) {
                            return false;
                        } else {
                            messages.add(name + " not allowed by current access token.");
                        }
                    }
                } else if (type.endsWith("_role")) {
                    if (!userRolesOptions.containsKey(name)) {
                        if (messages == null) {
                            return false;
                        } else {
                            messages.add(name + " not allowed by owner meta data.");
                        }
                    }
                }
            } else if (term.getVisibleByRoles() != null) {
                Set<String> visibleByRoles = term.getVisibleByRoles();
                boolean visible = false;
                for (String role : roles) {
                    if (visibleByRoles.contains(role)) {
                        visible = true;
                        break;
                    }
                }
                if (!visible) {
                    if (messages == null) {
                        return false;
                    } else {
                        messages.add(name + " is not permitted.");
                    }
                }
            }
        }
        if (messages == null || messages.size() == 0) {
            return true;
        }
        return false;
    }

    public static Set<Term> getValidTermSet(String type) {
        Set<Term> terms = new HashSet<>();
        Map<String, Term> termMap = getTermTypeMap(type);
        if (termMap.size() == 0) {
            return terms;
        }
        Set<String> roles = AuthCtx.getUserAuthorities();
        Map<String, Object> userRolesOptions = getUserRolesOptions(roles);

        termMap.forEach((name, term) -> {
            if (userRolesOptions.containsKey("ALL")) {
                terms.add(term);
                return;
            }
            if (type.equals("scope")) {
                Set<String> scopes = AuthCtx.getScope();
                if (scopes != null && scopes.contains(name)) {
                    terms.add(term);
                    return;
                }
            }
            if (type.equals("resource_id")) {
                Set<String> resourceIds = AuthCtx.getResourceIds();
                if (resourceIds != null && resourceIds.contains(name)) {
                    terms.add(term);
                    return;
                }
            }
            if (type.endsWith("_role")) {
                if (userRolesOptions.containsKey(name)) {
                    terms.add(term);
                    return;
                }
            }
            Set<String> visibleByRoles = term.getVisibleByRoles();
            for (String role : roles) {
                if (visibleByRoles.contains(role)) {
                    terms.add(term);
                    return;
                }
            }
        });
        return terms;
    }

    public static Map<String, Term> getTermTypeMap(String type) {
        String cacheKey = "term-type::" + type;
        Map<String, Term> termMap = new HashMap<>();
        Map<String, Object> map = AppCtx.getCacheOps().get(cacheKey);
        if (map == null) {
            return termMap;
        }
        map.forEach((k, v) -> {
            termMap.put(k, Utils.toPojo((Map<String, Object>) v, Term.class));
        });
        return termMap;
    }

    public static Map<String, Object> getUserRolesOptions(Set<String> roles) {
        String cacheKey = "role-options::" + AuthCtx.getAccessToken();
        Map<String, Object> map = AppCtx.getCacheOps().get(cacheKey);
        if (map == null) {
            Map<String, Term> userRoles = getTermTypeMap("user_role");
            map = new HashMap<>();
            for (String role : roles) {
                Term term = userRoles.get(role);
                if (term == null ) {
                    continue;
                }
                Set<String> options = term.getRoleOptions();
                for (String option : options) {
                    map.put(option, "");
                }
            }
            AppCtx.getCacheOps().put(cacheKey, map, 60L);
        }
        return map;
    }
}