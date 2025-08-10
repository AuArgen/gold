package org.example.gold_site.controller;

import org.example.gold_site.repositories.PriceRepository;
import org.example.gold_site.services.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private PriceService priceService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("prices", priceService.getPricesModifies());
        model.addAttribute("content", "index");
        return "layout/base";
    }

}
