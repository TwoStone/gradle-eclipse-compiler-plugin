package de.set.gradle.ecj;

/**
 * Extension for configuring the Eclipse Compiler for Java.
 */
public class EclipseCompilerExtension {

  private String toolGroupId;
  private String toolArtifactId;
  private String toolVersion;

  
  public String getToolGroupId() {
	return toolGroupId;
  }

  public void setToolGroupId(String toolGroupId) {
    this.toolGroupId = toolGroupId;
    }
	
  public String getToolArtifactId() {
    return toolArtifactId;
    }
	
  public void setToolArtifactId(String toolArtifactId) {
    this.toolArtifactId = toolArtifactId;
    }

  public String getToolVersion() {
    return toolVersion;
  }

  public void setToolVersion(String toolVersion) {
    this.toolVersion = toolVersion;
  }
}
