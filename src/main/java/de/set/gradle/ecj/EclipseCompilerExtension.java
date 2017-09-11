package de.set.gradle.ecj;

/**
 * Extension for configuring the Eclipse Compiler for Java.
 */
public class EclipseCompilerExtension {

  private String toolVersion;

  public String getToolVersion() {
    return toolVersion;
  }

  public void setToolVersion(String toolVersion) {
    this.toolVersion = toolVersion;
  }
}
