package com.example.library.services;

import com.example.library.models.Edition;
import com.example.library.models.Like;
import com.example.library.models.User;
import com.example.library.repositories.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public boolean checkLike(Integer editionId, Integer userId)
    {
        return likeRepository.findByUserIdAndEditionId(userId, editionId).isPresent();
    }

    public void delete(Integer editionId, Integer userId)
    {
        Optional<Like> like = likeRepository.findByUserIdAndEditionId(userId, editionId);
        like.ifPresent(value -> likeRepository.delete(value));
    }

    public void add(Edition edition, User user)
    {
        Like like = new Like(edition, user);
        likeRepository.save(like);
    }

    public Iterable<Edition> getLikedEditions(Integer userId)
    {
        List<Like> liked = (List<Like>)likeRepository.findByUserId(userId);
        return liked.stream().map(Like::getEdition).toList();
    }
}
