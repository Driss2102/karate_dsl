package com.karate.karatespring.controller;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.karate.karatespring.config.CustomExtentReport;
import de.siegmar.fastcsv.reader.CsvReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@RequestMapping("/api/tests")
public class KarateTestController {


//    @Scheduled(fixedRate = 120000)
@GetMapping("/run")
public ResponseEntity<InputStreamResource> runTestsAndDownloadReport() throws IOException {
    // 📌 1️⃣ Exécuter les tests Karate
    Results results = Runner.path("src/test/resources/karate/test.feature").parallel(1);

    // 📌 2️⃣ Dossier du rapport Karate
    String reportDir = "target/karate-reports";
    String zipFilePath = "target/karate-reports.zip";

    // 📌 3️⃣ Compresser le rapport
    zipDirectory(reportDir, zipFilePath);

    // 📌 4️⃣ Envoyer le fichier ZIP en téléchargement
    File zipFile = new File(zipFilePath);
    InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));

    uploadReportToS3(zipFilePath);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=karate-reports.zip")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(zipFile.length())
            .body(resource);
}

    private void zipDirectory(String sourceDirPath, String zipFilePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(fos);
        Path sourcePath = Paths.get(sourceDirPath);

        Files.walk(sourcePath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                    try {
                        zos.putNextEntry(zipEntry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        zos.close();
        fos.close();
    }




    public void uploadReportToS3(String zipFilePath) {
//         definir les info perso du bucjet s3
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                "AKIA3EDGJN6M6DYKXKT6",
                "5VdVkmKqSynR+f44OI6GWWa1ULJReX/91b2tekr3"
        );

        S3Client s3 = S3Client.builder()
                .region(Region.of("eu-north-1")).credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();


        Path path = Paths.get(zipFilePath);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket("test-api-report-driss").key("karate-report.zip").build();
        PutObjectResponse response = s3.putObject(putObjectRequest, path);
        System.out.println("File uploaded to S3: " + response);
    }



    public void uploadKarateReportFolderToS3(String reportFolderPath) {
        // Set up your AWS credentials and region
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                "AKIA3EDGJN6M6DYKXKT6", // Replace with your actual access key
                "5VdVkmKqSynR+f44OI6GWWa1ULJReX/91b2tekr3" // Replace with your actual secret key
        );
        S3Client s3 = S3Client.builder()
                .region(Region.of("eu-north-1")) // Replace with your region
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        // Prepare the folder and list all files inside it (including subfolders)
        File folder = new File(reportFolderPath);
        List<File> files = new ArrayList<>();
        listFilesRecursively(folder, files);

        // Upload each file to S3 while preserving the folder structure
        for (File file : files) {
            // Construct the S3 path (key) for each file, preserving the folder structure
            String key = getRelativePath(folder, file); // Get relative path to preserve folder structure

            // Create the upload request
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("test-api-report-driss") // Replace with your bucket name
                    .key(key) // This will be the S3 path (folder structure)
                    .build();

            // Upload the file to S3
            PutObjectResponse response = s3.putObject(putObjectRequest, file.toPath());
            System.out.println("Uploaded file to S3: " + file.getName() + " with key: " + key);
        }
    }

    // Helper method to list all files in the folder, even in subfolders
    private void listFilesRecursively(File folder, List<File> files) {
        File[] fileList = folder.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    listFilesRecursively(file, files); // Recurse into subdirectories
                } else {
                    files.add(file); // Add the file to the list
                }
            }
        }
    }

    // Helper method to get the relative path of a file to preserve the folder structure
    private String getRelativePath(File baseFolder, File file) {
        String basePath = baseFolder.getPath();
        String filePath = file.getPath();

        // Create the relative path, keeping the structure starting from the Karate report folder
        return filePath.substring(basePath.length() + 1).replace("\\", "/");
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

    @Scheduled(cron = "0 21 9 * * *")
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

    @GetMapping("/runreport")
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
        String reportDir = "target/extended-reports";
        //pour la partie du Extent Report
        CustomExtentReport extentReport = new CustomExtentReport()
                .withKarateResult(results)
                .withReportDir(reportDir)
                .withReportTitle("Karate Test Execution Report");
        extentReport.generateExtentReport();


        return String.format(
                "Scénarios Karate exécutés : %d réussis, %d échoués",
                results.getScenariosPassed(), results.getScenariosFailed()
        );
    }









}







