package com.kotelking.event;

import com.kotelking.event.config.RepositoryConfig;
import com.kotelking.event.model.Apply;
import com.kotelking.event.model.User;
import com.kotelking.event.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegisterService {

    /**
     * Total count of Applied User
     * this count is total number of a web server
     *
     * It can be bucket count when use multi web server
     */
    private volatile int count;

    private volatile boolean isFull;

    public static final int MAX = 100;

    private static final int THREAD_POOL_MIN_CORE_SIZE = 4;
    private static final int THREAD_POOL_MAX_SIZE = 24;
    private ThreadPoolTaskExecutor pool;
    private final List<RegisterRepository> registerRepositories;

    @Autowired
    public RegisterService(@Qualifier(RepositoryConfig.REPOSITORY_LIST) List<RegisterRepository> registerRepositories){

        this.registerRepositories=registerRepositories;
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

    /**
     * User Count check if it is available
     * @param userCount
     * @return
     */
    private boolean isAvailable(int userCount){
        return count + userCount <= MAX;
    }

    /**
     * Register Users
     * @param users
     */
    public void register(final List<User> users) {
        if (isFull)
            throw new LimitReachedException();

        int userCount=users.size();
        if (!isAvailable(userCount))   //check before get a lock
            throw new LimitReachedException(userCount);

        final int newCount=increseUserCount(users);
        final LocalDateTime now=LocalDateTime.now();
        pool.submit(()->
        {
            Apply apply=new Apply();
            apply.setId(newCount);
            apply.setAttendees(users);
            apply.setAppliedTime(now);
            users.forEach(u->u.setId(newCount));
            registerRepositories.forEach(r->r.register(apply));
        }); //submit apply request
    }

    /**
     * Get Applied Count
     * @return
     */
    public int getCount(){
        return count;
    }

    /**
     * Get All Applied List
     * @return
     */
    public List<Apply> getApplies(){
        return registerRepositories.get(0).getList(); // Uses Cached List
    }

    /**
     * Increase Applied User Count
     * 3 operations should be synchronized
     * @param users
     */
    private synchronized int increseUserCount(List<User> users){
        int userCount=users.size();
        if (!isAvailable(userCount)) //check again after get a lock
            throw new LimitReachedException(userCount);
        count+=userCount;

        if (count == MAX){
            isFull=true;
        }
        return count;
    }


}