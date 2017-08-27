package com.kotelking.event.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Java Configurations for Database connection resources.
 *
 * DB properties are loaded from the 'mysql.properties' file of the active profile.
 */
@Configuration
@PropertySources({
@PropertySource("classpath:rdbms/mysql.properties")})
@ComponentScan
public class DBConfig {

    public static class DBTargets {
        public static final String REGISTER = "register";

    }


    @Bean
    @Qualifier(DBTargets.REGISTER)
    public DataSource registerDataSource() {
        params.setDatabaseName(DBTargets.REGISTER);
        return dataSource(params);
    }


    /**
     * In the @Bean case, the name is "txManager" (per the name of the method); data the XML case,
     * the name is "transactionManager".
     */
    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(registerDataSource());
    }

    @Bean
    @Qualifier(DBTargets.REGISTER)
    public SqlSession registerSession(@Qualifier(DBTargets.REGISTER) DataSource src) {
        return sqlSession(src);
    }


    private final Logger LOG = LoggerFactory.getLogger(DBConfig.class);

    @Autowired
    private Params params;

    @Configuration
    @PropertySource("classpath:rdbms/mysql.properties") // annotation approach, XML-less
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    static class Params {
        @Autowired
        private Environment env;

        @Value("${driverClassName}") String driverClassName;

        @Value("${datasrc.default.username}") String username;
        @Value("${datasrc.default.password}") String password;

        @Value("${server}") String serverName;
        @Value("${server.parameters}") String params;

        /* default values */

        @Value("${datasrc.default.initialSize}") int initialSize;
        @Value("${datasrc.default.maxWait}") long maxWait;
        @Value("${datasrc.default.maxActive}") int maxActive;
        @Value("${datasrc.default.validationQuery}") String validationQuery;

        @Value("${datasrc.default.testOnBorrow}") boolean testOnBorrow;
        @Value("${datasrc.default.testOnReturn}") boolean testOnReturn;
        @Value("${datasrc.default.testWhileIdle}") boolean testWhileIdle;

        @Value("${datasrc.default.maxIdle}") int maxIdle;
        @Value("${datasrc.default.minIdle}") int minIdle;
        @Value("${datasrc.default.timeBetweenEvictionRunsMillis}")
        long timeBetweenEvictionRunsMillis;

        private @Value("${server.url_format}") String urlFormat;
        private @Value("${dbname_format}") String dbNameFormat;

        private String dbName;

        /**
         * Set the database-specific values:
         */
        void setDatabaseName(String name) {
            dbName = name;

            // Override the default values:

            initialSize = env.getProperty(name("initialSize"), Integer.class, initialSize);
            maxWait = env.getProperty(name("initialSize"), Long.class, maxWait);
            maxActive = env.getProperty(name("initialSize"), Integer.class, maxActive);
            validationQuery = env.getProperty(name("initialSize"), validationQuery);

            testOnBorrow = env.getProperty(name("testOnBorrow"), Boolean.class, testOnBorrow);
            testOnReturn = env.getProperty(name("testOnReturn"), Boolean.class, testOnReturn);
            testWhileIdle = env.getProperty(name("testWhileIdle"), Boolean.class, testWhileIdle);

            maxIdle = env.getProperty(name("maxIdle"), Integer.class, maxIdle);
            minIdle = env.getProperty(name("minIdle"), Integer.class, minIdle);
            timeBetweenEvictionRunsMillis = env.getProperty(name("timeBetweenEvictionRunsMillis"),
                    Long.class, timeBetweenEvictionRunsMillis);

            username = env.getProperty(name("username"), username);
            password = env.getProperty(name("password"), password);
        }

        /**
         * Composes the url with the url and rdbms name.
         *
         * @return the url string.
         */
        String getUrl() {
            return String.format(urlFormat, dbName);
        }

        private String name(String property) {
            return String.format(dbNameFormat, dbName, property);
        }

        @Bean
        static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    /**
     * Generates a DataSource object for the specific database.
     *
     * @param p parameters for the specific database connection.
     */
    private DataSource dataSource(final Params p) {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(p.driverClassName);
        dataSource.setUrl(p.getUrl());
        dataSource.setUsername(p.username);
        dataSource.setPassword(p.password);
        dataSource.setInitialSize(p.initialSize);
        dataSource.setMaxWaitMillis(p.maxWait);
        dataSource.setMaxTotal(p.maxActive);
        dataSource.setValidationQuery(p.validationQuery);
        dataSource.setTestOnBorrow(p.testOnBorrow);
        dataSource.setTestOnReturn(p.testOnReturn);
        dataSource.setTestWhileIdle(p.testWhileIdle);
        dataSource.setMaxIdle(p.maxIdle);
        dataSource.setMinIdle(p.minIdle);
        dataSource.setTimeBetweenEvictionRunsMillis(p.timeBetweenEvictionRunsMillis);
        dataSource.setDefaultReadOnly(false);

        return dataSource;
    }

    /**
     * Generates a SqlSessionFactoryBean for MyBatis.
     *
     * Check if the paths for MyBatis config and mapper files are correct.
     */
    private SqlSessionFactoryBean sqlSessionFactory(DataSource src) throws Exception {
        final SqlSessionFactoryBean bean = new SqlSessionFactoryBean();

        bean.setDataSource(src);
        bean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mybatis/mapper/*.xml"));

        return bean;
    }

    private SqlSession sqlSession(DataSource src) {
        try {
            final SqlSessionFactoryBean factory = sqlSessionFactory(src);
            return new SqlSessionTemplate(factory.getObject());
        } catch (Exception e) {
            LOG.error("failed to make a SQL session.", e);
        }

        return null;
    }
}