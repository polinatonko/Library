package com.example.library.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class ImageDto {
    private String name;
    private MultipartFile file;
}