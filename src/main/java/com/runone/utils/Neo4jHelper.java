package com.runone.utils;


import org.neo4j.driver.v1.*;
import org.springframework.stereotype.Component;

/**
 * Neo4j操作图形数据库
 */
@Component
public class Neo4jHelper {

    private final String uri = "bolt://192.168.10.101:7687";
    private final String username = "neo4j";
    private final String password = "runone2016";

    private Driver driver;

    public Driver getConnnection() {
        if (this.driver == null) {
            this.driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        }
        return this.driver;
    }

    public void close() {
        this.driver.close();
    }

    public StatementResult execute(String cql) {
        Driver driver = this.getConnnection();
        Session session = driver.session();
        StatementResult result = session.run(cql);
        session.close();
        return result;
    }


}
