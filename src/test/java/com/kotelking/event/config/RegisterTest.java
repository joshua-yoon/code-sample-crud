package com.kotelking.event.config;

import com.kotelking.event.AppConfig;
import com.kotelking.event.model.Apply;
import com.kotelking.event.RegisterService;
import com.kotelking.event.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    }

    @Test
    public void randomRegister(){
        makeRandomApply().parallelStream().forEach(
                a->unitRegister(a.getAttendees()));
        assertEquals(TEST_MAX,registerService.getCount());
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
