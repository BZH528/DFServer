package com.runone.service;

import com.runone.bean.GovernmentNoticeInfo;
import com.runone.bean.SimpleNewsInfo;
import com.runone.vo.NewsInfoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataLandingService {

    String uploadFileToFdfs(MultipartFile file, String filename);

    boolean deleteFile(String url);

    boolean indexNewsInfo(SimpleNewsInfo newsInfo);

    boolean indexNoticeInfo(GovernmentNoticeInfo noticeInfo);

    boolean clearNewInfo();

    List<NewsInfoVo> searchNewsInfo(SimpleNewsInfo newsInfo);
}
