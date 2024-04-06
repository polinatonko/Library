package com.example.library.services;
import com.example.library.GlobalFunctions;
import com.example.library.models.Block;
import com.example.library.models.User;
import com.example.library.dto.UserDto;
import com.example.library.enums.ERole;
import com.example.library.repositories.*;
import com.example.library.timer.BlockTimerTask;
import com.example.library.tokens.NewEmailToken;
import com.example.library.tokens.PasswordResetToken;
import com.example.library.tokens.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private TimerService timerService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private GlobalFunctions utils;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlockService blockService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private NewTokenRepository newTokenRepository;

    public User registerUser(UserDto userDto, ERole roleName) throws IllegalArgumentException
    {
        if (userRepository.existsUserByEmail(userDto.getEmail()))
        {
            throw new IllegalArgumentException("There is an account with such email address: " +
                    userDto.getEmail());
        }

        User user = new User(userDto, passwordEncoder.encode(userDto.getPassword()));
        user.setRole(roleService.getRoleByName(roleName));

        return save(user);
    }

    public User save(User user)
    {
        User new_user = userRepository.save(user);
        VerificationToken token = createToken(new_user);
        new_user.setVerificationToken(token);
        userRepository.save(new_user);

        emailService.sendEmail(
                user.getEmail(),
                "Account confirmation",
                "Click to link to activate your account: http://localhost:8080/confirm-account?token=" + token.getToken()
        );

        return user;
    }

    public void sendNewEmailConfirmationMail(User user, String new_email)
    {
        Optional<NewEmailToken> optToken = newTokenRepository.findById(user.getId());
        NewEmailToken token;
        if (optToken.isEmpty())
        {
            token = createNewEmailToken(user, new_email);
            user.setNewEmailToken(token);
            userRepository.save(user);
        }
        else
        {
            token = optToken.get();
            token.setExpiryDate(utils.getDate(1, 0, 0, 0));
            token.setNewEmail(new_email);
        }

        emailService.sendEmail(
                new_email,
                "New email confirmation",
                "Click to link to confirm your new email: http://localhost:8080/confirm-email?token=" + token.getToken()
        );
    }

    public String confirmEmail(String token)
    {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken.isPresent())
        {
            User user = userRepository.findUserByVerificationToken(verificationToken.get());
            if (user.isEnabled())
                return "User account already active!";

            user.setEnabled(true);
            userRepository.save(user);
            return "Account was successfully activated!";
        }
        return "Token is invalid!";
    }

    public boolean existsByEmail(String email)
    {
        return userRepository.existsUserByEmail(email);
    }

    public Optional<User> getById(Integer id)
    {
        return userRepository.findById(id);
    }

    public Optional<User> getByEmail(String email) { return userRepository.findUserByEmail(email); }
    public NewEmailToken createNewEmailToken(User user, String newEmail)
    {
        NewEmailToken token = new NewEmailToken(UUID.randomUUID().toString(), newEmail, user, 24);
        return newTokenRepository.save(token);
    }

    public VerificationToken createToken(User user)
    {
        VerificationToken token = new VerificationToken(UUID.randomUUID().toString(), user, 24);
        return verificationTokenRepository.save(token);
    }

    public PasswordResetToken createPasswordResetToken(User user)
    {
        PasswordResetToken token = new PasswordResetToken(UUID.randomUUID().toString(), user, 24);
        return passwordResetTokenRepository.save(token);
    }

    public void sendResetPasswordEmail(PasswordResetToken token)
    {
        String url = "localhost:8080/reset-password?token=" + token.getToken(),
                message = "reset-password";
        emailService.sendEmail(token.getUser().getEmail(),
                "Password reset",
                message + "\n" +url);
    }

    public Optional<User> findUserByToken(String token)
    {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        return verificationToken.map(VerificationToken::getUser);
    }

    public Optional<User> findUserByNewEmailToken(String token)
    {
        Optional<NewEmailToken> newEmailToken = newTokenRepository.findByToken(token);
        return newEmailToken.map(NewEmailToken::getUser);
    }

    public Optional<User> findUserByPasswordResetToken(String token)
    {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
        return passwordResetToken.map(PasswordResetToken::getUser);
    }

    public void changePassword(User user, String password)
    {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    public Iterable<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public Iterable<User> getAllUsersForAdmin() {return getAllUsers();}

    public Iterable<User> getAllUsersForLibrarian()
    {
        List<User> users = getUsersByRole(ERole.ROLE_LIBRARIAN);
        users.addAll(getUsersByRole(ERole.ROLE_READER));
        return users;
    }

    public List<User> getUsersByRole(ERole role)
    {
        return userRepository.findByRoleName(role);
    }

    public void updateUser(User user)
    {
        User saved = getUserByIdOrThrowException(user.getId());
        saved.setLastName(user.getLastName());
        saved.setName(user.getName());
        saved.setMiddleName(user.getMiddleName());
        saved.setEmail(user.getEmail());
        saved.setBirthDate(user.getBirthDate());

        userRepository.save(saved);
    }

    public void deleteUser(Integer id)
    {
        userRepository.deleteById(id);
    }

    public void unblockUser(Integer id)
    {
        User user = getUserByIdOrThrowException(id);
        user.setBlocked(false);
        user.setBlockingEnd(null);

        blockService.cancelBlock(userRepository.save(user).getId());
        timerService.stopTimerByUserId(user.getId());
    }

    public void blockUser(Integer id, Date blockingEnd)
    {
        User user = getUserByIdOrThrowException(id);
        user.setBlocked(true);
        user.setBlockingEnd(blockingEnd);
        User saved = userRepository.save(user);

        Block block = new Block(saved, timerService.getTimerById(saved.getId()), new BlockTimerTask(saved) { @Override public void run() { unblockUser(saved.getId());}});
        blockService.save(block);
    }

    public boolean isUserAdmin(Integer id)
    {
        User user = getUserByIdOrThrowException(id);
        return (user.getRole().getName() == ERole.ROLE_ADMIN);
    }

    private User getUserByIdOrThrowException(Integer id) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty())
            throw new UsernameNotFoundException("User with such id doesn't exist");

        return user.get();
    }

    public String changeEmail(String token) {
        Optional<NewEmailToken> newEmailToken = newTokenRepository.findByToken(token);

        if (newEmailToken.isPresent())
        {
            User user = userRepository.findByNewEmailToken(newEmailToken.get());
            user.setEmail(newEmailToken.get().getNewEmail());
            userRepository.save(user);
            return "Email was successfully changed";
        }
        return "Token is invalid!";
    }
}
