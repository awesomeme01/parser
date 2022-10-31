package kz.market.parser.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author shabdan
 *
 * Main parsing snippet
 */
@Component
public class MainPageParsing implements ApplicationRunner {

    private static final Map<String,String> URLS = Map.of(
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

    private static class Selectors {
        private static final String LISTING = "ul > li > article > div > a";

        private static class Main {
            private static final String NAME = "";
            private static final String PRICE = "";
            private static final String IMAGES = "";
            private static final String BEDS = "";
            private static final String BATHS = "";
            private static final String SQFT = "";
            private static final String DESCRIPTION = "";
        }

        private static class Agency {
            private static final String NAME = "";
            private static final String IMAGE = "";
            private static final String REGISTRATION_NUMBER = "";
            private static final String LICENSE_NUMBER = "";
            private static final String AGENT_NAME = "";
            private static final String AGENT_PHOTO = "";
        }

        private static class PROPERTY_INFO {
            private static final String TYPE = "";
            private static final String PURPOSE = "";
            private static final String REFERENCE = "";
            private static final String FURNISHING = "";
            private static final String DATE_POSTED = "";
            private static final String COMPLETION = "";
            private static final String AVERAGE_RENT = "";
        }

        private static class FEATURES {
            private static final String ALL_FEATURES = "";
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (String category : URLS.keySet()) {
            var url = URLS.get(category);
            var doc = Jsoup.connect(url).get();
            var elements = doc.select(Selectors.LISTING);
            int c = 1;
            for (Element el : elements) {
                if (c > 5) {
                    break;
                }
                var elementUrl = el.attr("href");
                System.out.printf("%s) surf(category=%s, href=%s)\n", c, category, elementUrl);

                var elementDoc = Jsoup.connect(String.format(ELEMENT_URL_FORMAT, elementUrl));
                c++;
            }
        }
    }
}
