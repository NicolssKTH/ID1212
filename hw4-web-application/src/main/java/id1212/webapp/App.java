package id1212.webapp;

import id1212.webapp.integration.CurrencyRepo;
import id1212.webapp.models.Currency;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class App implements CommandLineRunner {

    private final CurrencyRepo currencyRepo;

    @Autowired
    public App(CurrencyRepo currencyRepo){
        this.currencyRepo = currencyRepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        insertCurrencyData();
    }

    private void insertCurrencyData()throws IOException{
        currencyRepo.deleteAll();
        String content = new String(Files.readAllBytes(Paths.get("/Users/nicla/Desktop/hw4-web-application/src/main/resources/static/json/rate_data.json")));
        JSONObject json = new JSONObject(content);
        JSONObject rates = json.getJSONObject("rates");
        for (String key : rates.keySet()){
            currencyRepo.save(new Currency(key, rates.getDouble(key)));
        }
    }
}
