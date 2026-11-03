package rs.ac.uns.ftn.ci.isa.blob_storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Konfiguraciona klasa koja definise beanove za upotrebu blob storeage-a, u vidu S3 API-ja.
 */
@Configuration
public class S3BlobStorageConfig {
    /**
     * Adresa blob storage-a koji klijent gadja
     */
    @Value("${rustfs.s3.endpoint}")
    private String endpoint;

    /**
     * Kljuc za pristup skladistu (definisan AWS-ovim standardom)
     */
    @Value("${rustfs.s3.access-key}")
    private String accessKey;

    /**
     * Secret kljuc za pristup (definisan AWS-ovim standardom)
     */
    @Value("${rustfs.s3.secret-key}")
    private String secretKey;

    /**
     * Region u kom je skladiste (AWS ima svoje regione, RustFS prati njegove standarde)
     */
    @Value("${rustfs.s3.region}")
    private String region;

    /**
     * Kreira Spring Bean koji predstavlja klijent za pristup skladistu
     *
     * @return Vraca instancu {@link  S3Client}
     */
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Obavezno za skladista koja su nalik S3, npr RustFS
                        .build())
                .build();
    }

    /**
     * Vrca "potpisivac" definisan putem S3, sluzi za generisanje presigned url-ova.
     *
     * @return instanca {@link S3Presigner}
     */
    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Obavezno za skladista koja su nalik S3, npr RustFS
                        .build())
                .build();
    }
}
