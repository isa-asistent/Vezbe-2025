package rs.ac.uns.ftn.ci.isa.blob_storage.shared.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.ci.isa.blob_storage.shared.StorageService;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;


/**
 * Konkretna implementacija servisa za interakciju sa S3 skladistem.
 */
@Service
public class RustFSStorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${rustfs.s3.bucket-name}")
    private String bucketName;

    public RustFSStorageService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public void uploadFile(String keyName, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
//                .contentType(file.getContentType()) // Mozete specificirati konkretni tip file kojim rukuje S3. Jednom tako potpisan URL prima iskljucivo samo taj tip datoteke.
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(file.getBytes()));
    }
    @Override
    public String generatePutUrl(String keyName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
//                .contentType("image/jpeg") // // Mozete specificirati konkretni tip file kojim rukuje S3. Jednom tako potpisan URL prima iskljucivo samo taj tip datoteke.
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // URL je validan 10 min, ovo je konfigurabilno putem application.properties
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObject = s3Presigner.presignPutObject(presignRequest);
        return presignedPutObject.url().toString();
    }

    @Override
    public String generateGetUrl(String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // URL je validan 10 minuta
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObject = s3Presigner.presignGetObject(presignRequest);
        return presignedGetObject.url().toString();
    }

    @Override
    public ResponseInputStream<GetObjectResponse> downloadFile(String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
}
