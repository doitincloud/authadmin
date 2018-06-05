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

        String dummyKey = "term-type-refreshable";

        put(dummyKey, recycleSecs / 4, new Refreshable() {
                    @Override
                    public Map<String, Object> call() throws Exception {

                        Iterable <Term> allTerms = termJpaRepo.findAll();
                        Map<String, Object> termTypeMap = new HashMap<>();
                        Iterator<Term> iterator = allTerms.iterator();
                        while (iterator.hasNext()) {
                            Term term = iterator.next();
                            String type = term.getType();
                            Map<String, Object> map = (Map<String, Object>) termTypeMap.get(type);
                            if (map == null) {
                                map = new HashMap<>();
                                termTypeMap.put(type, map);
                            }
                            map.put(term.getName(), Utils.toMap(term));
                        }

                        Map<String, Object> dummyMap = get(dummyKey);
                        if (dummyMap == null) {
                            dummyMap = new HashMap<>();
                        }

                        for (Map.Entry<String, Object> entry: termTypeMap.entrySet()) {
                            String key = entry.getKey();
                            Map<String, Object> map = (Map<String, Object>) entry.getValue();
                            dummyMap.put(key, map.size());
                            String cacheKey = "term-type::" + key;
                            Utils.getExcutorService().submit(() -> {
                                put(cacheKey, map, recycleSecs * 2);
                            });
                        }
                        return dummyMap;
                    }
                }
        );
    }
}
