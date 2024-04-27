package com.example.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    private Integer rating;
    private String content;
    private Integer editionId;
    public ReviewDto(Integer id) { editionId = id; }
}
