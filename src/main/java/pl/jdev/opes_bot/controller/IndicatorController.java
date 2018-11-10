package pl.jdev.opes_bot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jdev.opes_bot.service.calculator.SMACalculator;
import pl.jdev.opes_commons.rest.message.SMARequest;

import java.util.Map;

@RestController
@RequestMapping("/indicator")
@Log4j2
public class IndicatorController {

    @Autowired
    SMACalculator smaCalculator;

    @PostMapping(value = "/sma")
    @ResponseBody
    public Map<String, Double> calculateSMA(@RequestBody SMARequest smaRequest) {
        return smaCalculator.calculate(smaRequest.getCandles(),
                smaRequest.getNumOfSMAs(),
                smaRequest.getNumOfTimePeriods());
    }

//    @PostMapping(value = "/ema")
//    @ResponseBody
//    public ResponseEntity calculateEMA(@RequestBody EMARequest emaRequest) {
//        return null;
//    }
}
