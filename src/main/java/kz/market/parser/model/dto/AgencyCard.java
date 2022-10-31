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
public class AgencyCard {
    private String agentUrl;
    private String agencyName;
    private String agencyImage;
    private String registrationNumber;
    private String agentName;
    private String agentPhoto;
}
