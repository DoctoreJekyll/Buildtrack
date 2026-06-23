package com.jose.buildtrack;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildStatus;
import com.jose.buildtrack.domain.BuildVersion;
import com.jose.buildtrack.domain.SoftwareProject;

import java.util.List;

public class App {

    public static void main(String[] args) {
        System.out.println("=== BuildTrack Demo ===");
        System.out.println();

        SoftwareProject project = createProject();

        registerBuilds(project);

        printRegisteredBuilds(project);

        processBuilds(project);

        printFinalSummary(project);

        printBuildsByStatus(project, BuildStatus.APPROVED, "Approved builds");
        printBuildsByStatus(project, BuildStatus.REJECTED, "Rejected builds");
        printBuildsByStatus(project, BuildStatus.CREATED, "Pending builds");

        runInvalidScenarios(project);
    }

    private static SoftwareProject createProject() {
        SoftwareProject project = new SoftwareProject("project-001", "BuildTrack");

        System.out.println("Project created:");
        System.out.println(project.getId() + " - " + project.getName());
        System.out.println();

        return project;
    }

    private static void registerBuilds(SoftwareProject project) {
        project.addBuild(new Build("build-001", new BuildVersion("1.0.0")));
        project.addBuild(new Build("build-002", new BuildVersion("1.1.0")));
        project.addBuild(new Build("build-003", new BuildVersion("1.2.0")));
    }

    private static void printRegisteredBuilds(SoftwareProject project) {
        System.out.println("Registered builds:");

        for (Build build : project.getBuilds()) {
            printBuild(build);
        }

        System.out.println();
    }

    private static void processBuilds(SoftwareProject project) {
        System.out.println("Processing builds...");

        project.startBuildValidation("build-001");
        project.approveBuildById("build-001");
        System.out.println("build-001 approved");

        project.startBuildValidation("build-002");
        project.rejectBuildById("build-002");
        System.out.println("build-002 rejected");

        System.out.println("build-003 pending");
        System.out.println();
    }

    private static void printFinalSummary(SoftwareProject project) {
        System.out.println("Final summary:");
        System.out.println("CREATED: " + project.countBuildsByStatus(BuildStatus.CREATED));
        System.out.println("VALIDATING: " + project.countBuildsByStatus(BuildStatus.VALIDATING));
        System.out.println("APPROVED: " + project.countBuildsByStatus(BuildStatus.APPROVED));
        System.out.println("REJECTED: " + project.countBuildsByStatus(BuildStatus.REJECTED));
        System.out.println();
    }

    private static void printBuildsByStatus(
            SoftwareProject project,
            BuildStatus status,
            String title
    ) {
        System.out.println(title + ":");

        List<Build> builds = project.findBuildsByStatus(status);

        if (builds.isEmpty()) {
            System.out.println("No builds found");
        } else {
            for (Build build : builds) {
                printBuild(build);
            }
        }

        System.out.println();
    }

    private static void runInvalidScenarios(SoftwareProject project) {
        System.out.println("Invalid scenarios:");

        try {
            new BuildVersion("release");
        } catch (IllegalArgumentException exception) {
            System.out.println("Invalid version rejected: " + exception.getMessage());
        }

        try {
            project.addBuild(new Build("build-004", new BuildVersion("1.0.0")));
        } catch (IllegalArgumentException exception) {
            System.out.println("Duplicated version rejected: " + exception.getMessage());
        }

        try {
            project.approveBuildById("build-003");
        } catch (IllegalStateException exception) {
            System.out.println("Invalid transition rejected: " + exception.getMessage());
        }
    }

    private static void printBuild(Build build) {
        System.out.println(
                build.getId()
                        + " / "
                        + build.getVersion()
                        + " / "
                        + build.getStatus()
        );
    }
}