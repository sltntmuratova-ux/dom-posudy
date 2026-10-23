package kg.shop.config;

import kg.shop.repository.PageRepository;
import kg.shop.service.CartService;
import kg.shop.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final CartService cartService;
    private final SettingsService settingsService;
    private final PageRepository pageRepository;

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getCart().getTotalItems();
    }

    @ModelAttribute("settings")
    public Object settings() {
        return settingsService.get();
    }

    @ModelAttribute("footerPages")
    public Object footerPages() {
        return pageRepository.findByShowInFooterTrueAndPublishedTrueOrderBySortOrderAsc();
    }
}