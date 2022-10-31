package kz.market.parser.repository;

import kz.market.parser.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}
