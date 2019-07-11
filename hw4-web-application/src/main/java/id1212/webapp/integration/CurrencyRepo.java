package id1212.webapp.integration;

import id1212.webapp.models.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrencyRepo extends CrudRepository<Currency, Long> {
    Currency findByCurrencyCode(@Param("currencyCode") String currencyCode);
    List<Currency> findAllByOrderByCurrencyCodeAsc();
}
