package com.wip.api;
import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioCloudService {

    /**
     * MinIO连接信息
     */
    private static final String ENDPOINT = "http://x.x.x.x:9001"; // MinIO服务器地址
    private static final String ACCESS_KEY = "root"; // MinIO访问密钥
    private static final String SECRET_KEY = "xxxxxx"; // MinIO秘密密钥
    private static final String BUCKET = "xxxx"; // MinIO存储桶名称

    /**
     * 上传文件到MinIO
     * @param file     上传的文件
     * @param fileName 文件名
     * @return 上传后的文件名
     */
    public static String upload(MultipartFile file, String fileName) {
        try {
            // 创建MinioClient实例
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(ENDPOINT)
                    .credentials(ACCESS_KEY, SECRET_KEY)
                    .build();

            // 使用MinioClient上传文件流
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build());

            // 返回上传后的文件名
            return fileName;
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | MinioException e) {
            e.printStackTrace();
            // 在这里处理异常
            return null;
        }
    }


}
