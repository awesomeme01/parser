package kz.market.parser.parser;

import kz.market.parser.model.Listing;
import kz.market.parser.model.ListingForExportToJson;
import kz.market.parser.model.dto.AgencyCard;
import kz.market.parser.model.dto.MainInformation;
import kz.market.parser.model.dto.PropertyInformation;
import kz.market.parser.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    private static final Boolean EXPORT_TO_JSON = Boolean.TRUE;
    private static final String FILE_PATH = "parsed_listings.json";
    private static final Map<String, String> URLS = Map.of(
            "apartments",           "https://www.bayut.com/for-sale/apartments/uae/",
            "villas",               "https://www.bayut.com/for-sale/villas/uae/",
            "townhouses",           "https://www.bayut.com/for-sale/townhouses/uae/",
            "penthouse",            "https://www.bayut.com/for-sale/penthouse/uae/",
            "villa-compound",       "https://www.bayut.com/for-sale/villa-compound/uae/",
            "hotel-apartments",     "https://www.bayut.com/for-sale/hotel-apartments/uae/",
            "residential-plots",    "https://www.bayut.com/for-sale/residential-plots/uae/",
            "residential-floors",   "https://www.bayut.com/for-sale/residential-floors/uae/",
            "residential-building", "https://www.bayut.com/for-sale/residential-building/uae/"
    );
    private static final String ELEMENT_URL_FORMAT = "https://www.bayut.com%s";
    private final ListingRepository listingRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final List<ListingForExportToJson> jsons = new ArrayList<>();
        for (String category : URLS.keySet()) {
            log.info("======== CATEGORY {} ========", category);
            var url = URLS.get(category);
            var doc = Jsoup.connect(url).get();
            var elements = doc.select(Selectors.LISTING);

            int c = 1;
            for (Element el : elements) {
                if (c > 5) {
                    break;
                }

                var elementUrl = el.attr("href");
                log.info("{}) surf(category={}, href={})", c, category, elementUrl);
                var elementUrlConn = String.format(ELEMENT_URL_FORMAT, elementUrl);
                var elementDoc = Jsoup.connect(elementUrlConn).get();
                final MainInformation mainInformation = fillMainInformation(elementDoc);
                final AgencyCard agencyCard = fillAgencyCard(elementDoc);
                final PropertyInformation propertyInformation = fillPropertyInformation(elementDoc);
                final List<ListingForExportToJson.GuideInformation> guideInformations = fillGuidedInformation(elementDoc);
                final Listing listing = Listing.builder()
                        .mainInformation(mainInformation)
                        .agencyCard(agencyCard)
                        .propertyInformation(propertyInformation)
                        .category(category)
                        .guideInformation(guideInformations)
                        .url(elementUrlConn)
                        .build();
                if (EXPORT_TO_JSON) {
                    jsons.add(ListingForExportToJson.builder()
                            .name(listing.getMainInformation().getName())
                            .description(listing.getMainInformation().getDescription())
                            .originUrl(listing.getUrl())
                            .price(listing.getMainInformation().getPrice())
                            .media(listing.getMainInformation().getImageUrls())
                            .categoryName(category)
                            .features(getFeatures(listing))
                            .guideInformation(guideInformations)
                            .agency(agencyCard)
                            .build());
                }
                listingRepository.save(listing);
                c++;
            }
        }

        if (EXPORT_TO_JSON) {
            JAXBContext jc = org.eclipse.persistence.jaxb.JAXBContextFactory
                    .createContext(new Class[]{ListingForExportToJson.class, ListingForExportToJson.Feature.class}, null);


            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            marshaller.marshal(jsons, new File(FILE_PATH));
        }
    }

    private List<ListingForExportToJson.GuideInformation> fillGuidedInformation(Document elementDoc) {
        var guidedInfoList = new ArrayList<ListingForExportToJson.GuideInformation>();

        var guidedInfoBlock = elementDoc.select(Selectors.PropertyInfo.GUIDED_INFO);

        for (Element guideInfo : guidedInfoBlock) {
            var parent = guideInfo.parent();
            var children = parent.children();
            var index = 0;
            var guideInfoDto = new ListingForExportToJson.GuideInformation();
            for (Element child : children) {
                if (index == 0) {
                    guideInfoDto.setGuideLinkTitle(child.text());
                } else if (index == 1) {
                    guideInfoDto.setDescription(child.text());
                }
                index++;
            }
            guidedInfoList.add(guideInfoDto);
        }
        return guidedInfoList;
    }

    private List<ListingForExportToJson.Feature> getFeatures(Listing listing) {
        var beds = listing.getMainInformation().getBeds();
        var baths = listing.getMainInformation().getBaths();
        var sqft = listing.getMainInformation().getSqft();
        var propertyType = listing.getPropertyInformation().getType();
        var purpose = listing.getPropertyInformation().getPurpose();

        var features = new ArrayList<ListingForExportToJson.Feature>();

        features.add(createFeature("beds", beds));
        features.add(createFeature("baths", baths));
        features.add(createFeature("sqft", sqft));
        features.add(createFeature("propertyType", propertyType));
        features.add(createFeature("purpose", purpose));

        return features;
    }

    private ListingForExportToJson.Feature createFeature(String name, String value) {
        return ListingForExportToJson.Feature.builder()
                .name(name)
                .value(value)
                .type("String").build();
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
        var agentUrl = selectFirstAttribute(document, Selectors.Agency.AGENT_URL, "href");

        return AgencyCard.builder()
                .agentUrl(agentUrl != null ? String.format(ELEMENT_URL_FORMAT, agentUrl) : null)
                .agencyName(selectFirstString(document, Selectors.Agency.AGENCY_NAME))
                .agencyImage(selectAgencyName(document))
                .registrationNumber(selectFirstString(document, Selectors.Agency.REGISTRATION_NUMBER))
                .agentName(selectFirstString(document, Selectors.Agency.AGENT_NAME))
                .agentPhoto(selectFirstAttribute(document, Selectors.Agency.AGENT_PHOTO, "src"))
                .build();
    }

    private PropertyInformation fillPropertyInformation(Document document) {
        final String hasAverageRent = selectFirstString(document, Selectors.PropertyInfo.HAS_AVERAGE_RENT);
        return PropertyInformation.builder()
                .type(selectFirstString(document, Selectors.PropertyInfo.TYPE))
                .purpose(selectFirstString(document, Selectors.PropertyInfo.PURPOSE))
                .reference(selectFirstString(document, Selectors.PropertyInfo.REFERENCE))
                .furnishing(selectFirstString(document, Selectors.PropertyInfo.FURNISHING))
                .datePosted(selectFirstString(document, Selectors.PropertyInfo.DATE_POSTED))
                .completion(selectFirstString(document, Selectors.PropertyInfo.COMPLETION))
                .hasAverageRent(!ObjectUtils.isEmpty(hasAverageRent) && !hasAverageRent.equals("Not available"))
                .averageRent(selectFirstString(document, Selectors.PropertyInfo.AVERAGE_RENT))
                .averageRentFrequency(selectFirstString(document, Selectors.PropertyInfo.AVERAGE_RENT_FREQ))
                .trueCheck(selectFirstString(document, Selectors.PropertyInfo.TRUE_CHECK))
                .build();

    }

    private String selectAgencyName(Document document) {
        Elements elements = document.select(Selectors.Agency.AGENCY_IMAGE);
        if (elements == null) {
            return null;
        }
        if (elements.size() <= 1) {
            return elements.last().attr("data-src");
        }
        return elements.get(1).attr("data-src");
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
