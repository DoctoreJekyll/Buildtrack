/*package com.jose.buildtrack;

import com.jose.buildtrack.domain.*;
import com.jose.buildtrack.service.BuildService;

public class App {

    public static void main(String[] args) {

        BuildService buildService = new BuildService();

        System.out.println("=== BUILDTRACK SIMULATION START ===\n");

        // 1. Crear build
        System.out.println("Creating build...");

        Build build = buildService.createBuild(
                "B-001",
                "1.0.0",
                Platform.WINDOWS
        );

        System.out.println("Build created:");
        System.out.println("ID: " + build.getId());
        System.out.println("Version: " + build.getVersion());
        System.out.println("Platform: " + build.getPlatform());
        System.out.println("Status: " + build.getStatus());
        System.out.println();

        // 2. Añadir issues
        System.out.println("Adding issues...");

        Issue issue1 = new Issue(
                "I-001",
                "NullPointerException on startup",
                IssueSeverity.BLOCKER
        );

        Issue issue2 = new Issue(
                "I-002",
                "UI alignment bug",
                IssueSeverity.LOW
        );

        build.addIssue(issue1);
        build.addIssue(issue2);

        System.out.println("Issues added: " + build.getIssues().size());
        System.out.println();

        // 3. Iniciar validación
        System.out.println("Starting validation...");

        buildService.startValidation(build.getId());

        System.out.println("Build status: " + build.getStatus());
        System.out.println();

        // 4. Evaluar estado del build
        System.out.println("Evaluating build...");

        if (build.hasOpenBlockerIssues()) {
            System.out.println("Open BLOCKER issues detected!");
            System.out.println("Rejecting build...");
            buildService.rejectBuild(build.getId());
        } else {
            System.out.println("No blocker issues found.");
            System.out.println("Approving build...");
            buildService.approveBuild(build.getId());
        }

        System.out.println();

        // 5. Resultado final
        System.out.println("=== FINAL RESULT ===");
        System.out.println("Build ID: " + build.getId());
        System.out.println("Version: " + build.getVersion());
        System.out.println("Platform: " + build.getPlatform());
        System.out.println("Final Status: " + build.getStatus());

        System.out.println("\nIssues summary:");
        for (Issue issue : build.getIssues()) {
            System.out.println("- " + issue.getId()
                    + " | " + issue.getSeverity()
                    + " | " + issue.getStatus());
        }

        System.out.println("\n=== BUILDTRACK SIMULATION END ===");
    }
}*/