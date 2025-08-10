package org.example.gold_site.modify;

import org.example.gold_site.models.Price;

public class PriceModify extends Price {
    private Double differenceBuy;
    private Double differenceSale;

    public PriceModify(Price price) {
        this.setId(price.getId());
        this.setGold(price.getGold());
        this.setPriceBuy(price.getPriceBuy());
        this.setPriceSale(price.getPriceSale());
        this.setPublishedAt(price.getPublishedAt());
    }

    public Double getDifferenceBuy() {
        return differenceBuy;
    }

    public void setDifferenceBuy(Double differenceBuy) {
        this.differenceBuy = differenceBuy;
    }

    public Double getDifferenceSale() {
        return differenceSale;
    }

    public void setDifferenceSale(Double differenceSale) {
        this.differenceSale = differenceSale;
    }
}
