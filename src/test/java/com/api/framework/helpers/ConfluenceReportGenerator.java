package com.api.framework.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfluenceReportGenerator {

    private static final String TARGET_DIR = "target/karate-reports";
    private static final String SUMMARY_FILE = "karate-summary-json.txt";
    private static final String OUTPUT_FILE = "target/confluence-report.docx";

    public static void main(String[] args) {
        System.out.println("Generating Confluence Word Report...");
        try {
            generateReport();
            System.out.println("Report generated successfully: " + OUTPUT_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void generateReport() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File summaryFile = new File(TARGET_DIR, SUMMARY_FILE);

        if (!summaryFile.exists()) {
            System.out.println("Summary file not found: " + summaryFile.getAbsolutePath());
            return;
        }

        JsonNode summaryNode = mapper.readTree(summaryFile);

        try (XWPFDocument document = new XWPFDocument()) {
            // Title
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Test Execution Report");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Metadata
            XWPFParagraph meta = document.createParagraph();
            XWPFRun metaRun = meta.createRun();
            metaRun.setText("Date: " + summaryNode.path("resultDate").asText());
            metaRun.addBreak();
            metaRun.setText(
                    "Total Time: " + String.format("%.2f", summaryNode.path("totalTime").asDouble() / 1000) + "s");

            // Summary Table
            XWPFParagraph summaryHeader = document.createParagraph();
            XWPFRun summaryHeaderRun = summaryHeader.createRun();
            summaryHeaderRun.setText("Summary");
            summaryHeaderRun.setBold(true);
            summaryHeaderRun.setFontSize(14);

            XWPFTable table = document.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            headerRow.getCell(0).setText("Feature");
            headerRow.addNewTableCell().setText("Scenarios");
            headerRow.addNewTableCell().setText("Passed");
            headerRow.addNewTableCell().setText("Failed");
            headerRow.addNewTableCell().setText("Duration (s)");

            JsonNode features = summaryNode.path("featureSummary");
            if (features.isArray()) {
                for (JsonNode feature : features) {
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(feature.path("name").asText());
                    row.getCell(1).setText(String.valueOf(feature.path("scenarioCount").asInt()));
                    row.getCell(2).setText(String.valueOf(feature.path("passedCount").asInt()));
                    row.getCell(3).setText(String.valueOf(feature.path("failedCount").asInt()));
                    row.getCell(4).setText(String.format("%.2f", feature.path("durationMillis").asDouble() / 1000));
                }
            }

            document.createParagraph().createRun().addBreak();

            // Detailed Results
            XWPFParagraph detailsHeader = document.createParagraph();
            XWPFRun detailsHeaderRun = detailsHeader.createRun();
            detailsHeaderRun.setText("Detailed Results");
            detailsHeaderRun.setBold(true);
            detailsHeaderRun.setFontSize(14);

            if (features.isArray()) {
                for (JsonNode feature : features) {
                    processFeatureDetail(mapper, feature, document);
                }
            }

            try (FileOutputStream out = new FileOutputStream(OUTPUT_FILE)) {
                document.write(out);
            }
        }
    }

    private static void processFeatureDetail(ObjectMapper mapper, JsonNode featureNode, XWPFDocument document)
            throws IOException {
        XWPFParagraph featureTitle = document.createParagraph();
        XWPFRun featureTitleRun = featureTitle.createRun();
        featureTitleRun.setText("Feature: " + featureNode.path("name").asText());
        featureTitleRun.setBold(true);
        featureTitleRun.setFontSize(12);

        String pkgName = featureNode.path("packageQualifiedName").asText();
        String jsonFileName = pkgName + ".karate-json.txt";
        File jsonFile = new File(TARGET_DIR, jsonFileName);

        if (jsonFile.exists()) {
            JsonNode featureDetail = mapper.readTree(jsonFile);
            JsonNode scenarios = featureDetail.path("scenarioResults");

            if (scenarios.isArray()) {
                for (JsonNode scenario : scenarios) {
                    String name = scenario.path("name").asText();
                    boolean failed = scenario.path("failed").asBoolean();
                    String status = failed ? "FAILED" : "PASSED";

                    XWPFParagraph scenarioPara = document.createParagraph();
                    XWPFRun scenarioRun = scenarioPara.createRun();
                    scenarioRun.setText("Scenario: " + name);
                    scenarioRun.setBold(true);
                    scenarioRun.addBreak();
                    scenarioRun.setText("Status: " + status);
                    if (failed) {
                        scenarioRun.setColor("FF0000");
                    } else {
                        scenarioRun.setColor("008000");
                    }
                    scenarioRun.addBreak();
                    scenarioRun.setText("Duration: " + scenario.path("durationMillis").asDouble() + "ms");

                    // Logs
                    JsonNode steps = scenario.path("stepResults");
                    if (steps.isArray()) {
                        for (JsonNode stepResult : steps) {
                            String stepLog = stepResult.path("stepLog").asText();
                            if (stepLog != null && !stepLog.isEmpty()
                                    && (stepLog.contains("request:") || stepLog.contains("response time"))) {
                                XWPFParagraph logPara = document.createParagraph();
                                logPara.setBorderLeft(Borders.SINGLE);
                                XWPFRun logRun = logPara.createRun();
                                logRun.setFontFamily("Courier New");
                                logRun.setFontSize(9);
                                logRun.setText(stepLog.trim());
                            }
                        }
                    }
                }
            }
        }
        document.createParagraph().createRun().addBreak();
    }
}
