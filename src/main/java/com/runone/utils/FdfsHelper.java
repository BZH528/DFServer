package com.runone.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

@Component
public class FdfsHelper {

    private StorageClient1 storageClient1;

    public FdfsHelper() {
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
//        String path = this.getClass().getClassLoader().getResource("fdfs_client.conf").getPath();
//        File file = new File("DFServer/conf/fdfs_client.conf");
//        String path = file.getPath();
//        Resource resource = new DefaultResourceLoader().getResource("classpath:fdfs_client.conf");
//        String path = resource.getFile().getPath();
        String path = "config/fdfs_client.conf";
        System.out.println("path:\t" + path);
        ClientGlobal.init(path);
        //创建TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //获取TrackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        if (trackerServer == null) {
            throw new IllegalStateException("getConnection return null");
        }

        //创建StorageServer对象
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        if (storageServer == null) {
            throw new IllegalStateException("getStoreStorage return null");
        }
        //使用TrackerServer和StorageServer构造StorageClient对象
        this.storageClient1 = new StorageClient1(trackerServer, storageServer);
    }

    /**
     * 上传文件
     *
     * @param file     文件对象
     * @param fileName 文件名
     * @param metaList 文件元数据
     * @return
     */
    public String uploadFile(File file, String fileName, Map<String, String> metaList) {
        try {
            byte[] buff = IOUtils.toByteArray(new FileInputStream(file));
            NameValuePair[] nameValuePairs = null;
            if (metaList != null) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String name = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs[index++] = new NameValuePair(name, value);
                }
            }
            return this.storageClient1.upload_file1(buff, FilenameUtils.getExtension(fileName), nameValuePairs);
        } catch (Exception e) {
            System.out.println("上传文件失败：" + e);
        }
        return null;
    }

    public String uploadeFile(byte[] buff, String fileName, Map<String, String> metaList) {
        NameValuePair[] nameValuePairs = null;
        if (metaList != null) {
            nameValuePairs = new NameValuePair[metaList.size()];
            int index = 0;
            for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> entry = iterator.next();
                String name = entry.getKey();
                String value = entry.getValue();
                nameValuePairs[index++] = new NameValuePair(name, value);
            }
        }
        try {
            return this.storageClient1.upload_file1(buff, FilenameUtils.getExtension(fileName), nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param fileId  文件ID（上传文件成功后返回的ID）
     * @param outFile 文件下载保存位置
     * @return
     */
    public int downloadFile(String fileId, File outFile) {
        FileOutputStream fos = null;
        ByteArrayInputStream in = null;
        try {
            byte[] content = storageClient1.download_file1(fileId);
            in = new ByteArrayInputStream(content, 0, content.length);
            fos = new FileOutputStream(outFile);
            IOUtils.copy(in, fos);
            return 0;
        } catch (Exception e) {
            System.out.println("下载文件失败：" + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("下载文件完毕后, 关闭输入流失败" + e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.out.println("下载文件完毕后, 关闭输出流失败" + e);
                }
            }
        }
        return -1;
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 删除失败返回-1，否则返回0
     */
    public int deleteFile(String fileId) {
        try {
            return storageClient1.delete_file1(fileId);
        } catch (Exception e) {
            System.out.println("删除文件失败：" + e);
        }
        return -1;
    }


}
