package com.runone.vo;

import com.runone.bean.SimpleNewsInfo;
import org.elasticsearch.common.text.Text;

import java.util.List;
import java.util.Map;

public class NewsInfoVo {

    private String title;
    private String summary;
    private String publish_time;
    private String urlhash;
    private String url;
    private String photo_url;
    private String local_photo_url;

    public String getLocal_photo_url() {
        return local_photo_url;
    }

    public void setLocal_photo_url(String local_photo_url) {
        this.local_photo_url = local_photo_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getUrlhash() {
        return urlhash;
    }

    public void setUrlhash(String urlhash) {
        this.urlhash = urlhash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    private List<String> match_Sentence;



    public List<String> getMatch_Sentence() {
        return match_Sentence;
    }

    public void setMatch_Sentence(List<String> match_Sentence) {
        this.match_Sentence = match_Sentence;
    }
}
