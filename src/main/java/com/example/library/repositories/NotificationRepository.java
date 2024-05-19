package com.example.library.repositories;

import com.example.library.models.Notification;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Integer> {
    boolean existsByUserIdAndEditionId(Integer userId, Integer editionId);
    List<Notification> findByEditionId(Integer editionId);

    @Transactional
    void deleteByEditionId(Integer id);
}
