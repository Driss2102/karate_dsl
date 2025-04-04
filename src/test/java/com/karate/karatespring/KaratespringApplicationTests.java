package com.karate.karatespring;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KaratespringApplicationTests {

    @Test
    void contextLoads() {
        // This is your existing test method
    }

    @Karate.Test
    Karate runKarateTest() {
        return Karate.run("classpath:karate/").relativeTo(getClass());
    }
}