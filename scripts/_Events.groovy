dataFile = "cobertura.ser"

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
        "*GrailsPlugin*"]


eventTestPhasesStart = {
    event("StatusUpdate", ["Instrumenting classes for coverage"])

    ant.delete(file: "${dataFile}")

    if (config.codeCoverageExclusionListOverride) {
        codeCoverageExclusionList = config.codeCoverageExclusionListOverride
    }

    if (config.coverage.exclusions) {
        codeCoverageExclusionList += config.coverage.exclusions
    }

    defineCoberturaPathAndTasks()
    instrumentClasses()
}

eventTestPhasesEnd = {
    defineCoberturaPathAndTasks()
    flushReportData()
    createCoverageReports()
    replaceClosureNamesInReports()

    event("StatusFinal", ["Cobertura Code Coverage Complete (view reports in: ${coverageReportDir})"])
}

def createCoverageReports() {
    coverageReportDir = "${config.grails.testing.reports.destDir ?: testReportsDir}/cobertura"

    ant.mkdir(dir: "${coverageReportDir}")

    coverageReportFormats = ['html']
    if (argsMap.xml) {
        coverageReportFormats << 'xml'
    }

    coverageReportFormats.each {reportFormat ->
        ant.'cobertura-report'(destDir: "${coverageReportDir}", datafile: "${dataFile}", format: reportFormat) {
            //load all these dirs independently so the dir structure is flattened,
            //otherwise the source isn't found for the reports
            fileset(dir: "${basedir}/grails-app/controllers")
            fileset(dir: "${basedir}/grails-app/domain")
            fileset(dir: "${basedir}/grails-app/services")
            fileset(dir: "${basedir}/grails-app/taglib")
            fileset(dir: "${basedir}/grails-app/utils")
            fileset(dir: "${basedir}/src/groovy")
            fileset(dir: "${basedir}/src/java")
            if (config.coverage?.sourceInclusions) {
                config.coverage.sourceInclusions.each {
                    fileset(dir: "${basedir}/${it}")
                }
            }
        }
    }
}

def defineCoberturaPathAndTasks() {
    ant.path(id: "cobertura.classpath") {
        fileset(dir: "${codeCoveragePluginDir}/lib") {
            include(name: "*.jar")
        }
    }

    ant.taskdef(classpathRef: 'cobertura.classpath', resource: "tasks.properties")
}

def replaceClosureNamesInReports() {
    if (!argsMap.nopost){
        def startTime = new Date().time
        replaceClosureNames(grailsApp?.controllerClasses)
        def endTime = new Date().time
        println "Done with post processing reports in ${endTime - startTime}ms"
    }
}

def replaceClosureNames(artefacts) {
    artefacts?.each {artefact ->
        def closures = [:]
        artefact.reference.propertyDescriptors.each {propertyDescriptor ->
            def closureClassName = artefact.getPropertyOrStaticPropertyOrFieldValue(propertyDescriptor.name, Closure)?.class?.name
            if (closureClassName) {
                // the name in the reports is sans package; subtract the package name
                def nameToReplace = closureClassName - "${artefact.packageName}."

                ant.replace(dir: "${coverageReportDir}",
                        token: ">${nameToReplace}<",
                        value: ">${artefact.shortName}.${propertyDescriptor.name}<") {
                    include(name: "**/*${artefact.fullName}.html")
                    include(name: "frame-summary*.html")
                }
            }
        }
    }
}

def instrumentClasses() {
    cleanCompiledSources()
    compile()
    try {
        ant.'cobertura-instrument'(datafile: "${dataFile}") {
            fileset(dir: classesDirPath) {
                include(name: "**/*.class")
                codeCoverageExclusionList.each {pattern ->
                    exclude(name: pattern)
                }
            }
        }
    }
    catch (Exception e) {
        event("StatusFinal", ["Error instrumenting classes: ${e.message}"])
        exit(1)
    }
}

def flushReportData() {
    try {
        net.sourceforge.cobertura.coveragedata.ProjectData.saveGlobalProjectData()
    } catch (Exception e) {
        event("StatusError", ["""
--------------------------------------------
***********WARNING*************
Unable to flush code coverage data.  
This usually happens when tests don't actually test anything;
e.g. none of the instrumented classes were exercised by tests!
--------------------------------------------
"""])
    }
}
