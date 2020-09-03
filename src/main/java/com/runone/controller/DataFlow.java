package com.runone.controller;

import com.alibaba.fastjson.JSONObject;
import com.runone.bean.GovernmentNoticeInfo;
import com.runone.bean.PersonRelation;
import com.runone.bean.SimpleNewsInfo;
import com.runone.service.DataLandingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class DataFlow {

    @Autowired
    private DataLandingService dataLandingService;

    @RequestMapping("/indexNewsInfo")
    public ResponseEntity<JSONObject> indexSimpleNew(SimpleNewsInfo newsInfo, @RequestParam("photo") MultipartFile photo, String suffix, String photo_exist) {
        if (suffix == null) {
            suffix = "jpeg";
        }
        JSONObject json = new JSONObject();
        if (newsInfo.getTitle() == null) {
            json.put("status", "500");
            json.put("msg", "the title is not allowed to null !");
            return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
        }
        if (photo != null && "1".equals(photo_exist) && photo.getSize() > 8) {
            String filename = null;
            long size = photo.getSize();
            filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;

            String url_code = this.dataLandingService.uploadFileToFdfs(photo, filename);
            if (url_code == null) {
                json.put("status", "500");
                json.put("msg", "the photo cann't be add to file system!");
                return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
            }
            newsInfo.setLocal_photo_url(url_code);
        }

        boolean res = this.dataLandingService.indexNewsInfo(newsInfo);
        if (!res) {
            json.put("status", "500");
            json.put("msg", "cann't index the newsinfo!");
            if (newsInfo.getPhoto_url() != null) {
                this.dataLandingService.deleteFile(newsInfo.getPhoto_url());
            }
            return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
        }
        json.put("status", "200");
        json.put("msg", "success");
        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/indexNoticesInfo", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> indexNoticesInfo(GovernmentNoticeInfo noticeInfo, @RequestParam("file_pdf") MultipartFile file, String file_exist, String suffix) {
        if (suffix == null) {
            suffix = "pdf";
        }
        JSONObject json = new JSONObject();
        if (noticeInfo.getNoticeCode() == null) {
            json.put("status", "500");
            json.put("msg", "the nodecode is not allowed to null !");
            return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
        }
        if (file != null && "1".equals(file_exist)) {
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            String url_code = this.dataLandingService.uploadFileToFdfs(file, filename);
            if (url_code == null) {
                json.put("status", "500");
                json.put("msg", "the noctice's file cann't be add to file system!");
                return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
            }
            noticeInfo.setFile_url(url_code);
        }
        String format_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        noticeInfo.setPostDate(format_date);
        boolean res = this.dataLandingService.indexNoticeInfo(noticeInfo);
        if (!res) {
            json.put("status", "500");
            json.put("msg", "cann't index the nocticeinfo!");
            return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
        }
        json.put("status", "200");
        json.put("msg", "success");
        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/topic/clearNewInfo", method = RequestMethod.GET)
    @ResponseBody
    public Object clearNewInfo() {
        boolean res = this.dataLandingService.clearNewInfo();
        Map result = new HashMap<String, Object>();
        if (res) {
            result.put("status", "200");
            result.put("msg", "success");
        } else {
            result.put("status", "500");
            result.put("msg", "cann't delete the index,please check the service is online");
        }
        return result;
    }

    @RequestMapping(value = "/indexRelation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<JSONObject> indexRelation(PersonRelation personRelation, @RequestParam("file") MultipartFile file, String suffix) {
        if (suffix == null) {
            suffix = "jpg";
        }
        JSONObject json = new JSONObject();
        if (personRelation.getPid() == null) {
            json.put("status", "500");
            json.put("msg", "the PersonRelation's file cann't be add to file system!");
            return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
        }
        String url_code = null;
        if (file != null && file.getSize() > 8) {
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            url_code = this.dataLandingService.uploadFileToFdfs(file, filename);
            if (url_code == null) {
                json.put("status", "500");
                json.put("msg", "the PersonRelation's file cann't be add to file system!");
                return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
            }
            personRelation.setLocal_photo_url(url_code);
        }
        boolean res = this.dataLandingService.indexPersonRelation(personRelation);
        if (!res) {
            json.put("status", "500");
            json.put("msg", "cann't index the nocticeinfo!");
            return new ResponseEntity<JSONObject>(json, HttpStatus.INTERNAL_SERVER_ERROR);//500,系统异常
        }
        json.put("status", "200");
        json.put("msg", "success");
        json.put("local_photo_url", url_code);
        return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }

}
