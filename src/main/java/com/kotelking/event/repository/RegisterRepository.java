package com.kotelking.event.repository;


import com.kotelking.event.model.Apply;

import java.util.List;

public interface RegisterRepository {

    boolean register(Apply apply);
    List<Apply> getList();

}
