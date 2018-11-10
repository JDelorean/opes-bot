package pl.jdev.opes_bot.service.calculator;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jdev.opes_commons.domain.instrument.Candlestick;
import pl.jdev.opes_commons.domain.instrument.CandlestickData;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Simple Moving Average Calculator
 */
@Component(value = "smaCalculator")
@Log4j2
public class SMACalculator implements Calculator {

    @Autowired
    DateFormat dateFormat;

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
//        log.traceEntry(format("Calculating SMA from %s", candles.toString()));
//        log.info(String.format("Calculating SMA based on: %s", candles));
//        try {
//            this.validate(candles);
//        } catch (Exception e) {
//            log.warning(e.getMessage());
//            throw e;
//        }
//        Map<String, CandlestickData> candleMap = strip(candles);
//        LinkedMap<String, CandlestickData> orderedCandles = this.orderByNatural(candleMap);
//        String latestPeriod = orderedCandles.lastKey();
//        double sma = this.performCalculation(orderedCandles);
//        log.traceExit(format("SMA for %s is %f", latestPeriod, sma));
        return calculate(candles, 1, candles.size());
    }

    /**
     * Will calculate and return list of SMAs value for the specified amount of latest periods of the provided candlesticks using the formula:
     * (closePriceOfCandle1 + closePriceOfCandle2 + ... + closePriceOfCandleN) / N
     *
     * @param candles
     * @param numOfSMAs
     * @param numOfTimePeriods
     * @return
     */
    public Map<String, Double> calculate(final Collection<Candlestick> candles, final int numOfSMAs, final int numOfTimePeriods) throws IllegalArgumentException, NullPointerException {
        Objects.requireNonNull(candles, "'candles' cannot be null!");

        Objects.requireNonNull(numOfSMAs, "'numOfSMAs' cannot be null!");
        Objects.requireNonNull(numOfTimePeriods, "'numOfTimePeriods' cannot be null!");
        log.traceEntry(format("Calculating %d SMAs with %d time periods from %s", numOfSMAs, numOfTimePeriods, candles.toString()));
        Map<String, CandlestickData> candleMap = strip(candles);
        LinkedMap<String, CandlestickData> orderedCandles = this.orderByNatural(candleMap);
        Map<String, Double> smas = new LinkedMap<>();
        int lastIndex = orderedCandles.size() - 1;
        int i = numOfSMAs;
        while (i != 0) {
            log.trace(format("Processing candle: %s", orderedCandles.get(lastIndex)));
            LinkedMap<String, CandlestickData> perSmaCandles = orderedCandles
                    .keySet()
                    .stream()
                    .skip(lastIndex - numOfTimePeriods + 1)
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

    //TODO: make this work
    private void validate(final Collection<Candlestick> candles) throws IllegalArgumentException {
        Collection<Optional<CandlestickData>> asks = candles.stream()
                .map(candlestick -> Optional.ofNullable(candlestick.getAsk()))
                .collect(Collectors.toList());
        Collection<Optional<CandlestickData>> bids = candles.stream()
                .map(candlestick -> Optional.ofNullable(candlestick.getBid()))
                .collect(Collectors.toList());
        Collection<Optional<CandlestickData>> mids = candles.stream()
                .map(candlestick -> Optional.ofNullable(candlestick.getMid()))
                .collect(Collectors.toList());
        boolean validAsks = asks.stream().allMatch(Optional::isPresent);
        boolean validBids = bids.stream().allMatch(Optional::isPresent);
        boolean validMids = mids.stream().allMatch(Optional::isPresent);
        if (!(validAsks && validBids && validMids)) {
            throw new IllegalArgumentException(format("Candles validation failed: %s", candles));
        }
    }

    private Map<String, CandlestickData> strip(final Collection<Candlestick> candles) {
        log.traceEntry(format("Stripping candlesticks %s", candles));
        Map<String, CandlestickData> stripped = candles.stream()
                .map(candlestick -> Map.entry(candlestick.getTime(),
                        Optional.ofNullable(candlestick.getAsk())
                                .orElse(Optional.ofNullable(candlestick.getBid())
                                        .orElse(Optional.ofNullable(candlestick.getMid())
                                                .get()))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        log.traceExit(format("Stripped to: %s", stripped));
        return stripped;
    }


    private LinkedMap<String, CandlestickData> orderByNatural(final Map<String, CandlestickData> candleMap) {
        log.traceEntry(format("Ordering: %s", candleMap.toString()));
        LinkedMap<String, CandlestickData> orderedCandles = candleMap.keySet()
                .stream()
                .sorted((strDate1, strDate2) -> {
                    int comp = 0;
                    try {
                        comp = dateFormat.parse(strDate1).compareTo(dateFormat.parse(strDate2));
                    } catch (ParseException e) {
                        log.error(e.getMessage());
                    }
                    return comp;
                })
                .collect(Collectors.toMap(t -> t,
                        candleMap::get,
                        (e1, e2) -> e1,
                        LinkedMap::new));
        log.traceExit(format("Ordered: %s", orderedCandles));
        return orderedCandles;
    }

    private double performCalculation(final Map<String, CandlestickData> candleMap) {
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