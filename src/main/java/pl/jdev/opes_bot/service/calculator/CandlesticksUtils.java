package pl.jdev.opes_bot.service.calculator;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import pl.jdev.opes_commons.domain.instrument.Candlestick;
import pl.jdev.opes_commons.domain.instrument.CandlestickData;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Log4j2
public class CandlesticksUtils {

    @Autowired
    static DateFormat dateFormat;

    //TODO: make this work
    public void validate(final Collection<Candlestick> candles) throws IllegalArgumentException {
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

    public static LinkedMap<String, CandlestickData> orderByNatural(final Map<String, CandlestickData> candleMap) {
        LinkedMap<String, CandlestickData> orderedCandles = candleMap.keySet()
                .stream()
                .peek(log::warn)
                .sorted()
//                .sorted((strDate1, strDate2) -> {
//                    int comp = 0;
//                    try {
////                        comp = dateFormat.parse(strDate1).compareTo(dateFormat.parse(strDate2));
//                        comp = ObjectUtils.compare(dateFormat.parse(strDate1), dateFormat.parse(strDate2));
//                    } catch (ParseException e) {
//                        //TODO: how to log this?
//                        log.error(e.getMessage());
//                    }
//                    return comp;
//                })
                .peek(log::trace)
                .collect(Collectors.toMap(t -> t,
                        candleMap::get,
                        (e1, e2) -> e1,
                        LinkedMap::new));
        return orderedCandles;
    }

    public static Map<String, CandlestickData> strip(final Collection<Candlestick> candles) {
        Map<String, CandlestickData> stripped = candles.stream()
                .map(candlestick -> Map.entry(candlestick.getTime(),
                        Optional.ofNullable(candlestick.getAsk())
                                .orElse(Optional.ofNullable(candlestick.getBid())
                                        .orElse(Optional.ofNullable(candlestick.getMid())
                                                .get()))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return stripped;
    }
}
