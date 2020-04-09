package se.xnix;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_RESOURCES;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Goal which print a depinfo.properties file with all dependency versions
 */
@Mojo(name = "property-file", defaultPhase = GENERATE_RESOURCES)
public class PropertyFileMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        File f = new File(project.getBuild().getOutputDirectory());

        if (!f.exists()) {
            f.mkdirs();
        }
        File touch = new File(f, "depinfo.properties");

        try (FileWriter w = new FileWriter(touch)) {
            var p = new Properties();
            project.getDependencies().forEach(d -> p.put(generateKey(d), d.getVersion()));
            p.store(w, "Generated by depinfo-maven-plugin");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        }
    }

    private static String generateKey(Dependency dependency) {
        return dependency.getGroupId() + ":" + dependency.getArtifactId();
    }
}