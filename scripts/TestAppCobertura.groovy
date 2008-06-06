scriptEnv = "test"

Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
System.setProperty('cobertura.code.coverage', 'on')

includeTargets << new File ( "${grailsHome}/scripts/Package.groovy" )
includeTargets << new File ( "${grailsHome}/scripts/Bootstrap.groovy" )
includeTargets << new File ( "${grailsHome}/scripts/TestApp.groovy" )

pluginHome = codeCoveragePluginDir
reportFormat = 'html'
codeCoverageExclusionList = [
    "**/*BootStrap*",
    "**/*Config*",
    "**/*DataSource*",
    "**/*resources*",
    "**/*UrlMappings*",
    "**/*Tests*",
    "**/PreInit*",
    "CodeCoverageGrailsPlugin*" ]

// We need to save the exit code from the 'testApp' target.
testAppExitCode = 0

//TODO - would like this to work (put file in tmp dir), but cobertura creates a cobertura.ser in the running dir anyway...
//dataFile = "${grailsTmp}/cobertura.ser"
dataFile = "cobertura.ser"

target ('default': "Test App with Cobertura") {
    depends(classpath, checkVersion, configureProxy)

    // Check whether the project defines its own directory for test
    // reports.
    coverageReportDir = "${config.grails.testing.reports.destDir ?: testDir}/cobertura"

    Ant.path(id: "cobertura.classpath"){
        fileset(dir:"${pluginHome}/lib/cobertura"){
            include(name:"*.jar")
        }
    }

    if(args?.indexOf('-xml') >-1) {
        reportFormat = 'xml'
        args -= '-xml'
    }

    cleanup()
    packageApp()

    // Add any custom exclusions defined by the project.
    // this needs to happen AFTER packageApp so config.groovy is properly loaded
    if (config.coverage.exclusions) {
      codeCoverageExclusionList += config.coverage.exclusions
    }

    
    compileTests()
    instrumentTests()
    testApp()

	flushCoverageData()

    coberturaReport()
    Ant.delete(dir:testDirPath)
    event("StatusFinal", ["Cobertura Code Coverage Complete (view reports in: ${coverageReportDir})"])

    // Exit the script using the code returned by 'testApp'.
    System.exit(testAppExitCode)
}

target(cleanup:"Remove old files") {
    Ant.delete(file:"${dataFile}", quiet:true)
    Ant.delete(dir:testDirPath, quiet:true)
    Ant.delete(dir:coverageReportDir, quiet:true)
}

target(instrumentTests:"Instruments the compiled test cases") {
    Ant.taskdef (  classpathRef : 'cobertura.classpath', resource:"tasks.properties" )
    try {
        //for now, instrument classes in the same directory grails creates for testClasses
        //TODO - need to figure out how to put cobertura instrumented classes in different dir
        //and put that dir in front of testClasses in the classpath
        Ant.'cobertura-instrument' (datafile:"${dataFile}") {
            fileset(dir:testDirPath) {
                include(name:"**/*.class")
                codeCoverageExclusionList.each { pattern ->
                    exclude(name:pattern)
                }
            }
        }
    }
    catch(Exception e) {
       event("StatusFinal", ["Compilation Error: ${e.message}"])
       exit(1)
    }
}

target(coberturaReport:"Generate Cobertura Reports") {
    Ant.mkdir(dir:"${coverageReportDir}")
    Ant.taskdef (  classpathRef : 'cobertura.classpath', resource:"tasks.properties" )
    try {
        Ant.'cobertura-report'(destDir:"${coverageReportDir}", datafile:"${dataFile}", format:reportFormat){
            //load all these dirs independently so the dir structure is flattened,
            //otherwise the source isn't found for the reports
            fileset(dir:"${basedir}/grails-app/controllers")
            fileset(dir:"${basedir}/grails-app/domain")
            fileset(dir:"${basedir}/grails-app/services")
            fileset(dir:"${basedir}/grails-app/taglib")
            fileset(dir:"${basedir}/grails-app/utils")
            fileset(dir:"${basedir}/src/groovy")
            fileset(dir:"${basedir}/src/java")
        }
    }
    catch(Exception e) {
       event("StatusFinal", ["Compilation Error: ${e.message}"])
       exit(1)
    }
}

target('exit':"override exit") { code ->
    // Save the exit code.
    testAppExitCode = code
}

def flushCoverageData() {
	//per the Cobertura FAQ, force save of coverage data before JVM exits...
    //see http://cobertura.sourceforge.net/faq.html "Cobertura only writes the coverage data file when..."
    try {
        def saveClass = Class.forName('net.sourceforge.cobertura.coveragedata.ProjectData')
        def saveMethod = saveClass.getDeclaredMethod("saveGlobalProjectData", new Class[0])
        saveMethod.invoke(null,new Object[0]);
    } catch (Throwable t) {
		event("StatusFinal", ["Unable to flush Cobertura code coverage data."])
		exit(1)
	}
}