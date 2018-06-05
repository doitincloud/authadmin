package doitincloud.authadmin.repositories;

import doitincloud.authadmin.models.Term;
import doitincloud.authadmin.supports.ValidateUtils;

import java.util.*;

public class ClientExRepoImpl implements ClientExRepo {

    @Override
    public String[] getAuthorityOptions() {
        Set<Term> terms = ValidateUtils.getValidTermSet("client_role");
        String[] result = new String[terms.size()];
        int i = 0;
        for (Term term: terms) {
            result[i++] = term.getName();
        }
        return result;
    }

    @Override
    public String[]  getResourceIdOptions() {
        Set<Term> terms = ValidateUtils.getValidTermSet("resource_id");
        String[] result = new String[terms.size()];
        int i = 0;
        for (Term term: terms) {
            result[i++] = term.getName();
        }
        return result;
    }

    @Override
    public String[]  getGrantTypeOptions() {
        Set<Term> terms = ValidateUtils.getValidTermSet("grant_type");
        String[] result = new String[terms.size()];
        int i = 0;
        for (Term term: terms) {
            result[i++] = term.getName();
        }
        return result;
    }

    @Override
    public String[]  getScopeOptions() {
        Set<Term> terms = ValidateUtils.getValidTermSet("scope");
        String[] result = new String[terms.size()];
        int i = 0;
        for (Term term: terms) {
            result[i++] = term.getName();
        }
        return result;
    }

    @Override
    public String[]  getAutoApproveScopeOptions() {
        Set<Term> terms = ValidateUtils.getValidTermSet("scope");
        String[] result = new String[terms.size()];
        int i = 0;
        for (Term term: terms) {
            result[i++] = term.getName();
        }
        return result;
    }
}
