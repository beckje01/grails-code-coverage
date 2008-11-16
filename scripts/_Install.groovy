String pluginDir = binding.getVariable('code-coveragePluginDir')

if (grailsVersion == '1.0.4'){
	println "renaming ${pluginDir}/scripts/Events.groovy to ${pluginDir}/scripts/_Events.groovy"
	Ant.move(file:"${pluginDir}/scripts/Events.groovy", tofile:"${pluginDir}/scripts/_Events.groovy", failonerror:false)
}
