package com.kotelking.event.repository;

import com.kotelking.event.model.Application;
import com.kotelking.event.model.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DbRepository implements RegisterRepository{

    private final SqlSession mockSession=new MockSession();
    private SqlSession registerSession;

// Comment out for a test
//    @Autowired
//    public void setSession(@Qualifier(DBConfig.DBTargets.REGISTER) SqlSession sqlSession){
//        registerSession=sqlSession;
//    }

    private SqlSession getSession(){

        if (registerSession == null)
            return mockSession;
        return registerSession;
    }

    @Override
    public boolean register(Application application) {

        int count=getSession().insert("register", application);
        application.getAttendees().forEach(a->getSession().insert("addUser",a));

        return count > 0;
    }

    @Override
    public Application get(int id) {
        return new Application();
    }


    @Override
    public Application remove(int id) {
        return new Application();
    }

    @Override
    public Application update(Application application) {
        return application;
    }

    @Override
    public List<Application> getList() {
        return getSession().selectList("getApplications");
    }

    public List<User> getUsers(int applyNo){
        return getSession().selectList("getUsers",applyNo);
    }

}
