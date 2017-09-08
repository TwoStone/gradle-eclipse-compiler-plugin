package de.set.gradle.ecj

import nebula.test.PluginProjectSpec

class EclipseCompilerBasePluginSpec extends PluginProjectSpec {
    @Override
    String getPluginName() {
        return 'de.set.ecj-base'
    }

    def 'should apply ecj configuration'() {
        when:
        project.apply plugin: pluginName
        project.evaluate()

        then:
        project.configurations.findByName('ecj')
    }

    def 'should apply ecj extension'() {
        when:
        project.apply plugin: pluginName
        project.evaluate()

        then:
        project.extensions.findByName('ecj')
    }
}