package com.bzh;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.util.UUID;

public class TestFastDFS {

    public static void main(String[] args) throws Exception {

        String path = "config/fdfs_client.conf";
        System.out.println("path:\t" + path);
        ClientGlobal.init(path);

        // 创建TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        // 获取TrackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        System.out.println(trackerServer);

        if (trackerServer == null) {
            System.out.println("getConnection return null");
        }

        // 创建StorageServer对象
        StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
        System.out.println(storeStorage);

    }
}
