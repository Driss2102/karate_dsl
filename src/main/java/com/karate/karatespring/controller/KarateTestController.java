package com.karate.karatespring.controller;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.karate.karatespring.config.CustomExtentReport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tests")
public class KarateTestController {

    @GetMapping("/run")
    public String runKarateTests() {
        Results results = Runner.path("src/test/resources/karate/test.feature").parallel(1);

        return String.format(
                "Scénarios Karate exécutés : %d réussis, %d échoués",
                results.getScenariosPassed(), results.getScenariosFailed()
        );
    }

    @GetMapping("/runTestWithParams")
    public String runKarateTest(@RequestParam("username") String username,@RequestParam("password") String password,@RequestParam("expiresInMins") int expiresInMins) {


        System.out.println(" Requête reçue avec username: " + username + ", password: " + password + ", expiresInMins: " + expiresInMins);

        Results results = Runner.builder()
                .path("src/test/resources/karate/testadd.feature")
                .systemProperty("username", username)
                .systemProperty("password", password)
                .systemProperty("expiresInMins", String.valueOf(expiresInMins))
                .reportDir("C:/Users/Lenovo/Desktop/karate-reports")
                .outputCucumberJson(false)
                .outputHtmlReport(true)
                .outputJunitXml(false)

                .parallel(1);


        return String.format(
                "Scénarios Karate exécutés : %d réussis, %d échoués",
                results.getScenariosPassed(), results.getScenariosFailed()
        );
    }

    @GetMapping("/runCsv")
    public String runKarateTestscsv() {
        Results results = Runner.path("src/test/resources/karate/testcsv.feature").parallel(1);

        return String.format(
                "Scénarios Karate exécutés : %d réussis, %d échoués",
                results.getScenariosPassed(), results.getScenariosFailed()
        );
    }

    @GetMapping("/runchoose")
    public String runChoose(@RequestParam(required = false, defaultValue = "") String tags) {
        List<String> tagList = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());


        System.out.println("Tags traités : " + tagList);


        String karateTags =  "@" + String.join(",@", tagList);
        System.out.println("Tags utilisés pour Karate : " + karateTags);

        Results results = Runner.path("src/test/resources/karate/test.feature")
                .tags(karateTags)
                .parallel(1);

        return String.format(
                "Scénarios Karate exécutés : %d réussis, %d échoués",
                results.getScenariosPassed(), results.getScenariosFailed()
        );
    }

    @GetMapping("/runReport")
    public String runKarateTestWithrep() {
        String reportDir = "target/extended-reports";

        Results results = Runner.path("src/test/resources/karate/test.feature")
                .outputCucumberJson(true)
                .parallel(4);

        //pour la partie du Extent Report
        CustomExtentReport extentReport = new CustomExtentReport()
                .withKarateResult(results)
                .withReportDir(reportDir)
                .withReportTitle("Karate Test Execution Report");
        extentReport.generateExtentReport();


        return String.format(
                "Scénarios Karate exécutés : %d réussis, %d échoués. Rapport généré dans target/karate-reports/ExtentReport.html",
                results.getScenariosPassed(), results.getScenariosFailed()
        );
    }

    @GetMapping("/runGraphQLTest")
    public String runGraphQLTest() {
        Results results = Runner.path("src/test/resources/karate/testGraphQL.feature").parallel(1);
        return String.format(
                "Scénarios Karate exécutés : %d réussis, %d échoués",
                results.getScenariosPassed(), results.getScenariosFailed()
        );
    }

}







