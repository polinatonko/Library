package com.example.library.services;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomCloudinaryService {
    @Resource
    Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName)
    {
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
