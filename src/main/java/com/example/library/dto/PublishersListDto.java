package com.example.library.dto;

import com.example.library.models.Publisher;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PublishersListDto {
    private List<Publisher> objects = new ArrayList<>();

    public PublishersListDto(List<Publisher> publishers) { this.objects.addAll(publishers);}
}
