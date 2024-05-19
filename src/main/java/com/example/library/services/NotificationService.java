package com.example.library.services;

import com.example.library.models.Edition;
import com.example.library.models.Notification;
import com.example.library.models.User;
import com.example.library.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationRepository notificationRepository;

    public void create(User user, Edition edition)
    {
        notificationRepository.save(new Notification(user, edition));
    }

    public boolean checkNotification(Integer id, Integer userId)
    {
        return notificationRepository.existsByUserIdAndEditionId(userId, id);
    }

    public void notify(Edition edition)
    {
        Integer id = edition.getId();
        for (Notification notification: notificationRepository.findByEditionId(id)) {
            emailService.sendEmail(
                    notification.getUser().getEmail(),
                    "Book arrival",
                    "Book " + edition.getName() + " was arrived: http://localhost:8080/books/" + id
            );
        }

        notificationRepository.deleteByEditionId(id);
    }
}
