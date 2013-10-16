grails.project.work.dir = 'target'

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {

	inherits 'global'
	log 'info'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()

	}

	dependencies {
		runtime 'net.sourceforge.cobertura:cobertura:1.9.4.1', {
			excludes 'ant', 'log4j'
		}
//		runtime 'xml-apis:xml-apis:2.0.2'
//		runtime 'org.ow2.asm:asm:4.1'
		runtime 'xerces:xercesImpl:2.11.0'
		runtime 'xalan:xalan:2.7.1'

//		compile 'org.slf4j:slf4j-api:1.7.5'


	}

	plugins {
		build ':release:3.0.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}
