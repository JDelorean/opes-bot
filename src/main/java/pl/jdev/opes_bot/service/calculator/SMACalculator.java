package pl.jdev.opes_bot.service.calculator;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;
import pl.jdev.opes_commons.domain.instrument.Candlestick;
import pl.jdev.opes_commons.domain.instrument.CandlestickData;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Simple Moving Average Calculator
 */
@Component(value = "smaCalculator")
@Log4j2
public class SMACalculator implements Calculator {

    /**
     * Will calculate and return the SMA date and SMA value for the last period of the provided candlesticks using the formula:
     * (closePriceOfCandle1 + closePriceOfCandle2 + ... + closePriceOfCandleN) / N
     *
     * @param candles Candlesticks to calculate the SMA based on.
     * @return Map with single entry of (key) date the SMA has been calculated for and (value) SMA for the last period of the provided candlesticks based on their number.
     */
    public Map<String, Double> calculate(final Collection<Candlestick> candles) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(candles, "'candles' cannot be null!");
        if (candles.size() == 0) {
            throw new IllegalArgumentException("'candles' cannot be empty!");
        }
        return calculate(candles, 1, candles.size());
    }

    /**
     * Will calculate and return list of SMAs value for the specified amount of latest periods of the provided candlesticks using the formula:
     * (closePriceOfCandle1 + closePriceOfCandle2 + ... + closePriceOfCandleN) / N
     *
     * @param candles
     * @param amtOfIndics
     * @param amtOfPeriods
     * @return
     */
    public Map<String, Double> calculate(final Collection<Candlestick> candles, final int amtOfIndics, final int amtOfPeriods) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(candles, "'candles' cannot be null!");
        if (amtOfIndics < 1) throw new IllegalArgumentException("'amtOfIndics' must be greater than 0!");
        if (amtOfPeriods < 1) throw new IllegalArgumentException("'amtOfPeriods' must be greater than 0!");
        log.traceEntry(format("Calculating %d SMAs with %d time periods from %s", amtOfIndics, amtOfPeriods, candles.toString()));
        Map<String, CandlestickData> candleMap = CandlesticksUtils.strip(candles);
        LinkedMap<String, CandlestickData> orderedCandles = CandlesticksUtils.orderByNatural(candleMap);
        Map<String, Double> smas = new LinkedMap<>();
        int lastIndex = orderedCandles.size() - 1;
        int i = amtOfIndics;
        while (i != 0) {
            log.trace(format("Processing candle: %s", orderedCandles.get(lastIndex)));
            LinkedMap<String, CandlestickData> perSmaCandles = orderedCandles
                    .keySet()
                    .stream()
                    .skip(lastIndex - amtOfPeriods + 1)
                    .peek(log::trace)
                    .collect(Collectors.toMap(k -> k,
                            candleMap::get,
                            (e1, e2) -> e1,
                            LinkedMap::new));
            Map.Entry<String, Double> processedEntry = Map.entry(orderedCandles.lastKey(), performCalculation(orderedCandles));
            smas.put(processedEntry.getKey(), processedEntry.getValue());
            log.trace(format("Processed entry: %s", processedEntry));
            orderedCandles.remove(lastIndex);
            i--;
            lastIndex--;
        }
        smas.forEach((key, value) -> log.traceExit(format("SMA for %s is %f", key, value)));
        return smas;
    }

    double performCalculation(final Map<String, CandlestickData> candleMap) {
        log.traceEntry(format("Calculating SMA from %s", candleMap.toString()));
        double candleCloseSum = candleMap.keySet()
                .stream()
                .peek(log::trace)
                .map(candleMap::get)
                .peek(log::trace)
                .mapToDouble(CandlestickData::getC)
                .sum();
        int amtOfCandles = candleMap.size();
        Double sma = candleCloseSum / amtOfCandles;
        log.traceExit(format("SMA: %f", sma));
        return Precision.round(sma, 6);
    }
}