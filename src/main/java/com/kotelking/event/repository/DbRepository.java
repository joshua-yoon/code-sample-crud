package com.kotelking.event.repository;

import com.kotelking.event.config.DBConfig;
import com.kotelking.event.model.Apply;
import com.kotelking.event.model.User;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class DbRepository implements RegisterRepository{

    private final SqlSession mockSession=new MockSession();
    private SqlSession registerSession;


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
    public boolean register(Apply apply) {

        int count=getSession().insert("register",apply);
        apply.getAttendees().forEach(a->getSession().insert("addUser",a));

        return count > 0;
    }

    @Override
    public Apply get(int id) {
        return new Apply();
    }


    @Override
    public Apply remove(int id) {
        return new Apply();
    }

    @Override
    public Apply update(Apply apply) {
        return apply;
    }

    @Override
    public List<Apply> getList() {
        return getSession().selectList("getApplies");
    }

    public List<User> getUsers(int applyNo){
        return getSession().selectList("getUsers",applyNo);
    }

}
