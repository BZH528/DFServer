package com.runone;

import com.runone.utils.FdfsHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TestDFS {

    private FdfsHelper fdfsHelper;

    @Before
    public void setUp() throws Exception {
        this.fdfsHelper = new FdfsHelper();
    }


    @Test
    public void testUpload() {
        String path = this.getClass().getClassLoader().getResource("static/imgs/bird.jpg").getPath();
        System.out.println("path:\t" + path);
        File file = new File(path);
        String res = fdfsHelper.uploadFile(file, "bird.jpg", new HashMap<String, String>());
        //group0/M00/00/00/wKgBtV8hS4-AbFofAACAIcNN37o228.jpg
        System.out.println(res);
    }

    @Test
    public void testDownLoad() {
        fdfsHelper.downloadFile("group0/M00/00/0A/wKgBtV88-kiAMVBHAACAIcNN37o311.jpg", new File("data/dog123.jpg"));
    }

    @Test
    public void testDelete() {
        int res = fdfsHelper.deleteFile("group0/M00/00/0D/wKgBtV9GMI6ACNX6AABd_10YmaE40.jpeg");
        System.out.println("res:\t" + res);
    }

    @Test
    public void test01(){
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(format);
    }


}
