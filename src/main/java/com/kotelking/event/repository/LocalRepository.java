package com.kotelking.event.repository;

import com.kotelking.event.model.Application;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LocalRepository for single web server instance or a bucket
 */
@Component
public class LocalRepository implements RegisterRepository {


    private Map<Integer,Application> innerRepository=new ConcurrentHashMap<>();

    /**
     * Register Users in memory list
     * @param application
     * @return
     */
    @Override
    public boolean register(Application application) {

        innerRepository.put(application.getId(), application);
        return true;
    }

    @Override
    public Application get(int id) {
        return innerRepository.get(id);
    }

    @Override
    public Application remove(int id) {
        return innerRepository.remove(id);
    }

    @Override
    public Application update(Application application) {
        return innerRepository.put(application.getId(), application);
    }

    /**
     * Unmodifiable Registerd list
     * @return
     */
    @Override
    public List<Application> getList() {
        return Collections.unmodifiableList(new ArrayList<>(innerRepository.values()));
    }
}
