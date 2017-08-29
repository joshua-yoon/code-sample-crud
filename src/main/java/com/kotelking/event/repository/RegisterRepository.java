package com.kotelking.event.repository;


import com.kotelking.event.model.Application;

import java.util.List;

public interface RegisterRepository {

    /**
     * get Application List
     * @return
     */
    List<Application> getList();

    /**
     * Register Application
     * @param application
     * @return
     */
    boolean register(Application application);

    /**
     * Get Application with ID
     * @param id
     * @return
     */
    Application get(int id);

    /**
     * remove Application from List
     * @param id
     * @return
     */
    Application remove(int id);

    /**
     * Update Application with new form
     * @param application
     * @return
     */
    Application update(Application application);

}
