package com.kotelking.event.repository;


import com.kotelking.event.model.Apply;

import java.util.List;

public interface RegisterRepository {

    List<Apply> getList();
    boolean register(Apply apply);
    Apply get(int id);
    Apply remove(int id);
    Apply update(Apply apply);

}
