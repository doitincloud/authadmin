package doitincloud.authadmin.repositories;

import doitincloud.authadmin.models.Term;
import doitincloud.authadmin.supports.ValidateUtils;

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
