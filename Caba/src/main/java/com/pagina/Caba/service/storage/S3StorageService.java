package com.pagina.Caba.service.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Servicio para gestionar la subida y eliminación de imágenes en AWS S3
 */
@Service
public class S3StorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Sube una imagen a S3 y retorna la URL pública
     */
    public String uploadImage(MultipartFile file, String folder) {
        try {
            // Validar que el archivo sea una imagen
            if (!isImageFile(file)) {
                throw new IllegalArgumentException("El archivo debe ser una imagen (PNG, JPG, JPEG, GIF)");
            }

            // Generar nombre único para el archivo
            String fileName = generateFileName(file.getOriginalFilename(), folder);

            // Preparar metadata
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ) // Hacer la imagen pública
                    .build();

            // Subir archivo a S3
            s3Client.putObject(putObjectRequest, 
                              RequestBody.fromBytes(file.getBytes()));

            // Construir URL pública
            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                                           bucketName, region, fileName);

            logger.info("Imagen subida exitosamente: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            logger.error("Error al subir imagen a S3", e);
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen de S3
     */
    public void deleteImage(String imageUrl) {
        try {
            // Extraer el key (nombre del archivo) de la URL
            String key = extractKeyFromUrl(imageUrl);
            
            if (key == null) {
                logger.warn("URL de imagen inválida: {}", imageUrl);
                return;
            }

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Imagen eliminada exitosamente: {}", imageUrl);

        } catch (S3Exception e) {
            logger.error("Error al eliminar imagen de S3", e);
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
    }

    /**
     * Genera un nombre único para el archivo
     */
    private String generateFileName(String originalFileName, String folder) {
        String extension = getFileExtension(originalFileName);
        String uniqueId = UUID.randomUUID().toString();
        return String.format("%s/%s.%s", folder, uniqueId, extension);
    }

    /**
     * Obtiene la extensión del archivo
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "jpg";
        }
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot == -1) ? "jpg" : fileName.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Valida que el archivo sea una imagen
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/png") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/jpeg") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    /**
     * Extrae el key (nombre del archivo) de la URL de S3
     */
    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        // URL format: https://bucket-name.s3.region.amazonaws.com/folder/file.ext
        String[] parts = imageUrl.split(bucketName + ".s3." + region + ".amazonaws.com/");
        return parts.length > 1 ? parts[1] : null;
    }

    /**
     * Verifica si el bucket existe y lo crea si no existe
     */
    public void ensureBucketExists() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            
            s3Client.headBucket(headBucketRequest);
            logger.info("Bucket S3 existe: {}", bucketName);
            
        } catch (NoSuchBucketException e) {
            logger.info("Creando bucket S3: {}", bucketName);
            
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            
            s3Client.createBucket(createBucketRequest);
            
            // Configurar política de acceso público para imágenes
            String bucketPolicy = String.format("""
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Sid": "PublicReadGetObject",
                            "Effect": "Allow",
                            "Principal": "*",
                            "Action": "s3:GetObject",
                            "Resource": "arn:aws:s3:::%s/*"
                        }
                    ]
                }
                """, bucketName);
            
            PutBucketPolicyRequest policyRequest = PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(bucketPolicy)
                    .build();
            
            s3Client.putBucketPolicy(policyRequest);
            logger.info("Bucket S3 creado exitosamente: {}", bucketName);
        }
    }
}
