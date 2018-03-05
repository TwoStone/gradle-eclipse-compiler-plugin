package de.set.gradle.ecj

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class EclipseCompilerPluginIntegrationSpec extends Specification {

    private static def GRADLE_VERSIONS = ['4.0', '4.2', '4.4.1', '4.5', '4.6']

    @Rule TemporaryFolder temporaryProjectDir = new TemporaryFolder()

    File buildFile
    File settingsFile
    File projectDir

    def setup() {
        projectDir = temporaryProjectDir.getRoot()
        buildFile = temporaryProjectDir.newFile('build.gradle')
        settingsFile = temporaryProjectDir.newFile('settings.gradle')
    }

    @Unroll
    def 'use eclipse compiler for compiling with Gradle version #gradleVersion'() {
        given:
        writeHelloWorld("de.set.gradle")
        buildFile << '''\
            plugins {
                id 'de.set.ecj'
                id 'java'
            }
            
            repositories {
                jcenter()
            }
        '''.stripIndent()

        when:
        def result = runGradle(gradleVersion, 'build', '-i')

        then:
        fileExists('build/classes/java/main/de/set/gradle/HelloWorld.class')
        result.getOutput().contains('Compiling sources using eclipse compiler for java')
        result.task(':compileJava').outcome == TaskOutcome.SUCCESS

        where:
        gradleVersion << GRADLE_VERSIONS
    }

    @Unroll
    def 'javaCompile output is cachable with Gradle version #gradleVersion'() {
        given:
        writeHelloWorld("de.set.gradle")
        buildFile << '''\
            plugins {
                id 'de.set.ecj'
                id 'java'
            }
            
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
        def result = runGradle(gradleVersion, 'build', '--build-cache')

        then:
        fileExists('build/classes/java/main/de/set/gradle/HelloWorld.class')
        result.task(':compileJava').outcome == TaskOutcome.SUCCESS

        when:
        new File(projectDir, 'build').deleteDir()
        def secondCall = runGradle(gradleVersion, 'build', '--build-cache')

        then:
        fileExists('build/classes/java/main/de/set/gradle/HelloWorld.class')
        secondCall.task(':compileJava').outcome == TaskOutcome.FROM_CACHE

        where:
        gradleVersion << GRADLE_VERSIONS
    }

    private BuildResult runGradle(String gradleVersion, String... args) {
        def arguments = []
        arguments.addAll(args)
        arguments.add('-s')

        GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(getProjectDir())
                .withArguments(arguments)
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