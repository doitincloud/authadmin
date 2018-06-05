package doitincloud.authadmin.configs;

import doitincloud.authadmin.supports.AppCtx;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AppContextAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppCtx.setApplicationContext(applicationContext);
    }
}
