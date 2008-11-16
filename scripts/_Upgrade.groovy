if (grailsVersion == '1.0.4'){
	println "renaming ${codeCoveragePluginDir}/scripts/Events.groovy to ${codeCoveragePluginDir}/scripts/_Events.groovy"
	Ant.move(file:"${codeCoveragePluginDir}/scripts/Events.groovy", tofile:"${codeCoveragePluginDir}/scripts/_Events.groovy", failonerror:false)
}
