package kz.market.parser.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kz.market.parser.model.dto.AgencyCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingForExportToJson {
    private String name;
    private String description;
    private String price;
    private String originUrl;
    private String address;
    private List<String> media;
    private String categoryName;
    private List<Feature> features;
    private AgencyCard agency;
    private List<GuideInformation> guideInformation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GuideInformation {
        private String guideLinkTitle;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Feature {
        private String name;
        private String value;
        private String type;
    }

}
