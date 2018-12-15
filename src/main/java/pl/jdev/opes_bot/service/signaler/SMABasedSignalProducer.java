package pl.jdev.opes_bot.service.signaler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jdev.opes_bot.service.calculator.SMACalculator;
import pl.jdev.opes_commons.rest.HttpHeaders;
import pl.jdev.opes_commons.rest.IntegrationClient;
import pl.jdev.opes_commons.rest.message.event.Event;

import static pl.jdev.opes_commons.rest.HttpHeaders.ACTION_TYPE;

@Service
public class SMABasedSignalProducer implements PricingBasedProducer {
    @Autowired
    private IntegrationClient integrationClient;
    @Autowired
    private SMACalculator calculator;

    @Override
    public void trigger() {
//        PricingEvent pricingEvent = (PricingEvent) event;
//        ConcurrentHashMap<String, Double> instrumentPriceMap = (ConcurrentHashMap<String, Double>) pricingEvent.getInstrumentPrice();
//        instrumentPriceMap.forEach((instrument, price) -> );
//        send(new IndicatorEvent("bleep"));
    }

    @Override
    public void send(Event event) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACTION_TYPE, "createOrder");
        integrationClient.postEvent(event, headers);
    }
}
