package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.config.CustomUserDetails;
import com.example.library.dto.ObjectsListDto;
import com.example.library.enums.BookingStatus;
import com.example.library.models.*;
import com.example.library.services.*;
import com.example.library.timer.BlockTimerTask;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Controller
@RequestMapping(value = "books")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private GenreService genreService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private PublisherService publisherService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private IssuanceService issuanceService;
    @Autowired
    private GlobalFunctions utils;
    @GetMapping
    public String index(@RequestParam(value = "search", required = false) String search, Model model)
    {

        model.addAttribute("form", new ObjectsListDto<Book>(bookService.getAll()));
        model.addAttribute("title", "Books");
        return "lists/books";
    }
    @PostMapping(value = "/notify/{id}")
    public String notify(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer editionId,
            Model model
    )
    {
        Book book = bookService.getById(editionId);
        Optional<User>  user = userService.getAuthUser(userDetails);
        Integer userId = userDetails.getId();
        if (user.isPresent())
        {
            // notify
        }
        return utils.getPreviousUrl(request);
    }
    @PostMapping(value = "/like/{id}")
    public String like(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer editionId,
            Model model)
    {
        Book book = bookService.getById(editionId);
        Optional<User>  user = userService.getAuthUser(userDetails);
        if (user.isPresent())
        {
            Integer userId = userDetails.getId();
            if (likeService.checkLike(editionId, userId))
                likeService.delete(editionId, userId);
            else
                likeService.add(book, user.get());
        }
        return utils.getPreviousUrl(request);
    }

    @PostMapping(value = "/cancel-book/{id}/{editionId}")
    public String cancelBooking (
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @PathVariable("editionId") Integer editionId,
            Model model)
    {
        bookingService.cancelBooking(editionId, id);
        return utils.getPreviousUrl(request);
    }

    @PostMapping(value = "/book/{id}")
    public String book(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer editionId,
            @ModelAttribute("lastDay") Date lastDay,
            Model model)
    {
        User user = userService.getAuthUser(userDetails).get(); // != null because auth user
        Book book = bookService.getById(editionId);
        if (book.getCopiesCount() > 0)
        {
            lastDay = utils.getDate(0, 0, 1, 0);
            bookingService.create(
                    book, user, lastDay,
                    new BlockTimerTask(user) { @Override public void run() { bookingService.cancelBooking(editionId, user.getId());}}
                    );
        }
        return utils.getPreviousUrl(request);
    }
    @Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"})
    @GetMapping(value = "/add")
    public String addForm(Model model)
    {
        model.addAttribute("title", "Add book");
        model.addAttribute("object", new Book());
        return "addForms/addBook";
    }

    @Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"})
    @PostMapping(value = "/add-post")
    public String add(
            HttpServletRequest request,
            @ModelAttribute("object") @Valid Book book,
            RedirectAttributes redirectAttributes,
            Model model,
            Errors errors)
    {
        if (errors.hasErrors())
        {
            return "redirect:/books/add";
        }

        book.setReceiptDate(utils.getDate(0, 0, 0, 0));
        bookService.save(book);

        return "redirect:/books";
    }

    @GetMapping(value = "/{id}")
    public String card(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer id,
            Model model
    )
    {
        Book book = bookService.getById(id);
        Optional<User> user = userService.getAuthUser(userDetails);
        model.addAttribute("object", book);
        model.addAttribute("lastDay", new Date());
        model.addAttribute("bookingStatus",
                user.isPresent() ? bookingService.checkBooking(id, userDetails.getId()) : BookingStatus.DISABLED);
        model.addAttribute("showLike",
                user.isPresent() && likeService.checkLike(id, userDetails.getId()));
        model.addAttribute("reviews", reviewService.getByEditionId(id));
        if (user.isPresent())
        {
            Integer userId = userDetails.getId();
            model.addAttribute("reviewExists", reviewService.existsByIds(userId, id));
            model.addAttribute("issuanceExists", issuanceService.exists(id, userId));
        }

        return "profiles/book";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @GetMapping(value = "/edit/{id}")
    public String edit(
            @PathVariable("id") Integer id,
            Model model
    )
    {
        Book book = bookService.getById(id);
        model.addAttribute("object", book);

        List<Genre> genres =
                new ArrayList<>(StreamSupport.stream(genreService.getAll().spliterator(), false)
                        .toList());

        genres.removeAll(book.getGenres());
        Genre addGenre = !genres.isEmpty() ? genres.getFirst() : new Genre();
        model.addAttribute("addGenre", addGenre);
        model.addAttribute("genres", genres);


        List<Author> authors =
                new ArrayList<>(StreamSupport.stream(authorService.getAll().spliterator(), false)
                        .toList());
        authors.removeAll(book.getAuthors());
        Author addAutor = !authors.isEmpty() ? authors.getFirst() : new Author();

        model.addAttribute("authors", authors);
        model.addAttribute("addAuthor", addAutor);

        model.addAttribute("publishers", publisherService.getAll());

        return "edit/editBook";
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/delete-genre/{id}/{genreId}")
    public String deleteGenre(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @PathVariable("genreId") Integer genreId,
            Model model
    )
    {
        bookService.deleteGenre(id, genreId);

        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/delete-author/{id}/{authorId}")
    public String deleteAuthor(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @PathVariable("authorId") Integer authorId,
            Model model
    )
    {
        bookService.deleteAuthor(id, authorId);
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/add-genre/{id}")
    public String addGenre(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @ModelAttribute("addGenre") Genre addGenre,
            Model model
    )
    {
        bookService.addGenre(id, addGenre.getId());
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/add-author/{id}")
    public String addAuthor(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @ModelAttribute("addAuthor") Author addAuthor,
            Model model
    )
    {
        bookService.addAuthor(id, addAuthor.getId());
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/edit/{id}")
    public String edit_post(
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            @ModelAttribute("object") Book book,
            Model model
    )
    {
        int publisherId = book.getPublisher().getId();
        Publisher newPublisher = publisherService.getById(publisherId);
        book.setPublisher(newPublisher);
        Book saved = bookService.getById(book.getId());
        book.setPhoto(saved.getPhoto());
        bookService.save(book);
        return utils.getPreviousUrl(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_LIBRARIAN"})
    @PostMapping(value = "/delete-book/{id}")
    public String delete(
            @PathVariable("id") Integer id,
            HttpServletRequest request,
            Model model
    )
    {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}
