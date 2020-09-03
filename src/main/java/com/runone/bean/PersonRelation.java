package com.runone.bean;

import com.runone.annotation.EsFieldType;
import com.runone.annotation.EsObject;
import com.runone.annotation.Field;

@EsObject(indexName = "relation", type = "_doc", shards = 10, replicas = 0)
public class PersonRelation {

    @Field(type = EsFieldType.Keyword)
    private String pid;

    @Field(type = EsFieldType.Text)
    private String name;

    @Field(type = EsFieldType.Text)
    private String disambiguation;

    @Field(type = EsFieldType.Keyword)
    private String local_photo_url;

    @Field(type = EsFieldType.Keyword)
    private String from_photo_url;

    @Field(type = EsFieldType.Text)
    private String relations;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisambiguation() {
        return disambiguation;
    }

    public void setDisambiguation(String disambiguation) {
        this.disambiguation = disambiguation;
    }

    public String getLocal_photo_url() {
        return local_photo_url;
    }

    public void setLocal_photo_url(String local_photo_url) {
        this.local_photo_url = local_photo_url;
    }

    public String getFrom_photo_url() {
        return from_photo_url;
    }

    public void setFrom_photo_url(String from_photo_url) {
        this.from_photo_url = from_photo_url;
    }

    public String getRelations() {
        return relations;
    }

    public void setRelations(String relations) {
        this.relations = relations;
    }

}
