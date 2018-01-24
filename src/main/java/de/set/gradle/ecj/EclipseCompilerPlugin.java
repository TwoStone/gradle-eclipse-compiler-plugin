package de.set.gradle.ecj;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaToolChain;

/**
 * Gradle {@link org.gradle.api.Plugin} that activates the modifies all JavaCompile tasks of the
 * project to use the Eclipse Compiler for Java.
 */
public class EclipseCompilerPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply(EclipseCompilerBasePlugin.class);

    final JavaToolChain toolChain = EclipseCompilerToolChain.create(project);
    project
        .getTasks()
        .withType(JavaCompile.class)
        .all(javaCompile -> javaCompile.setToolChain(toolChain));
  }
}
