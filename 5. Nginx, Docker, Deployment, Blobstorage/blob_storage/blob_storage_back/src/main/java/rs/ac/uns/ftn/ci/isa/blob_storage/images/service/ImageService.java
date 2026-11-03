package rs.ac.uns.ftn.ci.isa.blob_storage.images.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.ci.isa.blob_storage.images.dto.GetPutUrlResponse;

import java.io.IOException;

public interface ImageService {
    String upload(MultipartFile file) throws IOException;
    InputStreamResource download(String keyName);
    GetPutUrlResponse generatePutUrl();

    String generateGetUrl(String key);
}
