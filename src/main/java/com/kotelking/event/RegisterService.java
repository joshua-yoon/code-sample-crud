package com.kotelking.event;

import com.kotelking.event.config.RepositoryConfig;
import com.kotelking.event.exception.ApiException;
import com.kotelking.event.model.Application;
import com.kotelking.event.model.User;
import com.kotelking.event.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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


    private final RegisterRepository registerRepository;

    @Autowired
    public RegisterService(@Qualifier(RepositoryConfig.MULTI) RegisterRepository registerRepository){

        this.registerRepository=registerRepository;
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
     * Register Event Application
     * @param users
     */
    public Application register(final List<User> users) {
        if (isFull)
            throw new ApiException("선착순 신청이 마감되었습니다.",HttpStatus.BAD_REQUEST);

        int userCount=users.size();
        if (!isAvailable(userCount))   //check before get a lock
            throw new ApiException(userCount+"명 신청이 불가능합니다.", HttpStatus.BAD_REQUEST);

        final int newCount=increseUserCount(users.size());
        Application application = Application.makeNew(newCount,users);

        registerRepository.register(application);
        return application;
    }

    /**
     * Update Application
     * @param application
     * @return
     */
    public Application update(Application application){

        Application old=registerRepository.get(application.getId());
        int oldSize=old.getAttendees().size();
        int newSize= application.getAttendees().size();

        if (oldSize > newSize){
            decreaseUserCount(oldSize - newSize);
        }else if (oldSize < newSize){
            increseUserCount(newSize - oldSize);
        }
        registerRepository.update(application);
        return old;

    }

    /**
     * Get Application with application id
     * @param id
     * @return
     */
    public Application getApplication(int id){
        return registerRepository.get(id);
    }

    /**
     * Cancel Application, delete from list
     * @param id
     * @return
     */
    public Application delete(int id){

        Application application =registerRepository.get(id);
        if (application == null)
            throw new ApiException("존재하지 않는 신청서입니다",HttpStatus.NOT_FOUND);

        decreaseUserCount(application.getAttendees().size());
        registerRepository.remove(id);
        return application;
    }

    /**
     * Get Application Count
     * @return
     */
    public int getCount(){
        return count;
    }

    /**
     * get All Application List
     * @return
     */
    public List<Application> getApplications(){
        return registerRepository.getList(); // Uses Cached List
    }


    /**
     * Increase Registration Count
     * 3 operations should be synchronized
     * @param userCount
     */
    private synchronized int increseUserCount(int userCount){

        if (!isAvailable(userCount)) //check again after get a lock
            throw new ApiException(userCount+"명 신청이 불가능합니다",HttpStatus.BAD_REQUEST);
        count+=userCount;

        if (count == MAX){
            isFull=true;
        }
        return count;
    }

    /**
     * Decrease Registration Count
     * @param count
     * @return
     */
    private synchronized int decreaseUserCount(int count){
        this.count-=count;

        if (isFull)
            isFull=false;
        return this.count;
    }


}
