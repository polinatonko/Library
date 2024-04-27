package com.example.library.services;

import com.example.library.dto.ImageDto;
import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.models.Image;
import com.example.library.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CustomCloudinaryService cloudinaryService;

    public Image uploadImage(ImageDto imageDto, Author author)
    {
        try {
            if (imageDto.getName().isEmpty() || imageDto.getFile().isEmpty())
            {
                throw new Exception("Upload file error!");
            }

            Image image = new Image();
            image.setName(imageDto.getName());
            image.setUrl(cloudinaryService.uploadFile(imageDto.getFile(), "images"));
            image.setAuthor(author);
            if (image.getUrl() == null) {
                throw new Exception("Upload file error!");
            }
            return imageRepository.save(image);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public Image uploadImage(ImageDto imageDto, Book book)
    {
        try {
            if (imageDto.getName().isEmpty() || imageDto.getFile().isEmpty())
            {
                throw new Exception("Upload file error!");
            }

            Image image = new Image();
            image.setName(imageDto.getName());
            image.setUrl(cloudinaryService.uploadFile(imageDto.getFile(), "images"));
            image.setEdition(book);
            if (image.getUrl() == null) {
                throw new Exception("Upload file error!");
            }
            return imageRepository.save(image);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
