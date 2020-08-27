package com.runone.bean;


import com.runone.annotation.EsFieldType;
import com.runone.annotation.EsObject;
import com.runone.annotation.Field;

/**
 *
 *
 * id,url,urlhash,title,publish_time,summary,fingreprint,page_from,photo,label,label_id,content,file_path,photo_path,macine_id 增加一个machine_id,
 *
 */

@EsObject(indexName = "newsinfo", type = "base", shards = 5, replicas = 0)
public class SimpleNewsInfo {

    @Field(type = EsFieldType.Keyword)
    private String url;

    @Field(type = EsFieldType.Text)
    private String title;

    @Field(type = EsFieldType.Keyword)
    private String urlhash;

    @Field(type = EsFieldType.Keyword)
    private String publish_time;

    @Field(type = EsFieldType.Text)
    private String summary;

    @Field(type = EsFieldType.Keyword)
    private String fingerprint;

    @Field(type = EsFieldType.Keyword)
    private String page_from;

    @Field(type = EsFieldType.Text)
    private String label;

    @Field(type = EsFieldType.Keyword)
    private String label_id;

    @Field(type = EsFieldType.Text)
    private String content;

    @Field(type = EsFieldType.Keyword)
    private String photo_url;

    @Field(type = EsFieldType.Keyword)
    private String local_photo_url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlhash() {
        return urlhash;
    }

    public void setUrlhash(String urlhash) {
        this.urlhash = urlhash;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getPage_from() {
        return page_from;
    }

    public void setPage_from(String page_from) {
        this.page_from = page_from;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel_id() {
        return label_id;
    }

    public void setLabel_id(String label_id) {
        this.label_id = label_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getLocal_photo_url() {
        return local_photo_url;
    }

    public void setLocal_photo_url(String local_photo_url) {
        this.local_photo_url = local_photo_url;
    }
}
