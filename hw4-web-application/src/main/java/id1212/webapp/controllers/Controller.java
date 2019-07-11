package id1212.webapp.controllers;

import id1212.webapp.integration.CurrencyRepo;
import id1212.webapp.models.Conversion;
import id1212.webapp.models.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@org.springframework.stereotype.Controller
public class Controller {
    private final CurrencyRepo currencyRepo;

    private Iterable<Currency> currencies;
    private Conversion conversion;

    @Autowired
    public Controller(CurrencyRepo currencyRepo) {
        this.currencyRepo = currencyRepo;
        this.currencies = currencyRepo.findAllByOrderByCurrencyCodeAsc();
        this.conversion = new Conversion("EUR", "SEK", 0, 0);
    }

    @GetMapping(value = "/")
    public String converter(Model model) {
        model.addAttribute("currencies", currencies);
        if (!model.containsAttribute("conversion"))
            model.addAttribute("conversion", conversion);
        return "converter";
    }

    @PostMapping(value = "/convert")
    public String convert(@ModelAttribute Conversion conversion) {
        if (conversion.getFromCurrency().equals(conversion.getToCurrency())) {
            this.conversion = conversion;
            return "redirect:/";
        }

        this.conversion = new Conversion(conversion.getFromCurrency(), conversion.getToCurrency(), conversion.getAmount(), convertCurrencies(conversion));
        return "redirect:/";
    }

    @GetMapping(value = "/reset")
    public String reset() {
        this.conversion = new Conversion("EUR", "SEK", 0, 0);
        return "redirect:/";
    }

    private double convertCurrencies(Conversion conversion) {
        Currency fromCurrency = currencyRepo.findByCurrencyCode(conversion.getFromCurrency());
        Currency toCurrency = currencyRepo.findByCurrencyCode(conversion.getToCurrency());
        double inEuro = conversion.getAmount() * (1 / fromCurrency.getCurrencyRate());
        return (double) Math.round(inEuro * toCurrency.getCurrencyRate() * 100d) / 100d;
    }
}
