package com.doitincloud.authadmin.supports;

import com.doitincloud.authadmin.repositories.ClientJpaRepo;
import com.doitincloud.authadmin.repositories.TermJpaRepo;
import com.doitincloud.authadmin.repositories.UserJpaRepo;
import com.doitincloud.authadmin.services.CacheOps;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;

public class AppCtx {

    private static ApplicationContext ctx;

    public static void setApplicationContext(ApplicationContext ctx) {
        AppCtx.ctx = ctx;
    }

    private static ClientJpaRepo clientJpaRepo;

    private static TermJpaRepo termJpaRepo;

    private static UserJpaRepo userJpaRepo;

    private static CacheOps cacheOps;

    public static ClientJpaRepo getClientJpaRepo() {
        if (clientJpaRepo == null && ctx != null) {
            clientJpaRepo = ctx.getBean(ClientJpaRepo.class);
        }
        return clientJpaRepo;
    }

    public static TermJpaRepo getTermJpaRepo() {
        if (termJpaRepo == null && ctx != null) {
            termJpaRepo = ctx.getBean(TermJpaRepo.class);
        }
        return termJpaRepo;
    }

    public static UserJpaRepo getUserJpaRepo() {
        if (userJpaRepo == null && ctx != null) {
            userJpaRepo = ctx.getBean(UserJpaRepo.class);
        }
        return userJpaRepo;
    }

    public static CacheOps getCacheOps() {
        if (cacheOps == null && ctx != null) {
            cacheOps = ctx.getBean(CacheOps.class);
        }
        return cacheOps;
    }
}
