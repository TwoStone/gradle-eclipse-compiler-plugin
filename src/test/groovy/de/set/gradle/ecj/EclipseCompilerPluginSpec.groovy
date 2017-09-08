package de.set.gradle.ecj

import nebula.test.PluginProjectSpec
import org.gradle.api.tasks.compile.JavaCompile

class EclipseCompilerPluginSpec extends PluginProjectSpec {
    @Override
    String getPluginName() {
        return 'de.set.ecj'
    }

    def 'should apply base plugin'() {
        when:
        project.apply plugin: pluginName
        project.evaluate()

        then:
        project.plugins.hasPlugin('de.set.ecj-base')
        project.configurations.findByName('ecj')
        project.extensions.findByName('ecj')
    }

    def 'should configure all JavaCompile tasks'() {
        when:
        project.apply plugin: 'java'
        project.apply plugin: pluginName
        project.evaluate()

        then:
        def compileTasks = project.tasks.withType(JavaCompile)
        compileTasks.size() > 0
        compileTasks.all {
             assert it.toolChain instanceof EclipseCompilerToolChain
        }
    }
}
