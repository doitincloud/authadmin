package doitincloud.authadmin.services;

import com.google.common.base.Utf8;
import doitincloud.authadmin.models.Term;
import doitincloud.authadmin.repositories.TermJpaRepo;
import doitincloud.commons.LocalCache;
import doitincloud.commons.Refreshable;
import doitincloud.commons.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class CacheOps extends LocalCache {

    @Value("${cache.recycle_secs:600}")
    private Long recycleSecs;

    @Value("${cache.max_cache_size:512}")
    private Long maxCacheSize;

    @Value("${cache.max_secs_to_live:900}")
    private Long maxSecsToLive;

    @Autowired
    TermJpaRepo termJpaRepo;

    @EventListener
    public void handleEvent(ContextRefreshedEvent event) {
        setRecycleSecs(recycleSecs);
        setMaxCacheSize(maxCacheSize);
        setMaxSecsToLive(maxSecsToLive);
        if (cache == null) {
            initializeCache();
        }
    }

    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        setupTermTypeMap();
        start();
    }

    public void setupTermTypeMap() {

        String[] types = termJpaRepo.findDistinctByType();

        for (int i = 0; i < types.length; i++) {

            String type = types[i];
            String cacheKey = "term-type::" + type;

            put(cacheKey, recycleSecs * 11 / 10, new Refreshable() {
                @Override
                public Map<String, Object> call() throws Exception {
                    List<Term> terms = termJpaRepo.findByType(type);
                    Map<String, Object> map = new HashMap<>();
                    for (Term term: terms) {
                        map.put(term.getName(), Utils.toMap(term));
                    }
                    return map;
                }
            });
        }
    }
}
