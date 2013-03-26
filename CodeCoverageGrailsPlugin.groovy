
class CodeCoverageGrailsPlugin {
    def version = '1.2.6'

	def grailsVersion = '1.2 > *'

	def pluginExcludes = [
		"grails-app/conf/mavenInfo.groovy"
	]
	
	def dependsOn = [core:'1.2 > *'] 
	def author = "Mike Hugo"
	def authorEmail = "mike@piragua.com"
	def developers = [
        [ name: "Jeff Beck" ]
    ]

	def title = "Generates Code Coverage reports"
	def description = """
		Creates Code Coverage reports for your code.
        Special thanks to Peter Ledbrook and Jeff Kunkle for contributions to this plugin.
	"""
	def documentation = "https://github.com/beckje01/grails-code-coverage"
	def license = "APACHE"
	def issueManagement = [system: "github", url: "https://github.com/beckje01/grails-code-coverage/issues"]
	def scm = [url: "https://github.com/beckje01/grails-code-coverage"]
}
