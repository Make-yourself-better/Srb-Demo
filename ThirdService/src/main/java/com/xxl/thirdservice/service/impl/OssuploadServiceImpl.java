package com.xxl.thirdservice.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.xxl.thirdservice.config.OssProperties;
import com.xxl.thirdservice.service.OssuploadService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssuploadServiceImpl implements OssuploadService {
    @Override
    public String uploadFile(MultipartFile file) {
        // 创建OSSClient实例。
        try {
            OSS ossClient = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.KEY_ID, OssProperties.KEY_SECRET);
            if (ossClient.doesBucketExist(OssProperties.BUCKET_NAME)){
                //创建bucket
                ossClient.createBucket(OssProperties.BUCKET_NAME);
                //设置oss实例的访问权限：公共读
                ossClient.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
            }
            // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
            InputStream inputStream = file.getInputStream();
            //获取文件名
            String filename= file.getOriginalFilename();
            //在文件名称里添加随机唯一的值
            String uuid= UUID.randomUUID().toString().replace("-","");
            filename=uuid+filename;
            //把文件按日期分类
            String datePath = new DateTime().toString("yyyy/MM/dd");

            //第一个参数 Backet名称
            //第二个参数 上传到oss文件路径和文件名称
            //第三个参数 上传文件输入流
            // 创建PutObjectRequest对象。
            filename=datePath+"/"+filename;
            // 上传文件。
            // putObject(String bucketName, String key, InputStream input)
            ossClient.putObject(OssProperties.BUCKET_NAME,filename,inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            //需要把上传文件到阿里云的路径手动拼接出来
            // https://gulischool-dyk.oss-cn-beijing.aliyuncs.com/1.png
            String url="https://"+OssProperties.BUCKET_NAME+"."+OssProperties.ENDPOINT+"/"+filename;
            return url;

        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    @Override
    public void removeFile(String url) {

        OSS ossClient = new OSSClientBuilder().build(OssProperties.ENDPOINT, OssProperties.KEY_ID, OssProperties.KEY_SECRET);
        try {
            String address="https://"+OssProperties.BUCKET_NAME+"."+OssProperties.ENDPOINT+"/";
            String objectName = url.substring(address.length());
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(OssProperties.BUCKET_NAME, objectName);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }
}
