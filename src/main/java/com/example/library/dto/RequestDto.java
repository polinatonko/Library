package com.example.library.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestDto {
    private List<SearchRequestDto> searchRequestDtos;
}
