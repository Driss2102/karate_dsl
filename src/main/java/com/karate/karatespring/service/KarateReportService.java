package com.karate.karatespring.service;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;

@Service
public class KarateReportService {

    private static final String REPORT_PATH = "target/karate-reports/karate-summary.html";
    private static final String OUTPUT_PDF_PATH = "target/karate-reports/karate-summary.pdf";

    public File convertHtmlToPdf() throws Exception {
        File htmlFile = new File(REPORT_PATH);
        File pdfFile = new File(OUTPUT_PDF_PATH);

        if (!htmlFile.exists()) {
            throw new FileNotFoundException("HTML report not found: " + REPORT_PATH);
        }

        String htmlContent = new String(java.nio.file.Files.readAllBytes(htmlFile.toPath()));

        try (OutputStream os = new FileOutputStream(pdfFile)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(os);
        }

        return pdfFile;
    }
}
