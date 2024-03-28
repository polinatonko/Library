package com.example.library.services;
import com.example.library.models.Role;
import com.example.library.models.User;
import com.example.library.dto.UserDto;
import com.example.library.enums.ERole;
import com.example.library.repositories.PasswordResetTokenRepository;
import com.example.library.tokens.PasswordResetToken;
import com.example.library.tokens.VerificationToken;
import com.example.library.repositories.RoleRepository;
import com.example.library.repositories.UserRepository;
import com.example.library.repositories.VerificationTokenRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public User registerUser(UserDto userDto, ERole roleName) throws IllegalArgumentException
    {
        if (userRepository.existsUserByEmail(userDto.getEmail()))
        {
            throw new IllegalArgumentException("There is an account with such email address: " +
                    userDto.getEmail());
        }

        User user = new User(userDto, passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName(roleName);
        if (role == null)
        {
            role = new Role(roleName);
            roleRepository.save(role);
        }
        user.setRole(role);

        /*User new_user = userRepository.save(user);

        VerificationToken token = new VerificationToken(UUID.randomUUID().toString(), new_user, 24);
        token = verificationTokenRepository.save(token);

        new_user.setVerificationToken(token);
        userRepository.save(new_user);

        emailService.sendEmail(
                user.getEmail(),
                "Account confirmation",
                "Click to link to activate your account: http://localhost:8080/confirm-account?token=" + token.getToken()
        );*/

        return save(user);
    }

    public User save(User user)
    {
        User new_user = userRepository.save(user);
        VerificationToken token = createToken(new_user);
        new_user.setVerificationToken(token);
        user = userRepository.save(new_user);

        emailService.sendEmail(
                user.getEmail(),
                "Account confirmation",
                "Click to link to activate your account: http://localhost:8080/confirm-account?token=" + token.getToken()
        );

        return user;
    }

    public String confirmEmail(String token)
    {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken != null)
        {
            User user = userRepository.findUserByVerificationToken(verificationToken);
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

    public User getByEmail(String email) { return userRepository.findUserByEmail(email); }

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
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        return verificationToken != null ? Optional.of(verificationToken.getUser()) : Optional.empty();
    }

    public Optional<User> findUserByPasswordResetToken(String token)
    {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        return passwordResetToken != null ? Optional.of(passwordResetToken.getUser()) : Optional.empty();
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
        List<User> users = (List<User>) getUsersByRole(ERole.ROLE_LIBRARIAN);
        users.addAll(getUsersByRole(ERole.ROLE_READER));
        return users;
    }

    public List<User> getUsersByRole(ERole role)
    {
        return userRepository.findByRoleName(role);
    }

    public void updateUser(User user)
    {
        Optional<User> savedOptionalUser = userRepository.findById(user.getId());
        if (savedOptionalUser.isPresent())
        {
            User saved = savedOptionalUser.get();
            saved.setLastName(user.getLastName());
            saved.setName(user.getName());
            saved.setMiddleName(user.getMiddleName());
            saved.setEmail(user.getEmail());
            saved.setBirthDate(user.getBirthDate());

            userRepository.save(saved);
        }
    }

    public void deleteUser(Integer id)
    {
        userRepository.deleteById(id);
    }

    public void unblockUser(Integer id)
    {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent())
        {
            User user = optUser.get();
            user.setBlocked(false);
            user.setBlockingEnd(null);
            userRepository.save(user);
        }
        else
        {
            throw new UsernameNotFoundException("User with such id doesn't exist");
        }
    }

    public void blockUser(Integer id, Date blockingEnd)
    {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent())
        {
            User user = optUser.get();
            user.setBlocked(true);
            user.setBlockingEnd(blockingEnd);
            userRepository.save(user);
        }
        else
        {
            throw new UsernameNotFoundException("User with such id doesn't exist");
        }
    }

    public boolean isUserAdmin(Integer id)
    {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent())
        {
            User user = optUser.get();
            return (user.getRole().getName() == ERole.ROLE_ADMIN);
        }
        throw new UsernameNotFoundException("User with such id doesn't exist");
    }
}
