# ❗❗❗ Deprecation notice ❗❗❗

This plugin is no longer actively maintenanced. The latest gradle updates removed the possibility to hook into the JavaToolChain so this plugin does not work with newer gradle versions. Your are welcome to create a fork of this repository and continue the work.

# Eclipse Compiler for Java (ecj) for Gradle

## Description
The plugin provides a new [JavaToolChain](https://docs.gradle.org/current/javadoc/org/gradle/jvm/toolchain/JavaToolChain.html) for using the [Eclipse Compiler for Java (ecj)](https://help.eclipse.org/neon/topic/org.eclipse.jdt.doc.user/tasks/task-using_batch_compiler.htm?cp=1_3_8_0) for compiling java sources.
When applying the plugin to your project, all [JavaCompile](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/compile/JavaCompile.html) tasks will get configured to use the EclipseCompilerToolChain.

## Usage

Note: ECJ got new artifact group after 4.6.1 (original artifact group: org.eclipse.jdt.core.compiler), 
the 4.6.1 version is for eclipse 4.6 and the 3.15.1 with artifact group org.eclipse.jdt for eclipse 4.9
All versions from group org.eclipse.jdt.core.compiler considered as outdated even if version number looks higher then in the group org.eclipse.jdt

### With default ECJ compiler and eclipse project specific ECJ settings (see de.set.gradle.ecj.EclipseCompilerBasePlugin.DEFAULT_VERSION)
```gradle
buildscript {
	dependencies {
		classpath 'gradle.plugin.de.set.gradle:gradle-eclipse-compiler-plugin:1.3.0'
	}
}

plugins {
	id 'de.set.ecj'
	id 'java'
}

compileJava {
	options.compilerArgs << '-properties' << '$projectDir/.settings/org.eclipse.jdt.core.prefs'
}
compileTestJava {
	options.compilerArgs << '-properties' << '$projectDir/.settings/org.eclipse.jdt.core.prefs'
}

```

### With specified ECJ compiler version and eclipse project specific ECJ settings
```
buildscript {
	dependencies {
		classpath 'gradle.plugin.de.set.gradle:gradle-eclipse-compiler-plugin:1.3.0'
	}
}

plugins {
	id 'de.set.ecj'
	id 'java'
}

ecj.toolVersion = '3.15.1'

compileJava {
	options.compilerArgs << '-properties' << '$projectDir/.settings/org.eclipse.jdt.core.prefs'
}
compileTestJava {
	options.compilerArgs << '-properties' << '$projectDir/.settings/org.eclipse.jdt.core.prefs'
}

```

### With specified ECJ compiler artifact (OLD version) and eclipse project specific ECJ settings
```
buildscript {
	dependencies {
		classpath 'gradle.plugin.de.set.gradle:gradle-eclipse-compiler-plugin:1.3.0'
	}
}

plugins {
	id 'de.set.ecj'
	id 'java'
}

ecj.toolGroupId = 'org.eclipse.jdt.core.compiler'
ecj.toolArtifactId = 'ecj'
ecj.toolVersion = '4.6.1'

compileJava {
	options.compilerArgs << '-properties' << '$projectDir/.settings/org.eclipse.jdt.core.prefs'
}
compileTestJava {
	options.compilerArgs << '-properties' << '$projectDir/.settings/org.eclipse.jdt.core.prefs'
}

```
