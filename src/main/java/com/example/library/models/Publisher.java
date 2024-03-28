package com.example.library.models;

import com.example.library.interfaces.IEntity;
import com.example.library.dto.PublisherDto;
import com.example.library.validation.UniqueISBNPrefix;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Publisher implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(name = "description", length = 4096)
    private String description;
    private String address;
    private String ISBNPrefix;
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Edition> editions;

    public Publisher(PublisherDto publisherDto)
    {
        name = publisherDto.getName();
        description = publisherDto.getDescription();
        address = publisherDto.getAddress();
        ISBNPrefix = publisherDto.getISBNPrefix();
    }

    @Override
    public String getShortName() {
        return name;
    }
}
