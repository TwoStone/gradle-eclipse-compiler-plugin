package de.set.gradle.ecj

import org.gradle.api.logging.LogLevel
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class EclipseCompilerPluginIntegrationSpec extends Specification {

    @Rule TemporaryFolder temporaryProjectDir = new TemporaryFolder()

    File buildFile
    File settingsFile
    File projectDir

    def setup() {
        projectDir = temporaryProjectDir.getRoot()
        buildFile = temporaryProjectDir.newFile('build.gradle')
        settingsFile = temporaryProjectDir.newFile('settings.gradle')
    }

    def 'use eclipse compiler for compiling'() {
        given:
        writeHelloWorld("de.set.gradle")
        buildFile << '''\
            apply plugin: 'de.set.ecj'
            apply plugin: 'java'
            
            repositories {
                jcenter()
            }
        '''.stripIndent()

        when:
        def result = org.gradle.testkit.runner.GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(getProjectDir())
            .withArguments('build', '-i')
            .withPluginClasspath()
            .build()

        then:
        fileExists('build/classes/java/main/de/set/gradle/HelloWorld.class')
        result.getOutput().contains('Compiling sources using eclipse compiler for java')
        result.task(':compileJava').outcome == TaskOutcome.SUCCESS

        where:
        gradleVersion << ['4.0', '4.2', '4.3', '4.4.1']
    }

    def 'javaCompile output is cachable'() {
        given:
        writeHelloWorld("de.set.gradle")
        buildFile << '''\
            apply plugin: 'de.set.ecj'
            apply plugin: 'java'
            
            repositories {
                jcenter()
            }
        '''.stripIndent()
        settingsFile << '''
            buildCache {
                local(DirectoryBuildCache) {
                    enabled = true
                    directory = new File(rootDir, 'build-cache')
                }
            }
        '''.stripIndent()

        when:
        runGradle(gradleVersion, 'build', '--build-cache', '-i')
        def secondCall = runGradle(gradleVersion, 'clean', 'build', '--build-cache', '-i')

        then:
        fileExists('build/classes/java/main/de/set/gradle/HelloWorld.class')
        secondCall.task(':compileJava').outcome == TaskOutcome.FROM_CACHE
        !secondCall.output.contains('Compiling sources using eclipse compiler for java')

        where:
        gradleVersion << ['4.0', '4.2', '4.3', '4.4.1']
    }

    private BuildResult runGradle(String gradleVersion, String... args) {
        org.gradle.testkit.runner.GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(getProjectDir())
                .withArguments('build', '-i')
                .withPluginClasspath()
                .build()
    }

    protected void writeHelloWorld(String packageDotted, File baseDir = projectDir) {
        def path = 'src/main/java/' + packageDotted.replace('.', '/') + '/HelloWorld.java'
        def javaFile = createFile(path, baseDir)
        javaFile << """\
            package ${packageDotted};
        
            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Hello Integration Test");
                }
            }
            """.stripIndent()
    }

    protected File createFile(String path, File baseDir = projectDir) {
        File file = new File(baseDir, path)
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
            file.createNewFile()
        }
        return file
    }

    protected boolean fileExists(String path) {
        new File(projectDir, path).exists()
    }
}