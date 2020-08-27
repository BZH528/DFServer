package com.runone.service.impl;

import com.runone.annotation.EsObject;
import com.runone.bean.GovernmentNoticeInfo;
import com.runone.bean.SimpleNewsInfo;
import com.runone.service.DataLandingService;
import com.runone.utils.EsHelper;
import com.runone.utils.FdfsHelper;
import com.runone.vo.NewsInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service("DataLandingService")
public class DataLandingServiceImpl implements DataLandingService {

    @Autowired
    private FdfsHelper fdfsHelper;

    @Autowired
    private EsHelper esHelper;

    public String uploadFileToFdfs(MultipartFile file, String filename) {
        try {
            byte[] bytes = file.getBytes();
            return fdfsHelper.uploadeFile(bytes, filename, new HashMap<String, String>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteFile(String url) {
        return this.fdfsHelper.deleteFile(url) != -1;
    }


    public boolean indexNewsInfo(SimpleNewsInfo newsInfo) {
        return this.esHelper.indexDocument(SimpleNewsInfo.class, newsInfo);
    }

    public boolean indexNoticeInfo(GovernmentNoticeInfo noticeInfo) {
        return this.esHelper.indexDocument(GovernmentNoticeInfo.class, noticeInfo);
    }

    public boolean clearNewInfo() {
        EsObject esObject = SimpleNewsInfo.class.getAnnotation(EsObject.class);
        String indexName = esObject.indexName();

        if (this.esHelper.isExistIndex(indexName)) {
            return this.deleteIndexInfo(SimpleNewsInfo.class);
        }
        return true;
    }

    private boolean deleteIndexInfo(Class clazz) {
        EsObject esObject = (EsObject) clazz.getAnnotation(EsObject.class);
        String indexName = esObject.indexName();
        List<SimpleNewsInfo> simpleNewsInfos = this.esHelper.searchAll(SimpleNewsInfo.class);
        try {
            Iterator<SimpleNewsInfo> iterator = simpleNewsInfos.iterator();
            while (iterator.hasNext()) {
                SimpleNewsInfo newsInfo = iterator.next();
                String local_photo_url = newsInfo.getLocal_photo_url();
                if (null != local_photo_url && !"".equals(local_photo_url.trim())) {
                    this.fdfsHelper.deleteFile(local_photo_url);
                }
            }
            this.esHelper.deleteIndex(indexName);
            this.esHelper.createIndex(SimpleNewsInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<NewsInfoVo> searchNewsInfo(SimpleNewsInfo newsInfo) {

        return this.esHelper.searchNewsInfo(newsInfo);
    }

}
