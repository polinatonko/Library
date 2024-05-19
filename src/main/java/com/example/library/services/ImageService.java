package com.example.library.services;

import com.example.library.dto.ImageDto;
import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.models.Image;
import com.example.library.repositories.AuthorRepository;
import com.example.library.repositories.BookRepository;
import com.example.library.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomCloudinaryService cloudinaryService;
    public void deleteImage(Author author) throws IOException {
        if (author.getPhoto() != null)
        {
            String[] vars = author.getPhoto().getUrl().split("/");
            int len = vars.length;
            String publicId = String.join("/", vars[len - 2], vars[len - 1]);
            cloudinaryService.delete(publicId);
            author.setPhoto(null);
            authorRepository.save(author);
        }
    }

    public void deleteImage(Book book) throws IOException {
        if (book.getPhoto() != null)
        {
            String[] vars = book.getPhoto().getUrl().split("/");
            int len = vars.length;
            String publicId = String.join("/", vars[len - 2], vars[len - 1]);
            cloudinaryService.delete(publicId);
            book.setPhoto(null);
            bookRepository.save(book);
        }
    }

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
