package pl.jdev.opes_bot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jdev.opes_bot.service.calculator.EMACalculator;
import pl.jdev.opes_bot.service.calculator.SMACalculator;
import pl.jdev.opes_commons.rest.message.request.MARequest;

import java.util.Map;

@RestController
@RequestMapping("/indicator")
@Log4j2
public class IndicatorController {

    @Autowired
    SMACalculator smaCalculator;
    @Autowired
    EMACalculator emaCalculator;


    @PostMapping({"/sma", "/ema"})
    @ResponseBody
    public Map<String, Double> calculateMA(@RequestHeader("Data-Type") String dataType, @RequestBody MARequest maRequest) {
        if (dataType.equals("sma")) {
            return smaCalculator.calculate(maRequest.getCandles(),
                    maRequest.getAmtOfIndics(),
                    maRequest.getAmtOfPeriods());
        } else if (dataType.equals("ema")) {
            return emaCalculator.calculate(maRequest.getCandles(),
                    maRequest.getAmtOfIndics(),
                    maRequest.getAmtOfPeriods());
        }
        return null;
    }
}
