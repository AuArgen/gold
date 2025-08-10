package org.example.gold_site.controller;

import org.example.gold_site.models.Gold;
import org.example.gold_site.parses.NbkrParse;
import org.example.gold_site.services.GoldService;
import org.example.gold_site.services.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private GoldService goldService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private NbkrParse nbkrParse;

    @GetMapping
    public String admin(Model model) {

        model.addAttribute("content", "admin/index");
        return "layout/admin/base";
    }

    @GetMapping("/gold")
    public String gold(Model model, Long id) {
        Gold gold = new Gold();
        if (id != null) {
            gold = goldService.getById(id);
        }
        model.addAttribute("gold", gold);
        model.addAttribute("golds", goldService.getGolds());
        model.addAttribute("content", "admin/gold");
        return "layout/admin/base";
    }

    @PostMapping("/gold")
    public String goldAdd(Gold gold,
                          RedirectAttributes redirectAttributes) {
        try {
            goldService.save(gold);
            redirectAttributes.addFlashAttribute("message", "gold saved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/admin/gold";
    }

    @GetMapping("/gold/delete/{id}")
    public String goldDelete(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            goldService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "gold deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/admin/gold";
    }

    @GetMapping("/price")
    public String price(Model model) {
        model.addAttribute("prices", priceService.getPrices());
        model.addAttribute("content", "admin/price");
        return "layout/admin/base";
    }

    @GetMapping("/price/parsing")
    public String priceParsing(Model model) throws IOException {
        nbkrParse.parseGold();
        return "redirect:/admin/price";
    }
}
