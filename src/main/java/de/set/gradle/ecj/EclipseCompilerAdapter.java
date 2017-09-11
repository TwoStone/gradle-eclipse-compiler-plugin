package de.set.gradle.ecj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.tasks.compile.CommandLineJavaCompiler;
import org.gradle.api.internal.tasks.compile.JavaCompileSpec;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.WorkResult;
import org.gradle.internal.jvm.Jvm;
import org.gradle.language.base.internal.compile.Compiler;

/**
 * {@link Compiler} that calls the Eclipse Compiler for Java for code compilation.
 */
public class EclipseCompilerAdapter implements Compiler<JavaCompileSpec> {

  private static final Logger LOGGER = Logging.getLogger(EclipseCompilerAdapter.class);
  private Configuration compilerConfiguration;

  public EclipseCompilerAdapter(Configuration compilerConfiguration) {
    this.compilerConfiguration = compilerConfiguration;
  }

  @Override
  public WorkResult execute(JavaCompileSpec javaCompileSpec) {
    LOGGER.info("Compiling sources using eclipse compiler for java");

    List<String> jvmArgs = new ArrayList<>();
    jvmArgs.add("-cp");
    jvmArgs.add(compilerConfiguration.getAsPath());
    jvmArgs.add("org.eclipse.jdt.internal.compiler.batch.Main");

    File executable = Jvm.current().getJavaExecutable();
    javaCompileSpec.getCompileOptions().getForkOptions().setJvmArgs(jvmArgs);
    javaCompileSpec
        .getCompileOptions()
        .getForkOptions()
        .setExecutable(executable.getAbsolutePath());

    // We are using gradle's commandline compiler, that forks a new java process for us
    return new CommandLineJavaCompiler().execute(javaCompileSpec);
  }
}
