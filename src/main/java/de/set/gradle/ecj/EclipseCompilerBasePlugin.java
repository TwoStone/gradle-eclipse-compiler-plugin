package de.set.gradle.ecj;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Base Gradle Plugin for the Eclipse Compiler Plugin, that adds the required configuration for the
 * ecj classpath to the project.
 */
public class EclipseCompilerBasePlugin implements Plugin<Project> {

  static final String ECJ_CONFIGURATION = "ecj";
  static final String ECJ_EXTENSION = "ecj";

  private static final String DEFAULT_DEPENDENCY_GROUP = "org.eclipse.jdt";
  private static final String DEFAULT_DEPENDENCY_ARTIFACT = "ecj";
  private static final String DEFAULT_VERSION = "3.15.1";
  
  private boolean isEmpty(String str) {
      return str == null || str.trim().length() == 0;
  }

  @Override
  public void apply(Project project) {
    EclipseCompilerExtension extension =
        project.getExtensions().create(ECJ_EXTENSION, EclipseCompilerExtension.class);
    if (isEmpty(extension.getToolGroupId())) {
        extension.setToolGroupId(DEFAULT_DEPENDENCY_GROUP);
    }
    if (isEmpty(extension.getToolArtifactId())) {
        extension.setToolArtifactId(DEFAULT_DEPENDENCY_ARTIFACT);
    }
    if (isEmpty(extension.getToolVersion())) {
      extension.setToolVersion(DEFAULT_VERSION);
    }

    project
        .getConfigurations()
        .create(
            ECJ_CONFIGURATION,
            configuration -> configuration.defaultDependencies(
                dependencies -> {
                  dependencies.add(
                      project
                          .getDependencies()
                          .create(extension.getToolGroupId() + ":" + 
                                  extension.getToolArtifactId() + ":" + 
                                  extension.getToolVersion()));
                }));
  }
}
