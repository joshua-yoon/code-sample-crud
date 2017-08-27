package com.kotelking.event.repository;

import com.kotelking.event.model.Apply;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LocalRepository for single web server instance or a bucket
 */
@Component
public class LocalRepository implements RegisterRepository {

    private List<Apply> registeredList = Collections.synchronizedList(new ArrayList<>());

    /**
     * Register Users in memory list
     * @param apply
     * @return
     */
    @Override
    public boolean register(Apply apply) {
        return registeredList.add(apply);
    }

    /**
     * Unmodifiable Registerd list
     * @return
     */
    @Override
    public List<Apply> getList() {
        return Collections.unmodifiableList(registeredList);
    }
}
