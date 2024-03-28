package com.example.library.services;
import com.example.library.dto.PublishersListDto;
import com.example.library.models.Publisher;
import com.example.library.models.User;
import com.example.library.repositories.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.library.dto.PublisherDto;

import java.util.Optional;

@Service
public class PublisherService {
    @Autowired
    private PublisherRepository publisherRepository;
    public void deletePublisher(Integer id)
    {
        publisherRepository.deleteById(id);
    }
    public void updatePublisher(Publisher publisher)
    {
        Optional<Publisher> savedOptionalPublisher = publisherRepository.findById(publisher.getId());
        if (savedOptionalPublisher.isPresent())
        {
            Publisher saved = savedOptionalPublisher.get();
            saved.setName(publisher.getName());
            saved.setDescription(publisher.getDescription());
            saved.setAddress(publisher.getAddress());
            saved.setISBNPrefix(publisher.getISBNPrefix());

            publisherRepository.save(saved);
        }
    }

    public Publisher createPublisher(PublisherDto publisherDto)
    {
        Publisher publisher = new Publisher(publisherDto);
        if (publisherRepository.findByName(publisher.getName()) != null)
            throw new IllegalArgumentException("Publisher with such name exists: " + publisher.getName());

        //if (publisherRepository.findByISBNPrefix(publisher.getISBNPrefix()) != null)
          //  throw new IllegalArgumentException("Publisher with such ISBN prefix exists: " + publisher.getISBNPrefix());

        return publisherRepository.save(publisher);
    }

    public Iterable<Publisher> getAll() { return publisherRepository.findAll(); }

}
