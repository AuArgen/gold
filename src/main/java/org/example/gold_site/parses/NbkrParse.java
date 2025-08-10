package org.example.gold_site.parses;

import org.example.gold_site.models.Gold;
import org.example.gold_site.models.Price;
import org.example.gold_site.repositories.PriceRepository;
import org.example.gold_site.services.GoldService;
import org.example.gold_site.services.PriceService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Component
public class NbkrParse {
//    https://www.nbkr.kg/printver.jsp?item=2746&lang=KGZ
    @Autowired
    private GoldService goldService;

    @Autowired
    private PriceService PriceService;

    public List<Price> parseGold() throws IOException {
        Document doc = Jsoup.connect("https://www.nbkr.kg/printver.jsp?item=2746&lang=KGZ").get();
        Elements sourceQuotes = doc.select("tbody td");
        System.out.println("************* START ***********");
        List<Price> prices = new ArrayList<>();
        List<Gold> golds = goldService.getGolds();
        int start = 4;
        int count = 0;
        Optional<Gold> gold = Optional.of(new Gold());
        LocalDateTime date_published = LocalDateTime.now();
        Long price_sale = 0L;
        Long price_buy = 0L;
        for (Element quoteElement : sourceQuotes) {
            count++;
            if (count > start) {
                String text = quoteElement.text();
                System.out.println("*********** " + text);
                switch (count % 4) {
                    case 0:
                        if (gold.isEmpty())
                            continue;
                        price_sale = (long) (Double.parseDouble(quoteElement.text().replaceAll(" ", "")) * 10000);
                        Price price = new Price();
                        price.setPriceBuy(price_buy);
                        price.setPriceSale(price_sale);
                        price.setPublishedAt(date_published);
                        price.setGold(gold.get());
                        PriceService.save(price);
                        prices.add(price);
                        break;
                    case 1:
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                        date_published =  LocalDateTime.parse(quoteElement.text()+" 00:00", formatter);
                        break;
                    case 2:

                        gold = golds.stream().filter(u -> quoteElement.text().equals(u.getTitle())).findFirst();
                        break;
                    case 3:
                        price_buy = (long) Double.parseDouble(quoteElement.text().replaceAll(" ", "")) * 10000;
                        break;
                }
            }
        }
        return prices;
    }
}
