package pl.jdev.opes_bot.service.calculator;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jdev.opes_commons.domain.instrument.Candlestick;
import pl.jdev.opes_commons.domain.instrument.CandlestickData;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Exponential Moving Average Calculator
 */
@Component(value = "emaCalculator")
@Log4j2
public class EMACalculator implements Calculator {

    @Autowired
    SMACalculator smaCalculator;

    @Override
    public Map<String, Double> calculate(final Collection<Candlestick> candles) {
        return null;
    }

    public Map<String, Double> calculate(final Collection<Candlestick> candles, final int amtOfIndics, final int amtOfPeriods) {
        Objects.requireNonNull(candles, "'candles' cannot be null!");
        if (amtOfIndics < 1) throw new IllegalArgumentException("'amtOfIndics' must be greater than 0!");
        if (amtOfPeriods < 1) throw new IllegalArgumentException("'amtOfPeriods' must be greater than 0!");
        if (candles.size() < amtOfIndics + amtOfPeriods - 1)
            throw new IllegalArgumentException(String.format("Not enough candles to calculate %d indicators based on %d time periods!"));
//        log.traceEntry(format("Calculating %d EMAs with %d time periods from %s", amtOfIndics, amtOfPeriods, candles.toString()));
        Map<String, CandlestickData> candleMap = CandlesticksUtils.strip(candles);
        LinkedMap<String, CandlestickData> orderedCandles = CandlesticksUtils.orderByNatural(candleMap);
        Map<String, CandlestickData> initEMACandles = orderedCandles.clone();
        for (int i = amtOfPeriods; i < orderedCandles.size(); i++) {
            initEMACandles.remove(i);
        }
        double initEMA = smaCalculator.performCalculation(initEMACandles);

        double multiplier = 2.0 / (amtOfPeriods + 1);

        LinkedMap<String, Double> emas = new LinkedMap<>();
        orderedCandles.keySet()
                .stream()
                .skip(amtOfPeriods)
                .forEach(key -> emas.put(key, null));
        emas.replace(orderedCandles.get(amtOfPeriods), initEMA);

        for (int i = 1; i < emas.size(); i++) {
            double prevEMA = emas.getValue(i - 1);
            String key = emas.get(i);
            prevEMA = calculateFormula(prevEMA,
                    orderedCandles.get(key).getC(),
                    multiplier);
            emas.replace(key, prevEMA);
        }
        return emas;
    }

    double calculateFormula(double prevEMA, double close, double multiplier) {
        double ema = (close - prevEMA) * multiplier + prevEMA;
        return Precision.round(ema, 6);
    }
}