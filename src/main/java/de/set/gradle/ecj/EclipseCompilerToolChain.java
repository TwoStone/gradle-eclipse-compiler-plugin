package de.set.gradle.ecj;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.tasks.compile.JavaCompileSpec;
import org.gradle.api.internal.tasks.compile.NormalizingJavaCompiler;
import org.gradle.jvm.internal.toolchain.JavaToolChainInternal;
import org.gradle.jvm.platform.JavaPlatform;
import org.gradle.language.base.internal.compile.CompileSpec;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.platform.base.internal.toolchain.ToolProvider;
import org.gradle.util.TreeVisitor;

/**
 * {@link org.gradle.jvm.toolchain.JavaToolChain} that uses the Eclipse Compiler for Java for
 * compilation.
 */
public class EclipseCompilerToolChain implements JavaToolChainInternal {

  private final JavaVersion javaVersion = JavaVersion.current();
  private final Configuration compilerConfiguration;
  private final EclipseCompilerExtension extension;

  private EclipseCompilerToolChain(
      Configuration compilerConfiguration, EclipseCompilerExtension extension) {
    this.compilerConfiguration = compilerConfiguration;
    this.extension = extension;
  }

  static EclipseCompilerToolChain create(Project project) {
    return new EclipseCompilerToolChain(
        project.getConfigurations().getByName(EclipseCompilerBasePlugin.ECJ_CONFIGURATION),
        (EclipseCompilerExtension)
            project.getExtensions().getByName(EclipseCompilerBasePlugin.ECJ_EXTENSION));
  }

  @Override
  public JavaVersion getJavaVersion() {
    return this.javaVersion;
  }

  @Override
  public String getVersion() {
    return this.javaVersion.getMajorVersion();
  }

  @Override
  public String getDisplayName() {
    return "Eclipse Compiler for Java " + this.extension.getToolVersion() + "(" + javaVersion + ")";
  }

  @Override
  public String getName() {
    return "ecj" + this.extension.getToolVersion();
  }

  @Override
  public ToolProvider select(JavaPlatform javaPlatform) {
    if (javaPlatform != null && javaPlatform.getTargetCompatibility().compareTo(javaVersion) > 0) {
      return new UnavailableToolProvider(javaPlatform);
    }
    return new JavaToolProvider();
  }

  private class JavaToolProvider implements ToolProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CompileSpec> Compiler<T> newCompiler(Class<T> spec) {
      if (JavaCompileSpec.class.isAssignableFrom(spec)) {
        return (Compiler<T>)
            new NormalizingJavaCompiler(new EclipseCompilerAdapter(compilerConfiguration));
      }
      throw new IllegalArgumentException(
          String.format("Don't know how to compile using spec of type %s.", spec.getSimpleName()));
    }

    @Override
    public <T> T get(Class<T> toolType) {
      throw new IllegalArgumentException(
          String.format("Don't know how to provide tool of type %s.", toolType.getSimpleName()));
    }

    @Override
    public boolean isAvailable() {
      return true;
    }

    @Override
    public void explain(TreeVisitor<? super String> visitor) {
    }
  }

  private class UnavailableToolProvider implements ToolProvider {

    private final JavaPlatform targetPlatform;

    private UnavailableToolProvider(JavaPlatform targetPlatform) {
      this.targetPlatform = targetPlatform;
    }

    @Override
    public <T extends CompileSpec> Compiler<T> newCompiler(Class<T> compilerClass) {
      throw new IllegalArgumentException(getMessage());
    }

    @Override
    public <T> T get(Class<T> toolType) {
      throw new IllegalArgumentException(
          String.format("Don\'t know how to provide tool of type %s.", toolType.getSimpleName()));
    }

    @Override
    public boolean isAvailable() {
      return false;
    }

    @Override
    public void explain(TreeVisitor<? super String> visitor) {
      visitor.node(getMessage());
    }

    private String getMessage() {
      return String.format(
          "Could not target platform: '%s' using tool chain: '%s'.",
          targetPlatform.getDisplayName(), EclipseCompilerToolChain.this.getDisplayName());
    }
  }
}
