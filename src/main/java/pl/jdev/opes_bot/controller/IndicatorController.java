package pl.jdev.opes_bot.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jdev.opes_bot.service.SMACalculator;
import pl.jdev.opes_commons.rest.message.SMARequest;

@RestController
@RequestMapping("/indicator")
@Log4j2
public class IndicatorController {

    @Autowired
    SMACalculator smaCalculator;

    @PostMapping(value = "/sma")
    @ResponseBody
    public ResponseEntity calculateSMA(@RequestBody SMARequest smaRequest) {
        log.traceEntry("calculateSMA", smaRequest);


        return ResponseEntity.ok().body("A OK!");
    }

//    @PostMapping(value = "/ema")
//    @ResponseBody
//    public ResponseEntity calculateEMA(@RequestBody EMARequest emaRequest) {
//        return null;
//    }
}
