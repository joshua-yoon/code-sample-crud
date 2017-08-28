package com.kotelking.event.config;

import com.kotelking.event.repository.DbRepository;
import com.kotelking.event.repository.LocalRepository;
import com.kotelking.event.repository.MultiRepository;
import com.kotelking.event.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ComponentScan
@Configuration
public class RepositoryConfig {

    public static final String REPOSITORY_LIST="RepositoryList";

    public static final String LOCAL = "localRepository";
    public static final String DB = "dbRepository";
    public static final String MULTI = "multiRepository";

    @Bean
    @Qualifier(LOCAL)
    public RegisterRepository localRepository() {
        return new LocalRepository();
    }

    @Bean
    @Qualifier(DB)
    public RegisterRepository dbRepository() {
        return new DbRepository();
    }

    @Bean
    @Qualifier(MULTI)
    public RegisterRepository multiRepository(@Qualifier(REPOSITORY_LIST) List<RegisterRepository> repositories) {
        return new MultiRepository(repositories);
    }

    @Bean
    @Qualifier(REPOSITORY_LIST)
    public List<RegisterRepository> repositories(
            @Qualifier(LOCAL) RegisterRepository local,
            @Qualifier(DB) RegisterRepository db){
        List<RegisterRepository> registerRepositories=new ArrayList<>();
        registerRepositories.add(local);
        registerRepositories.add(db);
        return Collections.unmodifiableList(registerRepositories);
    }



}
