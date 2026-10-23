package kg.shop.controller;

import kg.shop.model.Page;
import kg.shop.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final PageRepository pageRepository;

    @GetMapping("/p/{slug}")
    public String view(@PathVariable String slug, Model model) {
        Page page = pageRepository.findBySlugAndPublishedTrue(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("page", page);
        return "page";
    }
}