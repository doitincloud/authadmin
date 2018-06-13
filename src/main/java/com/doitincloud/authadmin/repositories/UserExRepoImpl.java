package com.doitincloud.authadmin.repositories;

import com.doitincloud.authadmin.models.Term;
import com.doitincloud.authadmin.supports.ValidateUtils;

import java.util.Set;

public class UserExRepoImpl implements UserExRepo {
    @Override
    public String[] getAuthorityOptions() {
        Set<Term> terms = ValidateUtils.getValidTermSet("user_role");
        String[] result = new String[terms.size()];
        int i = 0;
        for (Term term: terms) {
            result[i++] = term.getName();
        }
        return result;
    }
}
