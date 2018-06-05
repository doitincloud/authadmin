package doitincloud.authadmin.supports;

import doitincloud.commons.Utils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class IdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Newable newable = (Newable) object;
        newable.renew();
        return Utils.generateId();
    }
}
