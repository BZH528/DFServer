package com.runone.bean;


import com.runone.annotation.EsFieldType;
import com.runone.annotation.EsObject;
import com.runone.annotation.Field;

@EsObject(indexName = "annotation_notice", type = "base", shards = 3, replicas = 1)
public class GovernmentNoticeInfo {

    @Field(type = EsFieldType.Text)
    private String organization;

    @Field(type = EsFieldType.Text)
    private String noticeType;

    @Field(type = EsFieldType.Keyword)
    private String publishDate;

    @Field(type = EsFieldType.Text)
    private String tosendPeople;

    @Field(type = EsFieldType.Keyword)
    private String noticeCode;

    @Field(type = EsFieldType.Keyword)
    private String noticeCodeEnc;

    @Field(type = EsFieldType.Text)
    private String label;

    @Field(type = EsFieldType.Keyword)
    private String postDate;

    @Field(type = EsFieldType.Keyword)
    private String from_url;

    @Field(type = EsFieldType.Keyword)
    private String file_url;

    @Field(type = EsFieldType.Text)
    private String noticeContent;

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getFrom_url() {
        return from_url;
    }

    public void setFrom_url(String from_url) {
        this.from_url = from_url;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getTosendPeople() {
        return tosendPeople;
    }

    public void setTosendPeople(String tosendPeople) {
        this.tosendPeople = tosendPeople;
    }

    public String getNoticeCode() {
        return noticeCode;
    }

    public void setNoticeCode(String noticeCode) {
        this.noticeCode = noticeCode;
    }

    public String getNoticeCodeEnc() {
        return noticeCodeEnc;
    }

    public void setNoticeCodeEnc(String noticeCodeEnc) {
        this.noticeCodeEnc = noticeCodeEnc;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
}
