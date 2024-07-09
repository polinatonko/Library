package com.example.library.dto;

import com.example.library.models.Genre;
import com.example.library.models.Publisher;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenreListDto extends ObjectsListDto<Genre> {
}