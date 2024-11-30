package mt.movie_theater.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
@PropertySource("classpath:application-aws.properties")
public class S3Uploader {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName) {
        File uploadFile = convertMultipartFileToFile(multipartFile);
        return upload(uploadFile, dirName);
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        try {
            File convertFile = File.createTempFile("upload-", file.getOriginalFilename());
            convertFile.deleteOnExit(); // 애플리케이션 종료 시 파일 삭제
            file.transferTo(convertFile);
            return convertFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String upload(File file, String dirName) {
        String fileName = dirName + "/" + file.getName();
        String uploadImageUrl = putS3(file, fileName);
        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File file, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, file)
                        .withCannedAcl(CannedAccessControlList.PublicRead)	// 공개 url 허용
        );
        return amazonS3.getUrl(bucket, fileName).toString();
    }
}
