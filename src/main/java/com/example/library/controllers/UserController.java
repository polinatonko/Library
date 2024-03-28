package com.example.library.controllers;
import com.example.library.GlobalFunctions;
import com.example.library.config.CustomUserDetails;
import com.example.library.dto.PasswordDto;
import com.example.library.dto.UsersListDto;
import com.example.library.enums.ERole;
import com.example.library.models.Role;
import com.example.library.models.User;
import com.example.library.dto.UserDto;
import com.example.library.repositories.RoleRepository;
import com.example.library.services.SecurityService;
import com.example.library.services.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GlobalFunctions utils;
    private void checkLibrarianPermission(HttpServletRequest request, Integer id)
    {
        if (request.isUserInRole("ROLE_LIBRARIAN") && userService.isUserAdmin(id))
            throw new AccessDeniedException("User must have role ADMIN");
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/unblock-user/{id}")
    public String unblockUser(HttpServletRequest request,
                              @PathVariable("id") Integer id,
                              Model model)
    {
        checkLibrarianPermission(request, id);

        userService.unblockUser(id);
        return utils.getPreviousUrl(request);
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/block-user/{id}")
    public String blockUser(HttpServletRequest request,
                              @PathVariable("id") Integer id,
                              Model model)
    {
        checkLibrarianPermission(request, id);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24 * 30);
        userService.blockUser(id, new Date(cal.getTime().getTime()));
        return utils.getPreviousUrl(request);
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/add-user-post")
    public String createUserAccount(
            HttpServletRequest request,
            @ModelAttribute("user") @Valid UserDto userDto,
            Model model,
            BindingResult bindingResult
    )
    {
        if (userDto.checkEqualPasswords())
        {
            try
            {
                User registered = userService.registerUser(userDto, userDto.getRole());
            }
            catch (IllegalArgumentException ex)
            {
                bindingResult.rejectValue("email", "userDto.email", "User with this email already exists");
            }
        }
        else
        {
            bindingResult.rejectValue("matchingPassword", "userDto.matchingPassword", "Passwords should be same");
        }

        if (bindingResult.hasErrors())
        {
            return "addUser";
        }

        return "redirect:/manage-users?role=all";
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/add-user")
    public String addUserForm(Model model)
    {
        model.addAttribute("title", "Add user");
        model.addAttribute("user", new UserDto());
        return "addUser";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/delete-user/{id}")
    public String deleteUser(HttpServletRequest request,
                             @PathVariable("id") Integer id,
                             Model model)
    {
        checkLibrarianPermission(request, id);

        userService.deleteUser(id);
        return utils.getPreviousUrl(request);
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/edit-user/{id}")
    public String editUser(HttpServletRequest request,
                           @PathVariable("id") Integer id,
                           @ModelAttribute("form") UsersListDto form,
                           Model model)
    {
        checkLibrarianPermission(request, id);

        User edited = form.getUsers().getLast();
        edited.setId(id);
        userService.updateUser(edited);
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/manage-users")
    public String manageUsers(HttpServletRequest request, @RequestParam("role") String role, Model model)
    {
        List<User> users;
        switch (role) {
            case ("admin"):
                if (!request.isUserInRole("ROLE_ADMIN"))
                    throw new AccessDeniedException("User must have role ADMIN to access this source!");

                users = userService.getUsersByRole(ERole.ROLE_ADMIN);
                break;
            case ("librarian"):
                users = userService.getUsersByRole(ERole.ROLE_LIBRARIAN);
                break;
            case ("reader"):
                users = userService.getUsersByRole(ERole.ROLE_READER);
                break;
            case ("all"):
                if (request.isUserInRole("ROLE_ADMIN"))
                    users = (List<User>) userService.getAllUsersForAdmin();
                else
                    users = (List<User>) userService.getAllUsersForLibrarian();
                break;
            default:
                throw new InvalidParameterException("There is no resource on such uri!");
        }
        model.addAttribute("users", users);
        model.addAttribute("form", new UsersListDto(users));
        model.addAttribute("new_user", new UserDto());
        return "manageUsers";
    }

    @PostMapping(value = "/reset-password")
    public String resetPassword(@ModelAttribute("email") String email,
                                BindingResult bindingResult,
                                Model model)
    {
        User user = userService.getByEmail(email);
        if (user == null)
        {
            return "redirect:/forgot-password?error=true";
        }

        userService.sendResetPasswordEmail(userService.createPasswordResetToken(user));
        model.addAttribute("messageHeader", "Password reset mail was send!");
        model.addAttribute("messageBody", "Сheck your mail and follow the link from the letter\n");
        return "message";
    }

    @GetMapping(value = "/reset-password")
    public String resetPasswordForm(HttpServletRequest request, HttpServletResponse response, @RequestParam("token") String token, Model model)
    {
        String res = securityService.checkToken(token);
        if (res != null)
        {
            return "redirect:";
        }
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setToken(token);
        model.addAttribute("passwordDto", passwordDto);
        return "updatePassword";
    }

    @GetMapping(value = "/forgot-password")
    public String forgotPassword()
    {
        return "resetPassword";
    }

    @PostMapping(value = "/save-password")
    public String savePassword(@ModelAttribute("passwordDto") PasswordDto passwordDto, Model model)
    {
        String res = securityService.checkToken(passwordDto.getToken());

        if (res != null)
        {
            return "redirect:/reset-password?token=" + passwordDto.getToken() + "&error=true";
        }

        Optional<User> user = userService.findUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent())
        {
            String pwd = passwordDto.getPassword(),
                    matchPwd = passwordDto.getMatchingPassword();

            if (pwd.equals(matchPwd)) {
                userService.changePassword(user.get(), pwd);
                model.addAttribute("messageHeader", "Success password reset!");
                model.addAttribute("messageBody", "Log in our application");
                return "login";
            }
        }
        return "redirect:/reset-password?token=" + passwordDto.getToken() + "&error=true";
    }

    @GetMapping(value = "/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails user, Model model)
    {
        model.addAttribute("title", "Profile");
        Optional<User> optAuthUser = userService.getById(user.getId());
        if (optAuthUser.isPresent())
        {
            User authUser = optAuthUser.get();
            model.addAttribute("user", authUser);
            UserDto userDto = new UserDto();
            userDto.setBirthDate(Calendar.getInstance().getTime());
            model.addAttribute("userDto", userDto);
            return "reader_profile";
        }
        // user profile not found !
        return "redirect:";
    }

    @PostMapping(value = "/profile")
    public String saveChangedEmail(
            @AuthenticationPrincipal CustomUserDetails user,
            @ModelAttribute("email") @Valid String new_email,
            Model model,
            RedirectAttributes redirectAttributes)
    {
        if (new_email == null || new_email.isEmpty() || userService.existsByEmail(new_email))
        {
            model.addAttribute("title", "Profile");
            model.addAttribute("user", userService.getById(user.getId()).get());

            String msg = userService.existsByEmail(new_email) ? "User with this email already exists!" : "Enter valid email!";
            redirectAttributes.addFlashAttribute("emailError", msg);
        }
        else
        {
            User auth_user = userService.getById(user.getId()).get();
            auth_user.setEmail(new_email);

            userService.save(auth_user);
        }

        return "redirect:/profile";
    }

    @PostMapping(value = "/change-password")
    public String changePassword(
            @AuthenticationPrincipal CustomUserDetails user,
            @ModelAttribute("userDto") UserDto userDto,
            Model model,
            RedirectAttributes redirectAttributes
    )
    {
        String pwd = userDto.getPassword(),
                matchPwd = userDto.getMatchingPassword();

        User authUser = userService.getById(user.getId()).get();

        if (pwd.equals(matchPwd)) {
            authUser.setPassword(passwordEncoder.encode(pwd));
            userService.save(authUser);

            redirectAttributes.addFlashAttribute("passwordSuccess", "Password was successfully changed!");
            return "redirect:/profile";
        }
        else
        {
            model.addAttribute("user", authUser);
            model.addAttribute("userDto", new UserDto());

            redirectAttributes.addFlashAttribute("passwordError", "Passwords don't match!");
        }

        return "redirect:/profile";
    }

    @GetMapping(value = "/reader/register")
    public String registration(Model model)
    {
        model.addAttribute("title", "Registration");
        model.addAttribute("user", new UserDto());
        return "registration";
    }

    @PostMapping(value = "/reader/register")
    public String registerUserAccount(
            @ModelAttribute("user") @Valid UserDto userDto,
            Model model,
            BindingResult bindingResult
    )
    {
        if (userDto.checkEqualPasswords())
        {
            try
            {
                User registered = userService.registerUser(userDto, ERole.ROLE_READER);
            }
            catch (IllegalArgumentException ex)
            {
                bindingResult.rejectValue("email", "userDto.email", "User with this email already exists");
            }
        }
        else
        {
            bindingResult.rejectValue("matchingPassword", "userDto.matchingPassword", "Passwords should be same");
        }

        if (bindingResult.hasErrors())
        {
            return "registration";
        }

        model.addAttribute("user", userDto);
        return "successRegister";
    }

    @RequestMapping(value = "/login")
    public String login(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        if (user != null)
        {
            return "redirect:";
        }
        model.addAttribute("title", "Login");
        return "login";
    }

    @PostMapping(value = "add-user")
    public String addUser(@RequestBody User user, Model model) {
        if (userService.existsByEmail(user.getEmail()))
        {
            model.addAttribute("title","Error");
            model.addAttribute("error_msg","User with this email exists!");

            return "error";
        }

        Role user_role = user.getRole(),
                saved_role = roleRepository.findByName(user.getRole().getName());

        if (saved_role == null)
        {
            roleRepository.save(user_role);
        }
        else
        {
            user.setRole(saved_role);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);

        model.addAttribute("title","Genres");
        return "redirect:";
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token")String token, Model model) {
        model.addAttribute("title", "Account confirmation");
        model.addAttribute("message", userService.confirmEmail(token));
        return "confirmation";
    }

}
