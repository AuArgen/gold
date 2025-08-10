package org.example.gold_site.services;

import org.example.gold_site.models.Gold;
import org.example.gold_site.repositories.GoldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoldService {

    @Autowired
    private GoldRepository goldRepository;

    public List<Gold> getGolds() {
        return goldRepository.findByOrderByWeight();
    }

    public void save(Gold gold) {
        if (gold.getCreatedAt() == null) {
            gold.setCreatedAt(LocalDateTime.now());
        }
        gold.setUpdatedAt(LocalDateTime.now());
        goldRepository.save(gold);
    }

    public Gold getById(Long id) {
        return goldRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        goldRepository.deleteById(id);
    }
}
