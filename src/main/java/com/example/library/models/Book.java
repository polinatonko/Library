package com.example.library.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue("book")
public class Book extends Edition {
    @ManyToMany(mappedBy = "books")
    private Set<Author> authors;

    public void removeAuthor(Author author) { authors.remove(author); }
    public void addAuthor(Author author) { authors.add(author); }

    @PreRemove
    private void removeAuthorAssociations() {
        for (Author author: this.authors) {
            author.getBooks().remove(this);
        }
    }
}
