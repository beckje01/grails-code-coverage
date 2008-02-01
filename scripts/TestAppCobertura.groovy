Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"  

includeTargets << new File ( "${grailsHome}/scripts/Package.groovy" )
includeTargets << new File ( "${grailsHome}/scripts/Bootstrap.groovy" )
includeTargets << new File ( "${grailsHome}/scripts/TestApp.groovy" )

pluginHome = codeCoveragePluginDir
reportFormat = 'html'

coverageReportDir = "${basedir}/test/reports/cobertura"

//TODO - would like this to work (put file in tmp dir), but cobertura creates a cobertura.ser in the running dir anyway...
//dataFile = "${grailsTmp}/cobertura.ser"
dataFile = "cobertura.ser"

target ('default': "Test App with Cobertura") {
    depends( classpath, checkVersion, configureProxy, packagePlugins )

    Ant.path(id: "cobertura.classpath"){
        fileset(dir:"${pluginHome}/lib"){
            include(name:"*.jar")
        }
    }

	if(args?.indexOf('-xml') >-1) {
		reportFormat = 'xml'
		args -= '-xml'        
	}

    cleanup()
    packageApp()
    compileTests()
    instrumentTests()
    testApp()

    //per the Cobertura FAQ, force save of coverage data before JVM exits...
    //see http://cobertura.sourceforge.net/faq.html "Cobertura only writes the coverage data file when..."
    try {
        String className = "net.sourceforge.cobertura.coveragedata.ProjectData";
        String methodName = "saveGlobalProjectData";
        Class saveClass = Class.forName(className);
        java.lang.reflect.Method saveMethod = saveClass.getDeclaredMethod(methodName, new Class[0]);
        saveMethod.invoke(null,new Object[0]);
    } catch (Throwable t) {}

    coberturaReport()
    Ant.delete(dir:testDirPath)
    event("StatusFinal", ["Cobertura Code Coverage Complete (view reports in: ${coverageReportDir})"])
}

target(cleanup:"Remove old files") {
    Ant.delete(file:"${dataFile}", quiet:true)
    Ant.delete(dir:testDirPath, quiet:true)
    Ant.delete(dir:coverageReportDir, quiet:true)
}

target(instrumentTests:"Instruments the compiled test cases") {
    Ant.taskdef (  classpath : 'cobertura.classpath', resource:"tasks.properties" )
    try {
        //for now, instrument classes in the same directory grails creates for testClasses
        //TODO - need to figure out how to put cobertura instrumented classes in different dir
        //and put that dir in front of testClasses in the classpath
        Ant.'cobertura-instrument' (datafile:"${dataFile}") {
            fileset(dir:testDirPath) {
                exclude(name:"**/*BootStrap*")
                exclude(name:"**/*Config*")
                exclude(name:"**/*DataSource*")
                exclude(name:"**/*resources*")
                exclude(name:"**/*UrlMappings*")
                exclude(name:"**/*Tests*")
                include(name:"**/*.class")
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
    Ant.taskdef (  classpath : 'cobertura.classpath', resource:"tasks.properties" )
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

target('exit':"override exit") { def code ->
    //ignore '0' exit code to bypass TestApp.groovy exiting, process any other return code (e.g. -1)
    if (0 != code){
        System.exit(code)
    }
}
