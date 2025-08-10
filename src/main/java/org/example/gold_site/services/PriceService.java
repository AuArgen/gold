package org.example.gold_site.services;

import org.example.gold_site.models.Price;
import org.example.gold_site.modify.PriceModify;
import org.example.gold_site.repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public List<Price> getPrices() {
        return priceRepository.findAllByOrderByPublishedAtDescGoldAsc();
    }

    public void save(Price price) {

        if (priceRepository.findByPublishedAtAndGold(price.getPublishedAt(), price.getGold()).isPresent())
            return;
        price.setCreatedAt(LocalDateTime.now());
        price.setUpdatedAt(LocalDateTime.now());
        priceRepository.save(price);
    }


    public List<PriceModify> getPricesModifies() {
        List<Price> prices = getPrices();
        return IntStream.range(0, prices.size() - 6)
                .mapToObj(i -> {
                    PriceModify pm = new PriceModify(prices.get(i));
                    long diffBuy = prices.get(i).getPriceBuy() - prices.get(i + 6).getPriceBuy();
                    long diffSale = prices.get(i).getPriceSale() - prices.get(i + 6).getPriceSale();
                    pm.setDifferenceBuy(diffBuy / 10000.0);
                    pm.setDifferenceSale(diffSale / 10000.0);
                    return pm;
                })
                .collect(Collectors.toList());
    }
}
