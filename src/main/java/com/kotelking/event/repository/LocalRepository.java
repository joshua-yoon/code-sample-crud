package com.kotelking.event.repository;

import com.kotelking.event.model.Apply;
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


    private Map<Integer,Apply> innerRepository=new ConcurrentHashMap<>();

    /**
     * Register Users in memory list
     * @param apply
     * @return
     */
    @Override
    public boolean register(Apply apply) {

        innerRepository.put(apply.getId(),apply);
        return true;
    }

    @Override
    public Apply get(int id) {
        return innerRepository.get(id);
    }

    @Override
    public Apply remove(int id) {
        return innerRepository.remove(id);
    }

    @Override
    public Apply update(Apply apply) {
        return innerRepository.put(apply.getId(),apply);
    }

    /**
     * Unmodifiable Registerd list
     * @return
     */
    @Override
    public List<Apply> getList() {
        return Collections.unmodifiableList(new ArrayList<>(innerRepository.values()));
    }
}
