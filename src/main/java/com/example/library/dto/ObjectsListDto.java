package com.example.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ObjectsListDto<T> {
    private List<T> objects = new ArrayList<>();

    public ObjectsListDto(List<T> objects) { this.objects.addAll(objects); }
}
