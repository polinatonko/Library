package com.example.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    private Integer rating;
    private String content;
    private Integer editionId;
    public ReviewDto(Integer id) { editionId = id; }
}
