// install-plugin doesn't call Init.groovy, so call it here to obtain the codeCoveragePluginDir variable
Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
includeTargets << new File ( "${grailsHome}/scripts/Init.groovy" )

if (grailsVersion == '1.0.4'){
	println "renaming ${codeCoveragePluginDir}/scripts/Events.groovy to ${codeCoveragePluginDir}/scripts/_Events.groovy"
	Ant.move(file:"${codeCoveragePluginDir}/scripts/Events.groovy", tofile:"${codeCoveragePluginDir}/scripts/_Events.groovy", failonerror:false)
}
