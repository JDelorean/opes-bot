package pl.jdev.opes_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jdev.opes.domain.instrument.Candlestick;

import java.text.DateFormat;
import java.util.Collection;

/**
 * Exponential Moving Average Calculator
 */
@Component(value = "emaCalculator")
public class EMACalculator {

    @Autowired
    DateFormat dateFormat;

    public double calculate(Collection<Candlestick> candles) {


        return 0;
    }
}