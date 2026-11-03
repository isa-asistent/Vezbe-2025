package rs.ac.uns.ftn.ci.isa.blob_storage.images.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.ci.isa.blob_storage.images.dto.GetPutUrlResponse;
import rs.ac.uns.ftn.ci.isa.blob_storage.shared.StorageService;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
    private final StorageService storageService;

    public ImageServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public String upload(MultipartFile file) throws IOException {
        var key = UUID.randomUUID().toString();
        storageService.uploadFile(key, file);
        return key;
    }

    @Override
    public InputStreamResource download(String keyName) {
        ResponseInputStream<GetObjectResponse> streamResponse = storageService.downloadFile(keyName);
        return new InputStreamResource(streamResponse);
    }

    @Override
    public GetPutUrlResponse generatePutUrl() {
        var key = UUID.randomUUID().toString();
        var putUrl = storageService.generatePutUrl(key);
        return new GetPutUrlResponse(key, putUrl);
    }

    @Override
    public String generateGetUrl(String key) {
        return storageService.generateGetUrl(key);
    }
}
