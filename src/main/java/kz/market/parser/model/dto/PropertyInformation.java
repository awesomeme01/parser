package kz.market.parser.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyInformation {
    private String type;
    private String purpose;
    private String reference;
    private String furnishing;
    private String datePosted;
    private String completion;
    private String averageRent;
    private String trueCheck;
}
