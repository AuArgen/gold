package org.example.gold_site.repositories;

import org.example.gold_site.models.Gold;
import org.example.gold_site.models.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Optional<Price> findByPublishedAtAndGold(LocalDateTime publishedAt, Gold gold);

    List<Price> findAllByOrderByPublishedAtDesc();
    List<Price> findAllByOrderByPublishedAtDescGoldAsc();
}
