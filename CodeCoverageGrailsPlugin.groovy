
class CodeCoverageGrailsPlugin {
    def version = 0.4
	def dependsOn = [core:'1.0-final-SNAPSHOT'] 
	def author = "Mike Hugo"
	def title = "Generates Code Coverage reports"
	def description = """
		Creates Code Coverage reports for your code
	"""
	def documentation = "http://docs.codehaus.org/display/GRAILS/Test+Code+Coverage+Plugin"

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }
   
    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)		
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }
	                                      
    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }
	
    def onChange = { event ->
        // TODO Implement code that is executed when this class plugin class is changed  
        // the event contains: event.application and event.applicationContext objects
    }
                                                                                  
    def onApplicationChange = { event ->
        // TODO Implement code that is executed when any class in a GrailsApplication changes
        // the event contain: event.source, event.application and event.applicationContext objects
    }
}
