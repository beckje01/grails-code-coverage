grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// specify dependency exclusions here; for example, uncomment this to disable ehcache:
		// excludes 'ehcache'
	}
	log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve
	legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

	repositories {
		grailsPlugins()
		grailsHome()
		grailsCentral()

		mavenCentral()

		//mavenRepo "http://repository.jboss.com/maven2/"
	}
	dependencies {


		runtime 'net.sourceforge.cobertura:cobertura:1.9.4.1'
	}

	plugins {
		runtime(":hibernate:$grailsVersion") {
			export = false
		}
		build(":tomcat:$grailsVersion") {
			export = false
		}

		build(":release:2.2.1") {
			export = false
		}
	}
}

//<editor-fold desc="Release Plugin External Maven Config">
def mavenConfigFile = new File("${basedir}/grails-app/conf/mavenInfo.groovy")
if (mavenConfigFile.exists()) {
	def slurpedMavenInfo = new ConfigSlurper().parse(mavenConfigFile.toURL())
	slurpedMavenInfo.grails.project.repos.each {k, v ->
		println "Adding maven info for repo $k"
		grails.project.repos."$k" = v
	}
}
else {
	println "No mavenInfo file found."
}
//</editor-fold>
