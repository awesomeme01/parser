package kz.market.parser.parser;

import kz.market.parser.model.Listing;
import kz.market.parser.model.dto.AgencyCard;
import kz.market.parser.model.dto.MainInformation;
import kz.market.parser.model.dto.PropertyInformation;
import kz.market.parser.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Select;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author shabdan
 * <p>
 * Main parsing snippet
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MainParser implements ApplicationRunner {

    private final ListingRepository listingRepository;

    private static final Map<String, String> URLS = Map.of(
            "apartments", "https://www.bayut.com/for-sale/apartments/uae/",
            "villas", "https://www.bayut.com/for-sale/villas/uae/",
            "townhouses", "https://www.bayut.com/for-sale/townhouses/uae/",
            "penthouse", "https://www.bayut.com/for-sale/penthouse/uae/",
            "villa-compound", "https://www.bayut.com/for-sale/villa-compound/uae/",
            "hotel-apartments", "https://www.bayut.com/for-sale/hotel-apartments/uae/",
            "residential-plots", "https://www.bayut.com/for-sale/residential-plots/uae/",
            "residential-floors", "https://www.bayut.com/for-sale/residential-floors/uae/",
            "residential-building", "https://www.bayut.com/for-sale/residential-building/uae/"
    );

    private static final String ELEMENT_URL_FORMAT = "https://www.bayut.com%s";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (String category : URLS.keySet()) {
            log.info("======== CATEGORY {} ========", category);
            var url = URLS.get(category);
            var doc = Jsoup.connect(url).get();
            var elements = doc.select(Selectors.LISTING);

            int c = 1;
            for (Element el : elements) {
                if (c > 2) {
                    break;
                }

                var elementUrl = el.attr("href");
                log.info("{}) surf(category={}, href={})", c, category, elementUrl);
                var elementUrlConn = String.format(ELEMENT_URL_FORMAT, elementUrl);
                var elementDoc = Jsoup.connect(elementUrlConn).get();
                final MainInformation mainInformation = fillMainInformation(elementDoc);
                final AgencyCard agencyCard = fillAgencyCard(elementDoc);
                final PropertyInformation propertyInformation = fillPropertyInformation(elementDoc);
                final Listing listing = Listing.builder()
                        .mainInformation(mainInformation)
                        .agencyCard(agencyCard)
                        .propertyInformation(propertyInformation)
                        .category(category)
                        .url(elementUrlConn)
                        .build();
                listingRepository.save(listing);
                c++;
            }
        }
    }

    private MainInformation fillMainInformation(Document document) {
        return MainInformation.builder()
                .name(selectFirstString(document, Selectors.Main.NAME))
                .price(selectFirstString(document, Selectors.Main.PRICE))
                .interval(selectFirstString(document, Selectors.Main.INTERVAL))
                .imageUrls(document.select(Selectors.Main.IMAGES).eachAttr("src"))
                .beds(selectFirstString(document, Selectors.Main.BEDS))
                .sqft(selectFirstString(document, Selectors.Main.SQFT))
                .description(selectFirstString(document, Selectors.Main.DESCRIPTION))
                .baths(selectFirstString(document, Selectors.Main.BATHS))
                .build();
    }

    private AgencyCard fillAgencyCard(Document document) {
        return AgencyCard.builder()
                .agentUrl(String.format(ELEMENT_URL_FORMAT, selectFirstAttribute(document, Selectors.Agency.AGENT_URL, "href")))
                .agencyName(selectFirstString(document, Selectors.Agency.AGENCY_NAME))
                .agencyImage(selectByIndexIfExists(document, Selectors.Agency.AGENCY_IMAGE, "data-src", 1))
                .registrationNumber(selectFirstString(document, Selectors.Agency.REGISTRATION_NUMBER))
                .agentName(selectFirstString(document, Selectors.Agency.AGENT_NAME))
                .agentPhoto(selectFirstAttribute(document, Selectors.Agency.AGENT_PHOTO, "src"))
                .build();
    }

    private PropertyInformation fillPropertyInformation(Document document) {
        return PropertyInformation.builder()
                .type(selectFirstString(document, Selectors.PropertyInfo.TYPE))
                .purpose(selectFirstString(document, Selectors.PropertyInfo.PURPOSE))
                .reference(selectFirstString(document, Selectors.PropertyInfo.REFERENCE))
//                .furnishing(selectFirstString(document, Selectors.PropertyInfo.FURNISHING))
                .datePosted(selectFirstString(document, Selectors.PropertyInfo.DATE_POSTED))
                .completion(selectFirstString(document, Selectors.PropertyInfo.COMPLETION))
                .averageRent(selectFirstString(document, Selectors.PropertyInfo.AVERAGE_RENT))
                .trueCheck(selectFirstString(document, Selectors.PropertyInfo.TRUE_CHECK))
                .build();

    }

    private String selectByIndexIfExists(Document document, String selector, String attr, int index) {
        Elements elements = document.select(selector);
        if (elements == null) {
            return null;
        }
        if (elements.size() <= index) {
            return elements.last().attr(attr);
        }
        return elements.get(index).attr(attr);
    }

    private String selectFirstString(Document document, String selector) {
        Element element = document.selectFirst(selector);
        return element != null ? element.text() : null;
    }

    private String selectFirstAttribute(Document document, String selector, String attributeName) {
        Element element = document.selectFirst(selector);
        return element != null ? element.attr(attributeName) : null;
    }
}
