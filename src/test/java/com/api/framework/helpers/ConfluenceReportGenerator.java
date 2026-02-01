package com.api.framework.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfluenceReportGenerator {

    private static final String TARGET_DIR = "target/karate-reports";
    private static final String SUMMARY_FILE = "karate-summary-json.txt";
    private static final String OUTPUT_FILE = "target/confluence-report.md";

    public static void main(String[] args) {
        System.out.println("Generating Confluence Report...");
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
        StringBuilder md = new StringBuilder();

        md.append("# Test Execution Report\n\n");
        md.append("**Date**: ").append(summaryNode.path("resultDate").asText()).append("\n");
        md.append("**Total Time**: ").append(summaryNode.path("totalTime").asDouble() / 1000).append("s\n\n");

        md.append("## Summary\n");
        md.append("| Feature | Scenarios | Passed | Failed | Duration (s) |\n");
        md.append("| :--- | :--- | :--- | :--- | :--- |\n");

        JsonNode features = summaryNode.path("featureSummary");
        if (features.isArray()) {
            for (JsonNode feature : features) {
                String name = feature.path("name").asText();
                int scenarioCount = feature.path("scenarioCount").asInt();
                int passedCount = feature.path("passedCount").asInt();
                int failedCount = feature.path("failedCount").asInt();
                double duration = feature.path("durationMillis").asDouble() / 1000;

                md.append("| ").append(name).append(" | ")
                        .append(scenarioCount).append(" | ")
                        .append(passedCount).append(" | ")
                        .append(failedCount).append(" | ")
                        .append(String.format("%.2f", duration)).append(" |\n");
            }
        }

        md.append("\n---\n\n## Detailed Results\n");

        if (features.isArray()) {
            for (JsonNode feature : features) {
                String pkgName = feature.path("packageQualifiedName").asText();
                String jsonFileName = pkgName + ".karate-json.txt";
                File jsonFile = new File(TARGET_DIR, jsonFileName);

                if (jsonFile.exists()) {
                    processFeatureDetail(mapper.readTree(jsonFile), md);
                }
            }
        }

        try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            writer.write(md.toString());
        }
    }

    private static void processFeatureDetail(JsonNode featureNode, StringBuilder md) {
        md.append("### ").append(featureNode.path("name").asText()).append("\n\n");

        JsonNode scenarios = featureNode.path("scenarioResults");
        if (scenarios.isArray()) {
            for (JsonNode scenario : scenarios) {
                String name = scenario.path("name").asText();
                boolean failed = scenario.path("failed").asBoolean();
                String status = failed ? "**FAILED**" : "**PASSED**";

                md.append("#### ").append(name).append("\n");
                md.append("- **Status**: ").append(status).append("\n");
                md.append("- **Duration**: ").append(scenario.path("durationMillis").asDouble()).append("ms\n\n");

                // Extract requests/responses if needed
                // This logic iterates through steps to find requests and responses
                JsonNode steps = scenario.path("stepResults");
                if (steps.isArray()) {
                    for (JsonNode stepResult : steps) {

                        String stepLog = stepResult.path("stepLog").asText();

                        if (stepLog != null && !stepLog.isEmpty()) {
                            // Simple regex or check to identify request/response logs
                            if (stepLog.contains("request:") || stepLog.contains("response time in milliseconds")) {
                                md.append("```\n").append(stepLog.trim()).append("\n```\n\n");
                            }
                        }
                    }
                }
            }
        }
        md.append("\n");
    }
}
