package com.runone;

import com.runone.utils.Neo4jHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;

public class TestNeo4j {

    private Neo4jHelper neo4jHelper;

    @Before
    public void setUp() {
        this.neo4jHelper = new Neo4jHelper();
    }

    @Test
    public void testCreatNode() {
        Driver driver = this.neo4jHelper.getConnnection();
        Session session = driver.session();
        StatementResult result = session.run("create (s:Student{sid:'1410300613',name:'zhaoliu',age:25})");
        while (result.hasNext()) {
            Record next = result.next();
            System.out.println(next);
        }
        session.close();
        this.neo4jHelper.close();
    }


    public void exeAndprintResult(String cql) {
        StatementResult result = this.neo4jHelper.execute(cql);
        while (result.hasNext()) {
            Record next = result.next();
            Value value = next.get("");
            System.out.println(next);
        }
    }

    @Test
    public void testMatch() {
        String cql = "Match(s:Student) return s.sid,s.name,s.age";
        this.exeAndprintResult(cql);
    }

    @Test
    public void testCreateRelation() {
//        String cql = "Match (a:Student{name:'steve'}),(b:Student{name:'jerry'})create (a)-[r:cooperate{type:'合作'}]->(b) return r";
        String cql = "Match (a:Dept{dname:'zhangsan'}),(b:Dept{dname:'lisi'}) create (a)-[r:Workmate{type:'同事'}]->(b) return r";
        this.exeAndprintResult(cql);
    }

    @Test
    public void testdeleteRelation() {
        String cql = "Match (a:Student{name:'steve'})-[r:cooperate{type:'合作'}]->(b:Student{name:'jerry'}) delete r";
        this.exeAndprintResult(cql);
    }

    @After
    public void after() {
        this.neo4jHelper.close();
    }


}
