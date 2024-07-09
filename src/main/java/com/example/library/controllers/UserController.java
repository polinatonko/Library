package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.config.CustomUserDetails;
import com.example.library.dto.*;
import com.example.library.enums.ERole;
import com.example.library.models.*;
import com.example.library.repositories.RoleRepository;
import com.example.library.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

import java.security.InvalidParameterException;

@Controller
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private BookService bookService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GlobalFunctions utils;
    @Autowired
    private PasswordEncoder passwordEncoder;
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

        userService.blockUser(id, utils.getDate(0, 0, 3, 0));
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
                userService.registerUser(userDto, userDto.getRole());
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
            return "addForms/addUser";
        }

        return "redirect:/manage-users?role=all";
    }
    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/add-user")
    public String addUserForm(Model model)
    {
        model.addAttribute("user", new UserDto());
        return "addForms/addUser";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "users/delete-user/{id}")
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

        User edited = form.getObjects().getLast();
        edited.setId(id);
        userService.updateUser(edited);
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/manage-users")
    public String manageUsers(HttpServletRequest request, @RequestParam("role") String role, Model model)
    {
        Iterable<User> users;
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
                    users = userService.getAllUsersForAdmin();
                else
                    users = userService.getAllUsersForLibrarian();
                break;
            default:
                throw new InvalidParameterException("There is no resource on such uri!");
        }
        model.addAttribute("form", new ObjectsListDto<User>(users));
        model.addAttribute("new_user", new UserDto());
        return "lists/manageUsers";
    }
    @GetMapping(value = "/reader/register")
    public String registration(Model model)
    {
        model.addAttribute("user", new UserDto());
        return "auth/registration";
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
            if (userDto.validatePassword())
            {
                try
                {
                    userService.registerUser(userDto, ERole.ROLE_READER);
                }
                catch (IllegalArgumentException ex)
                {
                    bindingResult.rejectValue("email", "userDto.email", "User with this email already exists");
                }
            }
            else
                bindingResult.rejectValue("password", "userDto.password",
                        "The password must contain at least 8 characters, at least 1 letter and one number");
        }
        else
        {
            bindingResult.rejectValue("matchingPassword", "userDto.matchingPassword", "Passwords should be same");
        }

        if (bindingResult.hasErrors())
        {
            return "auth/registration";
        }

        model.addAttribute("user", userDto);
        return "auth/successRegister";
    }

    @RequestMapping(value = "/login")
    public String login(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {
        if (user != null)
        {
            return "redirect:";
        }
        return "auth/login";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "add-user")
    public String addUser(@RequestBody User user, Model model) {
        if (userService.existsByEmail(user.getEmail()))
        {
            model.addAttribute("error_msg","User with this email exists!");

            return "pages/error";
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

        return "redirect:";
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token")String token, Model model) {
        model.addAttribute("message", userService.confirmEmail(token));
        return "confirmation";
    }

}
