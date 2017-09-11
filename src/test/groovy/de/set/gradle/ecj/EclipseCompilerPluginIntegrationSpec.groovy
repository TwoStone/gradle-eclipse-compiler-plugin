package de.set.gradle.ecj

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import org.gradle.api.logging.LogLevel

class EclipseCompilerPluginIntegrationSpec extends IntegrationSpec {

    def 'use eclipse compiler for compiling'() {
        given:
        writeHelloWorld("de.set.gradle")
        buildFile << '''
            apply plugin: 'de.set.ecj'
            apply plugin: 'java'
            
            repositories {
                jcenter()
            }
        '''.stripIndent()
        logLevel = logLevel.INFO

        when:
        ExecutionResult result = runTasksSuccessfully('build')

        then:
        fileExists('build/classes/java/main/de/set/gradle/HelloWorld.class')
        result.standardOutput.contains(':compileJava')
        result.standardOutput.contains('Compiling sources using eclipse compiler for java')
    }

    def 'javaCompile output is cachable'() {
        given:
        writeHelloWorld("de.set.gradle")
        buildFile << '''
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
        logLevel = LogLevel.INFO

        when:
        ExecutionResult insertCache = runTasksSuccessfully('build', '--build-cache')
        ExecutionResult secondCall = runTasksSuccessfully('clean', 'build', '--build-cache')

        then:
        fileExists('build/classes/java/main/de/set/gradle/HelloWorld.class')
        secondCall.standardOutput.contains(':compileJava FROM-CACHE')
        !secondCall.standardOutput.contains('Compiling sources using eclipse compiler for java')
    }
}