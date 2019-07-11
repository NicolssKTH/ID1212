package id1212.webapp.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private double currencyRate;

    private String currencyCode;

    public Currency() {
    }


    public Currency(String code, double rate) {
        this.currencyCode = code;
        this.currencyRate = rate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String code) {
        this.currencyCode = code;
    }

    public double getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(double rate) {
        this.currencyRate = rate;
    }
}
