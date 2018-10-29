package com.lht.test;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

public class FastDFSTest {
    @Test
    public void test() throws IOException, MyException {
        //获取配置文件的绝对路径
        String path = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();
        //设置全局的配置
        ClientGlobal.init(path);
        //创建追踪服务器的客户端
        TrackerClient trackerClient = new TrackerClient();
        //创建追踪服务器
        TrackerServer trackerServer = trackerClient.getConnection();
        //创建存储服务器
        StorageServer storageServer =null;
        //创建存储服务端
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //上传图片
        /**
         * 参数1：要上传的图片文件绝对路径
         * 参数2：图片文件的拓展名；如jpg
         * 参数3：文件meta信息
         * 返回内容；如下：
         * group1
         * M00/00/00/wKgMqFvOn9qAJzIbAABw0se6LsY691.jpg
         */
        String[] upload_file = storageClient.upload_file(
                "D:\\图片\\psb (1).jpg", "jpg", null);
        //组名
        String groupName = upload_file[0];
        //文件路径
        String fileName = upload_file[1];
        //获取存储服务器
        ServerInfo[] fetchStorages = trackerClient.getFetchStorages(trackerServer, groupName, fileName);
        for (ServerInfo fetchStorage : fetchStorages) {
            System.out.println("存储服务器ip:"+fetchStorage.getIpAddr()+";存储服务器的端口号:"+fetchStorage.getPort());
        }
        //图片可访问地址
        String url = "http://" + fetchStorages[0].getIpAddr() + "/" + groupName + "/" + fileName;

        System.out.println("图片可访问地址为：" + url);
    }
}
