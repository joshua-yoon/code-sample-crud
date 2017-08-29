package com.kotelking.event;

import com.kotelking.event.config.RepositoryConfig;
import com.kotelking.event.exception.ApiException;
import com.kotelking.event.model.Application;
import com.kotelking.event.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={
        AppConfig.class,
        RepositoryConfig.class})
public class RegisterTest {

    @Autowired
    private RegisterService registerService;

    private static final int TEST_MAX=RegisterService.MAX;
    private static final int TEST_MORE=(int) ((double)(RegisterService.MAX * 1.2));

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterTest.class);

    @Before
    public void prepare(){
        assertNotNull(registerService);
    }

    private List<User> makeUsers(int count){
        List<User> userList= new ArrayList<>();
        for (int i=0;i<count;i++){
            userList.add(new User());
        }
        return userList;
    }

    @Test
    public void serialRegister(){
        List<User> userList=makeUsers(1);
        IntStream.iterate(0,i->i++)
                .limit(TEST_MORE)
                .forEach(i->unitRegister(userList));
        assertEquals(TEST_MAX,registerService.getCount());
        assertEquals(TEST_MAX,registerService.getApplications().size());
    }

    @Test
    public void randomRegister(){
        makeRandomApply().parallelStream().forEach(
                a->unitRegister(a.getAttendees()));
        assertEquals(TEST_MAX,registerService.getCount());
        assertEquals(TEST_MAX,registerService.getApplications().size());
    }

    @Test
    public void updateTest(){
        serialRegister();
        registerService.update(Application.makeNew(1,makeUsers(1)));
    }

    @Test
    public void updateOverLimit(){
        serialRegister(); // Max 100, each 1 person register

        Application application = Application.makeNew(1,makeUsers(2)); // id : 1 , 2 people

        try {
            registerService.update(application);
        }catch (ApiException e){
            LOGGER.debug("{}",e);
             Assert.assertEquals(HttpStatus.BAD_REQUEST,e.getHttpStatus());
            return;
        }
        Assert.fail();
    }

    @Test
    public void deleteTest(){
        serialRegister();
        int count=registerService.getCount();
        Random random=new Random();

        int deleteCount=random.nextInt(count);

        LOGGER.debug("current count {} deleteCount {}",count, deleteCount);
        LOGGER.debug("applies count {}",registerService.getApplications().size());
        registerService.getApplications()
                .stream().limit(deleteCount).forEach(a->{


                LOGGER.debug("id {}",a.getId());
                registerService.delete(a.getId());
                }
        );

        Assert.assertEquals(count-deleteCount,registerService.getCount());

    }

    @Test
    public void deleteFailTest(){
        try {
            registerService.delete(Integer.MAX_VALUE);
        }catch (ApiException e){
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getHttpStatus());
            return;
        }
        Assert.fail();
    }

    @Test
    public void randomCheckId(){
        serialRegister();
        int count=registerService.getCount();
        Random random=new Random();

        List<Application> applicationList =registerService.getApplications();
        IntStream.iterate(0,i->i++).limit(count)
                .parallel()
                .forEach(x->{
                    int savedId=random.nextInt(count);
                    Application savedApplication = applicationList.get(savedId);
                    int checkId= savedApplication.getId();
                    Application checkApplication =registerService.getApplication(checkId);
                    Assert.assertEquals(savedApplication, checkApplication);

                });
    }

    private void unitRegister(List<User> users){
        try {
            registerService.register(users);
        }catch(Exception e){
            assertTrue(registerService.getCount() >= TEST_MAX);
        }
    }

    private List<User> makeRandomUsers(){

        Random random=new Random();
        return makeUsers(random.nextInt(2));
    }

    private List<Application> makeRandomApply(){
        List<Application> applies=new ArrayList<>();
        IntStream.iterate(0,i->i++).limit(TEST_MORE)
                .forEach(id-> Application.makeNew(id,makeRandomUsers()));
        return applies;
    }

}
