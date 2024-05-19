package com.example.library.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RequestDto {
    private List<SearchRequestDto> searchRequestDtos = new ArrayList<>();
    private PageRequestDto pageDto;
    public void add(String col, String val, SearchRequestDto.Operation op)
    {
        searchRequestDtos.add(new SearchRequestDto(col, val, op));
    }
}
