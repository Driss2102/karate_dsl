package com.karate.karatespring;

import com.intuit.karate.junit5.Karate;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(AllureJunit5.class)
class KaratespringApplicationTests {

    @Test
    void contextLoads() {
        // This is your existing test method
    }

    @Karate.Test
    Karate runKarateTest() {
        return Karate.run("classpath:karate/test.feature").relativeTo(getClass());
    }
}