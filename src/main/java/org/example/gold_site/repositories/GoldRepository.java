package org.example.gold_site.repositories;

import org.example.gold_site.models.Gold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoldRepository extends JpaRepository<Gold, Long> {
    List<Gold> findByOrderByWeight();
}
