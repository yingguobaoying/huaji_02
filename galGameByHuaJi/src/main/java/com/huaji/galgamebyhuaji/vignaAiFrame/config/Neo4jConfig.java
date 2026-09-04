package com.huaji.galgamebyhuaji.vignaAiFrame.config;

import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;

@org.springframework.context.annotation.Configuration
public class Neo4jConfig {
    
    @Bean
    public Configuration cypherDslConfiguration() {
        return Configuration.newConfig()
                .withDialect(Dialect.NEO4J_5) // 显式指定使用 Neo4j 5 的方言
                .build();
    }
    
    @Bean
    public Neo4jTransactionManager transactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}
