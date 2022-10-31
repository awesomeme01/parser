package kz.market.parser.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MainInformation {
    private String name;
    private String price;
    private String interval;
    private List<String> imageUrls;
    private String beds;
    private String baths;
    private String sqft;
    private String description;
}
