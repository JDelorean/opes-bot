package pl.jdev.opes_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jdev.opes_commons.domain.instrument.Candlestick;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Exponential Moving Average Calculator
 */
@Component(value = "emaCalculator")
public class EMACalculator implements Calculator {

    @Autowired
    DateFormat dateFormat;

    @Override
    public Map<String, Double> calculate(Collection<Candlestick> candlesticks) {
        return null;
    }
}