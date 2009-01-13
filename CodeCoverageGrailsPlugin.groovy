
class CodeCoverageGrailsPlugin {
    def version = '1.1.3'

	def environments = ['test']
	def scopes = [excludes:'war']
	
	def dependsOn = [core:'1.1 > *'] 
	def author = "Mike Hugo"
	def authorEmail = "mike@piragua.com"
	def title = "Generates Code Coverage reports"
	def description = """
		Creates Code Coverage reports for your code.
        Special thanks to Peter Ledbrook and Jeff Kunkle for contributions to this plugin.
	"""
	def documentation = "http://grails.org/Test+Code+Coverage+Plugin"
}
