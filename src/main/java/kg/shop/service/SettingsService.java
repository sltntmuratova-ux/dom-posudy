package kg.shop.service;

import kg.shop.dto.ShopSettings;
import kg.shop.model.Setting;
import kg.shop.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingRepository repository;

    private volatile ShopSettings cache;

    public ShopSettings get() {
        if (cache == null) {
            cache = load();
        }
        return cache;
    }

    public void save(ShopSettings s) {
        put("shop.name", s.getShopName());
        put("shop.phone", s.getPhone());
        put("shop.email", s.getEmail());
        put("shop.address", s.getAddress());
        put("shop.currency", s.getCurrency());
        put("shop.instagram", s.getInstagram());
        put("shop.whatsapp", s.getWhatsapp());
        put("shop.telegram", s.getTelegram());
        put("home.hero.title", s.getHeroTitle());
        put("home.hero.subtitle", s.getHeroSubtitle());
        put("home.about.text", s.getAboutText());
        put("delivery.cities", s.getDeliveryCities());
        put("delivery.price", s.getDeliveryPrice());
        put("delivery.free_from", s.getFreeDeliveryFrom());
        put("footer.requisites", s.getRequisites());
        put("seo.description", s.getSeoDescription()); // НОВОЕ
        put("seo.keywords", s.getSeoKeywords());       // НОВОЕ
        put("analytics.code", s.getAnalyticsCode());   // НОВОЕ

        cache = s; // сбрасываем кэш
    }

    private void put(String key, String value) {
        repository.save(new Setting(key, value == null ? "" : value));
    }

    private ShopSettings load() {
        Map<String, String> map = repository.findAll().stream()
                .collect(Collectors.toMap(Setting::getKey, Setting::getValue));

        ShopSettings s = new ShopSettings();
        if (map.containsKey("shop.name"))           s.setShopName(map.get("shop.name"));
        if (map.containsKey("shop.phone"))          s.setPhone(map.get("shop.phone"));
        if (map.containsKey("shop.email"))          s.setEmail(map.get("shop.email"));
        if (map.containsKey("shop.address"))        s.setAddress(map.get("shop.address"));
        if (map.containsKey("shop.currency"))       s.setCurrency(map.get("shop.currency"));
        if (map.containsKey("shop.instagram"))      s.setInstagram(map.get("shop.instagram"));
        if (map.containsKey("shop.whatsapp"))       s.setWhatsapp(map.get("shop.whatsapp"));
        if (map.containsKey("shop.telegram"))       s.setTelegram(map.get("shop.telegram"));
        if (map.containsKey("home.hero.title"))     s.setHeroTitle(map.get("home.hero.title"));
        if (map.containsKey("home.hero.subtitle"))  s.setHeroSubtitle(map.get("home.hero.subtitle"));
        if (map.containsKey("home.about.text"))     s.setAboutText(map.get("home.about.text"));
        if (map.containsKey("delivery.cities"))     s.setDeliveryCities(map.get("delivery.cities"));
        if (map.containsKey("delivery.price"))      s.setDeliveryPrice(map.get("delivery.price"));
        if (map.containsKey("delivery.free_from"))  s.setFreeDeliveryFrom(map.get("delivery.free_from"));
        if (map.containsKey("footer.requisites"))   s.setRequisites(map.get("footer.requisites"));
        if (map.containsKey("footer.requisites"))   s.setRequisites(map.get("footer.requisites"));
        if (map.containsKey("seo.description"))     s.setSeoDescription(map.get("seo.description"));
        if (map.containsKey("seo.keywords"))        s.setSeoKeywords(map.get("seo.keywords"));
        if (map.containsKey("analytics.code"))      s.setAnalyticsCode(map.get("analytics.code"));
        return s;
    }
}