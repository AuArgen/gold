package org.example.gold_site.parses;

import org.example.gold_site.models.Gold;
import org.example.gold_site.models.Price;
import org.example.gold_site.services.GoldService;
import org.example.gold_site.services.PriceService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NbkrParse {

    private static final Logger log = LoggerFactory.getLogger(NbkrParse.class);

    private static final String NBKR_URL = "https://www.nbkr.kg/printver.jsp?item=2746&lang=KGZ";
    private static final int START_INDEX = 4; // сколько ячеек пропустить
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    private GoldService goldService;

    @Autowired
    private PriceService priceService;

    public List<Price> parseGold() throws IOException {
        Document doc = Jsoup.connect(NBKR_URL).get();
        Elements cells = doc.select("tbody td");

        log.info("Начало парсинга цен на золото с NBKR");

        List<Price> prices = new ArrayList<>();
        List<Gold> golds = goldService.getGolds();

        int count = 0;
        Optional<Gold> currentGold = Optional.empty();
        LocalDateTime publishedAt = LocalDateTime.now();
        Long priceBuy = 0L;

        for (Element cell : cells) {
            count++;
            if (count <= START_INDEX) continue; // пропускаем ненужные ячейки

            String text = cell.text().trim();
            log.debug("Ячейка [{}]: {}", count, text);

            switch (count % 4) {
                case 1 -> { // дата
                    publishedAt = LocalDateTime.parse(text + " 00:00", DATE_FORMATTER);
                }
                case 2 -> { // название золота
                    currentGold = golds.stream()
                            .filter(g -> g.getTitle().equals(text))
                            .findFirst();
                    if (currentGold.isEmpty()) {
                        log.warn("Не найдено золото с названием: {}", text);
                    }
                }
                case 3 -> { // цена покупки
                    priceBuy = parsePrice(text);
                }
                case 0 -> { // цена продажи
                    Long priceSale = parsePrice(text);
                    if (currentGold.isPresent()) {
                        Price price = new Price();
                        price.setGold(currentGold.get());
                        price.setPublishedAt(publishedAt);
                        price.setPriceBuy(priceBuy);
                        price.setPriceSale(priceSale);
                        priceService.save(price);
                        prices.add(price);
                        log.info("Сохранена цена: {} | Покупка: {} | Продажа: {}",
                                currentGold.get().getTitle(), priceBuy, priceSale);
                    }
                }
            }
        }

        log.info("Парсинг завершён. Найдено {} цен.", prices.size());
        return prices;
    }

    private Long parsePrice(String text) {
        try {
            return (long) (Double.parseDouble(text.replaceAll(" ", "")) * 10000);
        } catch (NumberFormatException e) {
            log.error("Ошибка парсинга цены: {}", text, e);
            return 0L;
        }
    }
}
