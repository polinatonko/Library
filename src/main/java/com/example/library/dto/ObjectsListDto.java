package com.example.library.dto;

import com.example.library.models.Author;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ObjectsListDto<T> {
    private List<T> objects = new ArrayList<>();
    private Page<T> page = null;

    public ObjectsListDto(Iterable<T> objects) {
        this.objects.addAll((Collection<? extends T>) objects);
    }

    public ObjectsListDto(Page<T> page)
    {
        this.page = page;
        this.objects.addAll((Collection<? extends T>) page.getContent());
    }
}
