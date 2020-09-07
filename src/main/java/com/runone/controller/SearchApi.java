package com.runone.controller;


import com.runone.bean.SimpleNewsInfo;
import com.runone.service.DataLandingService;
import com.runone.vo.NewsInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * controller
 */
@Controller
public class SearchApi {

    @Autowired
    private DataLandingService dataLandingService;

    @RequestMapping(value = "/api/search/newsinfo", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public List<NewsInfoVo> searchNewsInfo(SimpleNewsInfo newsInfo) {
        return this.dataLandingService.searchNewsInfo(newsInfo);
    }

}
