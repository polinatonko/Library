package com.example.library.controllers;
import com.example.library.GlobalFunctions;
import com.example.library.config.CustomUserDetails;
import com.example.library.dto.*;
import com.example.library.enums.BookingStatus;
import com.example.library.enums.ERole;
import com.example.library.models.*;
import com.example.library.repositories.ActionRepository;
import com.example.library.repositories.IssuanceRepository;
import com.example.library.repositories.RoleRepository;
import com.example.library.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import java.util.*;

@Controller
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private IssuanceService issuanceService;
    @Autowired
    private  ReturnService returnService;
    @Autowired
    private BookService bookService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ActionRepository actionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GlobalFunctions utils;
    private void checkLibrarianPermission(HttpServletRequest request, Integer id)
    {
        if (request.isUserInRole("ROLE_LIBRARIAN") && userService.isUserAdmin(id))
            throw new AccessDeniedException("User must have role ADMIN");
    }
    @PostMapping(value = "/cancel-issuance/{id}")
    public String deleteIssuance(HttpServletRequest request,
                                 @PathVariable("id") Integer id,
                                 Model model)
    {
        Issuance issuance = issuanceService.getById(id);
        issuanceService.delete(issuance);

        return utils.getPreviousUrl(request);
    }
    @PostMapping(value = "/create-issuance")
    public String issuance(HttpServletRequest request,
                           @ModelAttribute("action") @Valid IssuanceDto issuanceDto,
                           Model model)
    {
        Integer editionId = issuanceDto.getEditionId(), userId = issuanceDto.getUserId();
        Edition edition = bookService.getById(editionId);
        User user = userService.getById(userId).get(); // fix!

        // create and save:
        Issuance issuance = new Issuance(edition, user, utils.getDate(30, 0, 0, 0), utils.getCurentDate());
        issuanceService.create(issuance);

        // cancel booking, if exists:
        BookingStatus bookingStatus = bookingService.checkBooking(editionId, userId);
        if (bookingStatus == BookingStatus.DISABLED)
            bookingService.cancelBooking(editionId, userId);

        return "redirect:/manage-actions?type=issuance";

    }
    @PostMapping(value = "/create-return")
    public String returns(HttpServletRequest request,
                          @ModelAttribute("action") @Valid IssuanceDto issuanceDto,
                        RedirectAttributes redirectAttributes,
                        Model model)
    {
        Integer editionId = issuanceDto.getEditionId(), userId = issuanceDto.getUserId();
        Edition edition = bookService.getById(editionId);
        User user = userService.getById(userId).get(); // fix!

        // check if issuance exists
        if (issuanceService.exists(editionId, userId))
        {
            // create and save:
            Return ret = new Return(edition, user, utils.getDate(0, 0, 0, 0));
            returnService.create(ret);
        }
        else
        {
            redirectAttributes.addFlashAttribute("returnError", true);
            return utils.getPreviousUrl(request);
        }

        return "redirect:/manage-actions?type=return";
    }
    @GetMapping(value = "/issuance")
    public String issuanceForm(HttpServletRequest request,
                               Model model)
    {
        model.addAttribute("books", bookService.getAll());
        model.addAttribute("users", userService.getEnabledUsers());
        model.addAttribute("action", new IssuanceDto());
        return "addForms/issuance";
    }
    @GetMapping(value = "/return")
    public String returnForm(HttpServletRequest request,
                             Model model)
    {
        model.addAttribute("books", bookService.getAll());
        model.addAttribute("users", userService.getEnabledUsers());
        model.addAttribute("action", new IssuanceDto());
        return "addForms/return";
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

        userService.blockUser(id, utils.getDate(0, 0, 1, 0));
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
        model.addAttribute("title", "Add user");
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
    @GetMapping(value = "/manage-actions")
    public String manageActions(
            HttpServletRequest request,
            @RequestParam("type") String type,
            @ModelAttribute("period") PeriodDto period,
            Model model)
    {
        String template = "lists/";
        boolean filter = period.getFrom() != null;
        Date from = period.getFrom(), to = period.getTo();
        switch (type) {
            case ("block"):
                model.addAttribute("actions", filter ? blockService.getByPeriod(from, to) : blockService.getAll());
                template += "blocks";
                break;
            case ("booking"):
                model.addAttribute("actions", filter ? bookingService.getByPeriod(from, to) : bookingService.getAll());
                template += "bookings";
                break;
            case ("issuance"):
                model.addAttribute("actions", filter ? issuanceService.getIssuancesByPeriod(from, to) : issuanceService.getAll());
                template += "issuances";
                break;
            case ("return"):
                model.addAttribute("actions", filter ? returnService.getByPeriod(from, to) : returnService.getAll());
                template += "returns";
                break;
            default:
                throw new InvalidParameterException("There is no resource on such uri!");
        }
        model.addAttribute("period", period);
        return template;
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

    @PostMapping(value = "/reset-password")
    public String resetPassword(@ModelAttribute("email") String email,
                                Model model)
    {
        Optional<User> user = userService.getByEmail(email);
        if (user.isEmpty())
        {
            return "redirect:/forgot-password?error=true";
        }

        userService.sendResetPasswordEmail(userService.createPasswordResetToken(user.get()));
        model.addAttribute("messageHeader", "Password reset mail was send!");
        model.addAttribute("messageBody", "Сheck your mail and follow the link from the letter\n");
        return "pages/message";
    }

    @GetMapping(value = "/reset-password")
    public String resetPasswordForm(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("token") String token,
            Model model)
    {
        String res = securityService.checkToken(token);
        if (res != null)
        {
            return "redirect:";
        }
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setToken(token);
        model.addAttribute("passwordDto", passwordDto);
        return "profiles/updatePassword";
    }

    @GetMapping(value = "/forgot-password")
    public String forgotPassword()
    {
        return "profiles/resetPassword";
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
    public String profile(@AuthenticationPrincipal CustomUserDetails user,
                          @ModelAttribute("period") PeriodDto period,
                          Model model)
    {
        model.addAttribute("title", "Profile");
        Optional<User> optAuthUser = userService.getById(user.getId());
        if (optAuthUser.isPresent()) // выполняется всегда, т. к. profile доступен только авторизованным пользователям
        {
            User authUser = optAuthUser.get();
            model.addAttribute("user", authUser);
            UserDto userDto = new UserDto();
            userDto.setBirthDate(Calendar.getInstance().getTime());
            model.addAttribute("userDto", userDto);

            Integer userId = authUser.getId();
            boolean filter = period.getFrom() != null;
            Date from = period.getFrom(), to = period.getTo();
            model.addAttribute("bookings", filter ? bookingService.getByPeriodAndUserId(from, to, userId) : bookingService.getUserBookings(userId));
            model.addAttribute("likes", likeService.getLikedEditions(userId));
            model.addAttribute("issuances", filter ? issuanceService.getIssuancesByPeriod(from, to, userId) : issuanceService.getIssuances(userId));
            model.addAttribute("returns", filter ? returnService.getByPeriodAndId(from, to, userId) : returnService.getReturns(userId));
            model.addAttribute("reviews", reviewService.getByUserId(userId));
            model.addAttribute("period", period);
            return "profiles/reader_profile";
        }
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
            userService.sendNewEmailConfirmationMail(auth_user, new_email);

            /*User auth_user = userService.getById(user.getId()).get();
            auth_user.setEmail(new_email);

            userService.save(auth_user);*/
        }

        model.addAttribute("changedEmail", new_email);
        return "redirect:/profile";
    }

    @GetMapping(value = "/confirm-email")
    public String changeEmail(@RequestParam("token")String token, Model model)
    {
        model.addAttribute("title", "New email confirmation");
        model.addAttribute("message", userService.changeEmail(token));
        return "confirmation";
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
        model.addAttribute("title", "Login");
        return "auth/login";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "add-user")
    public String addUser(@RequestBody User user, Model model) {
        if (userService.existsByEmail(user.getEmail()))
        {
            model.addAttribute("title","Error");
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
