package com.example.library.dto;

import com.example.library.models.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookListDto extends ObjectsListDto<Book> {
}
