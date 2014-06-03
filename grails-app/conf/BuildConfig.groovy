grails.project.work.dir = 'target'
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.5
grails.project.source.level = 1.5

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
    }
    log "info" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true
    legacyResolve false
    repositories {
        inherits false
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        runtime('org.apache.ant:ant-launcher:1.8.3')
        runtime 'net.sourceforge.cobertura:cobertura:2.0.3', {
            excludes 'ant', 'log4j'
        }
        runtime 'xerces:xercesImpl:2.11.0'
        runtime 'xalan:xalan:2.7.1'
    }

    plugins {
        build ':release:3.0.1', ':rest-client-builder:1.0.3', {
            export = false
        }
    }
}
