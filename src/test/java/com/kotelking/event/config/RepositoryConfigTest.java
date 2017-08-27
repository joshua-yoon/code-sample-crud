package com.kotelking.event.config;

import com.kotelking.event.repository.DbRepository;
import com.kotelking.event.repository.LocalRepository;
import com.kotelking.event.repository.RegisterRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepositoryConfig.class})
public class RepositoryConfigTest {

    @Autowired
    @Qualifier(RepositoryConfig.LOCAL)
    private RegisterRepository local;

    @Autowired
    @Qualifier(RepositoryConfig.DB)
    private RegisterRepository db;

    @Autowired
    @Qualifier(RepositoryConfig.REPOSITORY_LIST)
    private List<RegisterRepository> list;



    @Before
    public void beanCheck(){
        assertNotNull(local);
        assertNotNull(db);
        assertNotNull(list);
    }

    @Test
    public void localRepository(){

        assertTrue(local instanceof LocalRepository);
    }

    @Test
    public void dbRepository(){
        assertTrue(db instanceof DbRepository);
    }

    private boolean checkInList(RegisterRepository registerRepository){
        return registerRepository instanceof  LocalRepository ||
                registerRepository instanceof DbRepository;
    }

    @Test
    public void list(){
        list.forEach(r->assertTrue(checkInList(r)));
    }
}
