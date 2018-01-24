package de.set.gradle.ecj;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.tasks.compile.CommandLineJavaCompiler;
import org.gradle.api.internal.tasks.compile.JavaCompileSpec;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.compile.ForkOptions;
import org.gradle.internal.jvm.Jvm;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.tooling.BuildException;

/**
 * {@link Compiler} that calls the Eclipse Compiler for Java for code compilation.
 */
public class EclipseCompilerAdapter implements Compiler<JavaCompileSpec> {

  private static final Logger LOGGER = Logging.getLogger(EclipseCompilerAdapter.class);
  private Configuration compilerConfiguration;

  EclipseCompilerAdapter(Configuration compilerConfiguration) {
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
    final ForkOptions forkOptions = getForkOptions(javaCompileSpec);
    forkOptions.setJvmArgs(jvmArgs);
    forkOptions.setExecutable(executable.getAbsolutePath());

    // We are using gradle's commandline compiler, that forks a new java process for us
    return new CommandLineJavaCompiler().execute(javaCompileSpec);
  }

  private static ForkOptions getForkOptions(JavaCompileSpec compileSpec) {
    try {
      Method compileOptionsGetter = JavaCompileSpec.class.getMethod("getCompileOptions");
      final Object compileOptions = compileOptionsGetter.invoke(compileSpec);

      final Method getForkOptions = compileOptions.getClass().getMethod("getForkOptions");
      final Object forkOptions = getForkOptions.invoke(compileOptions);
      return (ForkOptions) forkOptions;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new BuildException("Cannot access javaCompileSpec forkOptions", e);
    }
  }
}
