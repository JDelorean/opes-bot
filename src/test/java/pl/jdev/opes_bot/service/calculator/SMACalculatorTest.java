package pl.jdev.opes_bot.service.calculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.jdev.opes_commons.domain.instrument.Candlestick;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SMACalculatorTest {
    @Autowired
    private Calculator smaCalculator;

    static Stream<Arguments> invalidCollectionAndExpectedException() {
        return Stream.of(
                arguments(null, new NullPointerException("'candles' cannot be null!")),
                arguments(Collections.EMPTY_LIST, new IllegalArgumentException("'candles' cannot be empty!")));
    }

    @ParameterizedTest
    @MethodSource("invalidCollectionAndExpectedException")
    void calculate_InvalidCollection_ShouldThrowException(ArgumentsAccessor arguments) {
        //given
        Collection<Candlestick> candlesticks = (Collection<Candlestick>) arguments.get(0);
        Exception expectedException = (Exception) arguments.get(1);

        //when
        try {
            smaCalculator.calculate(candlesticks);
            fail("No exception thrown.");
        } catch (Exception e) {
            //then
            assertSame(e.getClass(), expectedException.getClass());
            assertEquals(e.getMessage(), expectedException.getMessage());
        }
    }

    @Test
    void calculate_SingleCandleCollection_ShouldCalculateSMA() {

    }

    @Test
    void calculate_MultipleCandles_ShouldCalculateSMA() {

    }
//
//    @Test
//    void calculate_NoCandles_
}
