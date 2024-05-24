package com.example.library.controllers;

import com.example.library.config.CustomUserDetails;
import com.example.library.dto.PasswordDto;
import com.example.library.dto.PeriodDto;
import com.example.library.dto.UserDto;
import com.example.library.enums.IssuanceStatus;
import com.example.library.models.Issuance;
import com.example.library.models.User;
import com.example.library.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private BlockService blockService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private IssuanceService issuanceService;
    @Autowired
    private  ReturnService returnService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        model.addAttribute("messageBody", "Сheck your mail and follow the link from the letter.\n");
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
    public String savePassword(@ModelAttribute("passwordDto") PasswordDto passwordDto, RedirectAttributes redirectAttributes, Model model)
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
                redirectAttributes.addFlashAttribute("passwordChanged", true);
                return "redirect:/login";
            }
        }
        return "redirect:/reset-password?token=" + passwordDto.getToken() + "&error=true";
    }
    @GetMapping(value = "/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails user,
                          @ModelAttribute("period") PeriodDto period,
                          Model model)
    {
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
            List<Issuance> issuances = (List<Issuance>) (filter ? issuanceService.getAllByPeriod(from, to, userId) : issuanceService.getByUserId(userId));
            model.addAttribute("issuances", issuances);
            model.addAttribute("activeIssuances", issuances.stream().filter(i -> i.getStatus() != IssuanceStatus.RETURNED).toList());
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
}
