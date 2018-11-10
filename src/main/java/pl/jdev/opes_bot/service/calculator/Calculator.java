package pl.jdev.opes_bot.service.calculator;

import pl.jdev.opes_commons.domain.instrument.Candlestick;

import java.util.Collection;
import java.util.Map;

public interface Calculator {
    public Map<String, Double> calculate(Collection<Candlestick> candlesticks);
}
