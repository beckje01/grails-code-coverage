scriptEnv = "test"

includeTargets << grailsScript("_GrailsPackage")
includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsTest")

reportFormat = 'html'
postProcessReports = true
codeCoverageExclusionList = [
    "**/*BootStrap*",
    "Config*",
    "**/*DataSource*",
    "**/*resources*",
    "**/*UrlMappings*",
    "**/*Tests*",
    "**/grails/test/**",
    "**/org/codehaus/groovy/grails/**",
    "**/PreInit*",
    "*GrailsPlugin*" ]

// We need to save the exit code from the 'testApp' target.
def testAppExitCode = 0

//TODO - would like this to work (put file in tmp dir), but cobertura creates a cobertura.ser in the running dir anyway...
//dataFile = "${grailsTmp}/cobertura.ser"
dataFile = "cobertura.ser"

target ('testAppCobertura': "Test App with Cobertura") {
    depends(classpath, checkVersion, configureProxy, parseArguments, bootstrap)

    cleanup()
    packageApp()

    // Check whether the project defines its own directory for test
    // reports.
    coverageReportDir = "${config.grails.testing.reports.destDir ?: testReportsDir}/cobertura"

    // Add any custom exclusions defined by the project.
    // this needs to happen AFTER packageApp so config.groovy is properly loaded
    if (config.coverage.exclusions) {
      codeCoverageExclusionList += config.coverage.exclusions
    }

    ant.delete(dir:coverageReportDir, quiet:true)

    ant.path(id: "cobertura.classpath"){
        fileset(dir:"${codeCoveragePluginDir}/lib"){
            include(name:"*.jar")
        }
    }

    if (argsMap.xml) {
        reportFormat = 'xml'
    }

    if (argsMap.nopost) {
        postProcessReports = false
    }

    compileTests()
    instrumentClasses()
    def exitCode = testApp()

	flushCoverageData()

    coberturaReport()
    if (postProcessReports) {
		replaceControllerClosureNamesInReports()
	}
    ant.delete(dir:classesDirPath)
    event("StatusFinal", ["Cobertura Code Coverage Complete (view reports in: ${coverageReportDir})"])

    // Exit the script using the code returned by 'testApp'.
    return exitCode
}

target(cleanup:"Remove old files") {
    ant.delete(file:"${dataFile}", quiet:true)
    ant.delete(dir:classesDirPath, quiet:true)
}

target(instrumentClasses:"Instruments the compiled classes") {
    ant.taskdef (  classpathRef : 'cobertura.classpath', resource:"tasks.properties" )
    try {
        //for now, instrument classes in the same directory grails creates for testClasses
        //TODO - need to figure out how to put cobertura instrumented classes in different dir
        //and put that dir in front of testClasses in the classpath
        ant.'cobertura-instrument' (datafile:"${dataFile}") {
            fileset(dir:classesDirPath) {
                include(name:"**/*.class")
                codeCoverageExclusionList.each { pattern ->
                    exclude(name:pattern)
                }
            }
        }
    }
    catch(Exception e) {
       event("StatusFinal", ["Error instrumenting classes: ${e.message}"])
       exit(1)
    }
}

target(coberturaReport:"Generate Cobertura Reports") {
    ant.mkdir(dir:"${coverageReportDir}")
    ant.taskdef (  classpathRef : 'cobertura.classpath', resource:"tasks.properties" )
    try {
        ant.'cobertura-report'(destDir:"${coverageReportDir}", datafile:"${dataFile}", format:reportFormat){
            //load all these dirs independently so the dir structure is flattened,
            //otherwise the source isn't found for the reports
            fileset(dir:"${basedir}/grails-app/controllers")
            fileset(dir:"${basedir}/grails-app/domain")
            fileset(dir:"${basedir}/grails-app/services")
            fileset(dir:"${basedir}/grails-app/taglib")
            fileset(dir:"${basedir}/grails-app/utils")
            fileset(dir:"${basedir}/src/groovy")
            fileset(dir:"${basedir}/src/java")
            if (config.coverage?.sourceInclusions){
                config.coverage.sourceInclusions.each {
                    fileset(dir:"${basedir}/${it}")
                }
            }
        }
    }
    catch(Exception e) {
       event("StatusFinal", ["Compilation Error: ${e.message}"])
       exit(1)
    }
}

target('replaceControllerClosureNamesInReports': 'replace controller closure class name with action name') {
    def startTime = new Date().time
    def controllers = grailsApp.controllerClasses
    controllers.each {controllerClass ->
        def closures = [:]
        controllerClass.reference.propertyDescriptors.each {propertyDescriptor ->
            def closureClassName = controllerClass.getPropertyOrStaticPropertyOrFieldValue(propertyDescriptor.name, Closure)?.class?.name
            if (closureClassName) {
				// the name in the reports is sans package; subtract the package name
				def nameToReplace = closureClassName - "${controllerClass.packageName}."
				
                ant.replace(dir: "${coverageReportDir}",
                        token: ">${nameToReplace}<",
                        value: ">${controllerClass.name}.${propertyDescriptor.name}<") {
                    include(name: "**/*${controllerClass.fullName}.html")
                    include(name: "frame-summary*.html")
                }
            }
        }
    }
    def endTime = new Date().time
    println "Done with post processing reports in ${endTime - startTime}ms"
}

def flushCoverageData() {
	//per the Cobertura FAQ, force save of coverage data before JVM exits...
    //see http://cobertura.sourceforge.net/faq.html "Cobertura only writes the coverage data file when..."
    try {
        def saveClass = Class.forName('net.sourceforge.cobertura.coveragedata.ProjectData')
        def saveMethod = saveClass.getDeclaredMethod("saveGlobalProjectData", new Class[0])
        saveMethod.invoke(null,new Object[0]);
    } catch (Throwable t) {
        println t
		event("StatusFinal", ["Unable to flush Cobertura code coverage data."])
		exit(1)
	}
}

setDefaultTarget("testAppCobertura")

