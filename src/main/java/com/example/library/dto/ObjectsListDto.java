package com.example.library.dto;

import com.example.library.models.Author;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ObjectsListDto<T>{
    private List<T> objects = new ArrayList<>();
    public ObjectsListDto(Iterable<T> objects) { this.objects.addAll((Collection<? extends T>) objects); }
}
