package com.kotelking.event.repository;

import com.kotelking.event.config.RepositoryConfig;
import com.kotelking.event.model.Application;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@Repository
public class MultiRepository implements RegisterRepository{

    private static final int THREAD_POOL_MIN_CORE_SIZE = 4;
    private static final int THREAD_POOL_MAX_SIZE = 24;
    private ThreadPoolTaskExecutor pool;

    private final List<RegisterRepository> registerRepositories;
    private final RegisterRepository priorityRepository;


    public MultiRepository(@Qualifier(RepositoryConfig.REPOSITORY_LIST) List<RegisterRepository> registerRepositories){

        this.registerRepositories=registerRepositories;
        priorityRepository=registerRepositories.get(0);
    }
    /**
     * Initialize thread pool
     * determine pool size by available processors
     * however it should considered by SQL server connection size
     *
     * @throws Exception
     */
    @PostConstruct
    public void init(){
        int maxPoolSize = Runtime.getRuntime().availableProcessors();
        int corePoolSize = 0;
        if (maxPoolSize < 1) {
            maxPoolSize = THREAD_POOL_MAX_SIZE;
        }
        corePoolSize = maxPoolSize / 2; //half
        if (corePoolSize < THREAD_POOL_MIN_CORE_SIZE) {
            corePoolSize = THREAD_POOL_MIN_CORE_SIZE;
        }
        pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.initialize();
    }


    @Override
    public List<Application> getList() {
        return priorityRepository.getList();
    }

    @Override
    public boolean register(Application application) {
        pool.submit(()->registerRepositories.forEach(r->r.register(application)));
        return true;
    }

    @Override
    public Application get(int id) {
        return priorityRepository.get(id);
    }

    @Override
    public Application remove(int id) {
        pool.submit(()->registerRepositories.forEach(r->r.remove(id)));
        return null;
    }

    @Override
    public Application update(Application application) {
        pool.submit(()->registerRepositories.forEach(r->r.update(application)));
        return null;
    }
}
