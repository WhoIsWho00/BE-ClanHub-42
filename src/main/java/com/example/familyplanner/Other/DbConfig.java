package com.example.familyplanner.Other;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@EnableTransactionManagement
public class DbConfig {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String dialect;
    @Value("${spring.jpa.show-sql}")
    private String showSql;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int hikariMaximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int hikariMinimumIdle;

    @Value("${spring.datasource.hikari.connection-timeout}")
    private long hikariConnectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout}")
    private long hikariIdleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime}")
    private long hikariMaxLifeTime;

    @Value("${spring.datasource.hikari.pool-name}")
    private String hikariPoolName;

    @Value("${spring.datasource.hikari.connection-test-query}")
    private String hikariConnectionTestQuery;

    @Bean
    public DataSource datasource() {
        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);

        config.setMaximumPoolSize(hikariMaximumPoolSize); // Adjust based on your needs
        config.setMinimumIdle(hikariMinimumIdle);
        config.setConnectionTimeout(hikariConnectionTimeout);
        config.setMaxLifetime(hikariMaxLifeTime);
        config.setIdleTimeout(hikariIdleTimeout);
        config.setConnectionTestQuery(hikariConnectionTestQuery);
        config.setPoolName(hikariPoolName);
        log.info("Data source created");

        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }


    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(datasource());
        sessionFactory.setPackagesToScan("com.spring.boot.learning"); // Replace with your package name
        sessionFactory.setHibernateProperties(hibernateProperties());
        log.info("session factory created");
        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.show_sql", showSql);
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        log.info("Hibernate properties created");
        return properties;
    }
}
