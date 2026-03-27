package com.ait.deliveri.db.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "aitEntityManagerFactory", 
		transactionManagerRef = "aitTransactionManager", 
		basePackages = {"com.ait.deliveri.db.repository" }
		)
public class LogisticaDbConfig {

	@Primary
    @Bean(name = "aitDataSource")
    @ConfigurationProperties(prefix = "ait.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "aitEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean aitEntityManagerFactory(EntityManagerFactoryBuilder builder,
            @Qualifier("aitDataSource") DataSource dataSource) {

        return builder.dataSource(dataSource).packages("com.ait.deliveri.db.entity")
                .persistenceUnit("ait").build();

    }
    
	@Primary
    @Bean(name = "aitTransactionManager")
    public PlatformTransactionManager aitTransactionManager(
            @Qualifier("aitEntityManagerFactory") EntityManagerFactory aitEntityManagerFactory) {
        return new JpaTransactionManager(aitEntityManagerFactory);
    }
}
