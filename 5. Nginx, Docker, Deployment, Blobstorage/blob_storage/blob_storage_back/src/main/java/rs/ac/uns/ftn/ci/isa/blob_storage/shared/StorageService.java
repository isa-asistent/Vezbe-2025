package rs.ac.uns.ftn.ci.isa.blob_storage.shared;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

/**
 * Definise interfejs za upravljanje klasicnim uplaod-om i download-om u S3. Takodje pruza mehanizam geneiranja presigned ulr-ova.
 */
public interface StorageService {

    void uploadFile(String keyName, MultipartFile file) throws IOException;
    String generatePutUrl(String keyName);
    String generateGetUrl(String keyName);

    ResponseInputStream<GetObjectResponse> downloadFile(String keyName);
}
