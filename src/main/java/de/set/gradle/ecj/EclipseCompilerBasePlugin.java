package de.set.gradle.ecj;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;

/**
 * Base Gradle Plugin for the Eclipse Compiler Plugin, that adds the required configuration for the
 * ecj classpath to the project.
 */
public class EclipseCompilerBasePlugin implements Plugin<Project> {

  static final String ECJ_CONFIGURATION = "ecj";
  static final String ECJ_EXTENSION = "ecj";

  private static final String DEFAULT_DEPENDENCY = "org.eclipse.jdt.core.compiler:ecj:";
  private static final String DEFAULT_VERSION = "4.6.1";

  @Override
  public void apply(Project project) {
    EclipseCompilerExtension extension =
        project.getExtensions().create(ECJ_EXTENSION, EclipseCompilerExtension.class);
    extension.setToolVersion(DEFAULT_VERSION);

    project
        .getConfigurations()
        .create(
            ECJ_CONFIGURATION,
            new Action<Configuration>() {
              @Override
              public void execute(Configuration configuration) {
                configuration.defaultDependencies(
                    new Action<DependencySet>() {
                      @Override
                      public void execute(DependencySet dependencies) {
                        dependencies.add(
                            project
                                .getDependencies()
                                .create(DEFAULT_DEPENDENCY + extension.getToolVersion()));
                      }
                    });
              }
            });
  }
}
