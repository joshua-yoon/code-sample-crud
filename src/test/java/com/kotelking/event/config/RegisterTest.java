package com.kotelking.event.config;

import com.kotelking.event.AppConfig;
import com.kotelking.event.exception.LimitReachedException;
import com.kotelking.event.model.Apply;
import com.kotelking.event.RegisterService;
import com.kotelking.event.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    public void serialRegister(){
        List<User> userList=new ArrayList<>();
        userList.add(new User());
        IntStream.iterate(0,i->i++)
                .limit(TEST_MORE)
                .forEach(i->unitRegister(userList));
        assertEquals(TEST_MAX,registerService.getCount());
        assertEquals(TEST_MAX,registerService.getApplies().size());
    }

    @Test
    public void randomRegister(){
        makeRandomApply().parallelStream().forEach(
                a->unitRegister(a.getAttendees()));
        assertEquals(TEST_MAX,registerService.getCount());
        assertEquals(TEST_MAX,registerService.getApplies().size());
    }

    @Test
    public void updateTest(){
        serialRegister();

        List<User> users=new ArrayList<>(); // 2 people update
        users.add(new User());

        Apply apply=new Apply();
        apply.setId(1);
        apply.setAttendees(users);
        registerService.update(apply);
    }


    @Test
    public void updateOverLimit(){
        serialRegister(); // Max 100, each 1 person register

        List<User> users=new ArrayList<>(); // 2 people update
        users.add(new User());
        users.add(new User());

        Apply apply=new Apply();
        apply.setId(1);
        apply.setAttendees(users);
        try {
            registerService.update(apply);
        }catch (LimitReachedException e){
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
        LOGGER.debug("applies count {}",registerService.getApplies().size());
        registerService.getApplies()
                .stream().limit(deleteCount).forEach(a->{


                LOGGER.debug("id {}",a.getId());
                registerService.remove(a.getId());
                }
        );

        Assert.assertEquals(count-deleteCount,registerService.getCount());

    }

    @Test
    public void randomCheckId(){
        serialRegister();
        int count=registerService.getCount();
        Random random=new Random();

        List<Apply> applyList=registerService.getApplies();
        IntStream.iterate(0,i->i++).limit(count)
                .parallel()
                .forEach(x->{
                    int savedId=random.nextInt(count);
                    Apply savedApply=applyList.get(savedId);
                    int checkId=savedApply.getId();
                    Apply checkApply=registerService.getApply(checkId);
                    Assert.assertEquals(savedApply,checkApply);

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
        List<User> userList=new ArrayList<>();
        Random random=new Random();
        IntStream.iterate(0,i->i++).limit(random.nextInt(1)+1)
                .forEach(i->userList.add(new User()));
        return userList;
    }

    private List<Apply> makeRandomApply(){
        List<Apply> applies=new ArrayList<>();
        IntStream.iterate(0,i->i++).limit(TEST_MORE)
                .forEach(
                        a->{
                            Apply apply=new Apply();
                            apply.setAttendees(makeRandomUsers());
                            applies.add(apply);
                        }
                );
        return applies;
    }

}
