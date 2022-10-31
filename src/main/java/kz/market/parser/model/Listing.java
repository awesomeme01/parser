package kz.market.parser.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import kz.market.parser.model.dto.AgencyCard;
import kz.market.parser.model.dto.Features;
import kz.market.parser.model.dto.MainInformation;
import kz.market.parser.model.dto.OptionalData;
import kz.market.parser.model.dto.PropertyInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "listing")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreatedDate
    @Column(name = "date_created")
    private Instant dateCreated;

    @LastModifiedDate
    @Column(name = "date_updated", columnDefinition = "json")
    private Instant dateUpdated;

    @Column(name = "category")
    private String category;

    @Column(name = "url")
    private String url;

    @Type(type = "jsonb")
    @Column(name = "main_information", columnDefinition = "json")
    private MainInformation mainInformation;

    @Type(type = "jsonb")
    @Column(name = "agency_card", columnDefinition = "json")
    private AgencyCard agencyCard;

    @Type(type = "jsonb")
    @Column(name = "property_information", columnDefinition = "json")
    private PropertyInformation propertyInformation;

    @Type(type = "jsonb")
    @Column(name = "features", columnDefinition = "json")
    private Features features;

    @Type(type = "jsonb")
    @Column(name = "optional_data", columnDefinition = "json")
    private OptionalData optionalData;

}
