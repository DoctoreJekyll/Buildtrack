package com.jose.buildtrack;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.SoftwareProject;

public class App 
{
    public static void main( String[] args )
    {
        Build build = new Build("1", "1.0.0");
        System.out.println("Build ID: " + build.getId());   
        System.out.println("Build Version: " + build.getVersion());
        System.out.println("Build Status: " + build.getStatus());

        Build build2 = new Build("2", "1.0.1");
        System.out.println("Build ID: " + build2.getId());
        System.out.println("Build Version: " + build2.getVersion());
        System.out.println("Build Status: " + build2.getStatus());

        build.startValidation();
        System.out.println("Build Status after validation: " + build.getStatus());

        SoftwareProject project = new SoftwareProject("1", "My Project");
        project.addBuild(build);
        project.addBuild(build2);
        System.out.println("Project ID: " + project.getId());
        System.out.println("Project Name: " + project.getName());
        System.out.println("Project Builds: " + project.getBuilds().size());
    }
}
