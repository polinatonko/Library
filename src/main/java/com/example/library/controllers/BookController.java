package com.example.library.controllers;

import com.example.library.GlobalFunctions;
import com.example.library.config.CustomUserDetails;
import com.example.library.dto.*;
import com.example.library.enums.BookingStatus;
import com.example.library.models.*;
import com.example.library.services.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;


@Controller
@RequestMapping(value = "/books")
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
    private NotificationService notificationService;
    @Autowired
    private FilterSpecification<Book> filters;
    @Autowired
    private GlobalFunctions utils;
    @GetMapping
    public String index(
            @RequestParam(value = "author", required = false) String selectedAuthors,
            @RequestParam(value = "publisher", required = false) String selectedPublishers,
            @RequestParam(value = "format", required = false) String selectedFormats,
            @RequestParam(value = "genre", required = false) String selectedGenres,
            @RequestParam(value = "min", required = false) String minAge,
            @RequestParam(value = "max", required = false) String maxAge,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model,
            HttpServletRequest httpRequest)
    {

        RequestDto request = new RequestDto();
        request.setPageDto(new PageRequestDto(page, size));


        if (minAge == null )
            minAge = String.valueOf(bookService.getMinAge());

        if (maxAge == null)
            maxAge = String.valueOf(bookService.getMaxAge());

        Page<Book> bookPage = bookService.getSearchPaginatedPage(request, selectedAuthors, selectedPublishers, selectedFormats, selectedGenres, minAge, maxAge, keywords);

        model.addAttribute("form", new ObjectsListDto<Book>(bookPage));
        model.addAttribute("authors", authorService.getAll());
        model.addAttribute("publishers", publisherService.getAll());
        model.addAttribute("genres", genreService.getAll());
        model.addAttribute("minAge", bookService.getMinAge());
        model.addAttribute("maxAge", bookService.getMaxAge());

        model.addAttribute("selectedAuthors", selectedAuthors != null ? selectedAuthors : "");
        model.addAttribute("selectedPublishers", selectedPublishers != null ? selectedPublishers : "");
        model.addAttribute("selectedFormats", selectedFormats != null ? selectedFormats : "");
        model.addAttribute("selectedGenres", selectedGenres != null ? selectedGenres : "");
        model.addAttribute("selectedMinAge", minAge);
        model.addAttribute("selectedMaxAge", maxAge);

        String url = utils.getCurrentUrl(httpRequest);
        if (url.contains("page="))
        {
            url = url.replaceFirst("[&?]page=[\\d]+[?]?", "");
            System.out.println(url);
        }
        char firstDelimeter = url.contains("=") ? '&' : '?';

        model.addAttribute("currentUrl", url);
        model.addAttribute("firstDelimeter", firstDelimeter);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "lists/books";
    }
    @ResponseBody
    @PostMapping(value = "/post")
    public List<Book> search(@RequestBody RequestDto requestDto)
    {
        Specification<Book> spec = filters.getSearchSpecification(requestDto.getSearchRequestDtos());
        return (List<Book>)bookService.search(spec);
    }
    @GetMapping(value = "/notify/{id}")
    public String notify(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer editionId,
            Model model
    )
    {
        Book book = bookService.getById(editionId);
        Optional<User>  user = userService.getAuthUser(userDetails);
        if (user.isPresent())
        {
            notificationService.create(user.get(), book);
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

    @PostMapping(value = "/cancel-book/{id}")
    public String cancelBooking (
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            @PathVariable("id") Integer id,
            Model model)
            throws AccessDeniedException {
        bookingService.checkPermission(userDetails, id);
        bookingService.cancelBooking(id);
        return utils.getPreviousUrl(request);
    }

    @PostMapping(value = "/book/{id}")
    public String book(
            HttpServletRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer editionId,
            @ModelAttribute("booking") BookingDto bookingDto,
            RedirectAttributes redirectAttributes,
            Model model)
    {
        User user = userService.getAuthUser(userDetails).get(); // != null because auth user
        Book book = bookService.getById(editionId);

        // check age
        Integer userAge = userService.getAgeById(user.getId());

        // too small
        if (userAge < book.getAgeLimit())
        {
            redirectAttributes.addFlashAttribute("small", true);
            return utils.getPreviousUrl(request);
        }

        if (bookingService.checkBooking(book.getId(), user.getId()) == BookingStatus.ENABLED)
        {
            // !!! for testing
            //utils =  utils.getDate(0, 0, 1, 0);
            Date lastDay = bookingDto.getLastDay();

            System.out.println(lastDay);
            bookingService.create(
                    book, user, lastDay,
                    new TimerTask (){
                        @Override public void run() { bookingService.cancelBooking(editionId, user.getId());}
                        }
                    );
        }
        return utils.getPreviousUrl(request);
    }
    @Secured({"ROLE_ADMIN","ROLE_LIBRARIAN"})
    @GetMapping(value = "/add")
    public String addForm(Model model)
    {
        model.addAttribute("object", new Book());
        return "addForms/book";
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

    @GetMapping(value = "/preview/{id}")
    public String preview(
            @PathVariable("id") Integer id,
            Model model
    )
    {
        Book book = bookService.getById(id);
        model.addAttribute("object", book);
        return "profiles/book-preview";
    }

    @GetMapping(value = "/history")
    public String history(
            @CookieValue(value = "historyOfViews", required = false) String cookieValue,
            Model model
    )
    {
        List<Integer> views;

        if (cookieValue == null)
        {
            views = new ArrayList<>();
        }
        else
        {
            String[] cookieValues = cookieValue.split("a");
            views = new ArrayList<>(Arrays.stream(cookieValues)
                    .map(Integer::parseInt)
                    .toList());
        }

        List<Book> books = bookService.getByIds(views);

        model.addAttribute("books", books.reversed());

        return "lists/history";
    }

    @GetMapping(value = "/{id}")
    public String card(
            @CookieValue(value = "historyOfViews", required = false) String cookieValue,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer id,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model,
            HttpServletResponse response,
            HttpServletRequest httpRequest
    )
    {
        List<Integer> views;

        if (cookieValue == null)
        {
            views = Arrays.asList(id);
        }
        else
        {
            String[] cookieValues = cookieValue.split("a");
            views = new ArrayList<>(Arrays.stream(cookieValues)
                    .map(Integer::parseInt)
                    .toList());

            views.remove(id);

            if (views.size() == 10)
                views.removeLast();

            views.addFirst(id);
        }

        cookieValue = String.join("a", views.stream().map(String::valueOf).toList());
        Cookie cookie = new Cookie("historyOfViews", cookieValue);
        cookie.setMaxAge(3600);
        response.addCookie(cookie);


        Book book = bookService.getById(id);

        Optional<User> user = userService.getAuthUser(userDetails);
        model.addAttribute("object", book);
        model.addAttribute("booking", new BookingDto());
        model.addAttribute("bookingStatus",
                user.isPresent() ? bookingService.checkBooking(id, userDetails.getId()) : BookingStatus.DISABLED);
        model.addAttribute("showLike",
                user.isPresent() && likeService.checkLike(id, userDetails.getId()));
        model.addAttribute("disabled",
                user.isPresent() && notificationService.checkNotification(id, userDetails.getId()));
        if (user.isPresent())
        {
            Integer userId = userDetails.getId();
            model.addAttribute("reviewExists", reviewService.existsByIds(userId, id));
            model.addAttribute("issuanceExists", issuanceService.exists(id, userId));
        }

        RequestDto request = new RequestDto();
        size=Optional.of(1);
        request.setPageDto(new PageRequestDto(page, size));

        Page<Review> reviewPage = reviewService.getBookReviews(id, request);
        model.addAttribute("form", new ObjectsListDto<Review>(reviewPage));

        String url = utils.getCurrentUrl(httpRequest);
        if (url.contains("page="))
        {
            url = url.replaceFirst("[&?]page=[\\d]+[?]?", "");
            System.out.println(url);
        }
        char firstDelimeter = url.contains("=") ? '&' : '?';

        model.addAttribute("currentUrl", url);
        model.addAttribute("firstDelimeter", firstDelimeter);

        int totalPages = reviewPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
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
