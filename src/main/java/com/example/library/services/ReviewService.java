package com.example.library.services;

import com.example.library.GlobalFunctions;
import com.example.library.dto.PageRequestDto;
import com.example.library.dto.RequestDto;
import com.example.library.dto.ReviewDto;
import com.example.library.dto.SearchRequestDto;
import com.example.library.models.*;
import com.example.library.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private FilterSpecification<Review> filterSpecification;
    @Autowired
    private GlobalFunctions utils;
    @Autowired
    private ReviewRepository reviewRepository;
    public Review getById(Integer id) { return getByIdOrThrowException(id); }
    public void deleteByIds(Integer userId, Integer editionId) { reviewRepository.deleteByEditionIdAndUserId(editionId, userId); }
    public boolean existsByIds(Integer userId, Integer editionId) { return reviewRepository.existsByUserIdAndEditionId(userId, editionId); }

    public Iterable<Review> getByEditionId(Integer id) { return reviewRepository.findByEditionId(id); }
    public Iterable<Review> getByUserId(Integer id) { return reviewRepository.findByUserId(id); }
    public void create(ReviewDto reviewDto, Integer userId)
    {
        Edition edition = bookService.getById(reviewDto.getEditionId());
        User user = userService.getById(userId).get(); // fix!!!
        Review review = new Review(utils.getDate(0, 0, 0, 0), reviewDto.getRating(), reviewDto.getContent(), user, edition);

        bookService.updateRating(bookService.getById(reviewDto.getEditionId()), reviewDto.getRating(), true);

        reviewRepository.save(review);
    }
    public void delete(Review review)
    {
        bookService.updateRating((Book)review.getEdition(), review.getRating(), false);

        reviewRepository.delete(review);
    }
    private Review getByIdOrThrowException(Integer id)
    {
        Optional<Review> object = reviewRepository.findById(id);

        if (object.isEmpty())
            throw new IllegalArgumentException("Object with such id doesn't exist");

        return object.get();
    }

    public Page<Review> getBookReviews(Integer id, RequestDto request)
    {
        Pageable pageable = new PageRequestDto().getPageable(request.getPageDto());
        request.add("edition-id", id.toString(), SearchRequestDto.Operation.JOIN);
        Specification<Review> spec = filterSpecification.getSearchSpecification(request.getSearchRequestDtos());
        return reviewRepository.findAll(spec, pageable);
    }
}
