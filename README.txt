h1. Test Code Coverage Plugin

This plugin will generate code coverage reports using Cobertura.

{note:title=Reporting problems}
Please create issues in JIRA if you encounter problems.

* [JIRA Grails-CodeCoverage|http://jira.codehaus.org/browse/GRAILSPLUGINS/component/13147]
* [SVN Grails-CodeCoverage|http://svn.codehaus.org/grails-plugins/grails-code-coverage/]
* [Author Homepage|http://www.piragua.com/about]
{note}


h3. To install:

h5. For Grails 1.1
{code}
grails install-plugin code-coverage
{code}

h5.  For Grails versions prior to version 1.1
You need to install version 0.9 of the plugin
{code}
grails install-plugin code-coverage 0.9
{code}

h3. To run:

{code}
grails test-app
{code}

If you are using a Grails version prior to 1.1, you need to use the provided script:

{code}
grails test-app-cobertura
{code}

h3. Command line options:
By default, the script will create HTML reports and place them in the tests/report/cobertura directory.  If you would prefer XML reports (e.g for a Continuous Integration server), specify the @-xml@ flag like this:

{code}
grails test-app -xml
{code}

The plugin does some post processing on the HTML reports to make the Controller action names appear in human readable form (like @Controller.list@ instead of @Controller.$_closure1@ ).  If you don't require this or it is too resource intensive, you can tell the plugin to skip post processing of HTML files by passing the @-nopost@ flag.

{code}
grails test-app -nopost
{code}

The default is to run coverage whenever tests are run, but there may be times you would like to bypass this default (for speed of test execution, for example).  You can do this by using the -nocoverage flag on the command line:

{code}
grails test-app -nocoverage
{code}

h3. Configuration Options:
You can add your own exclusions from the reports by adding an entry in your BuildConfig.groovy file like this:

{code}
coverage {
	exclusions = ["**/ExcludedController*", "**/excludedDir/**"]
}
{code}

The exclusions are by class name, and package, not path.  Here's some more examples, that will filter out jsecurity classes and richui classes, if you are running cobertura with webtest.:

{code}
//cobertura exclusions
coverage {
	exclusions = [
	              '**/de/andreasschmitt/richui/taglib/renderer/**',
	              '**/plugins/richui-0.4/src/groovy/de/andreasschmitt/richui/**',
	              '**/de/andreasschmitt/richui/image/**',
	              '**/org/jsecurity/**',
	              '**/org/jsecurity/grails/**',
	              '**/JsecDbRealm*',
	              '**/*TagLib*/**',
	              "**/*Tests*",
	              '**/JsecAuthBase*',
	              '**/JsecurityFilters*']
}
{code}

By default, the plugin excludes several items from the coverage results including:
{code}
        "**/*BootStrap*",
        "Config*",
        "**/*DataSource*",
        "**/*resources*",
        "**/*UrlMappings*",
        "**/*Tests*",
        "**/grails/test/**",
        "**/org/codehaus/groovy/grails/**",
        "**/PreInit*",
        "*GrailsPlugin*"
{code}

As of version 1.1.4, you can completely override the default list of exclusions and provide your own.

{code}
coverage {
	exclusionListOverride = [
        "**/grails/test/**",
        "*GrailsPlugin*"]
}
{code}

In addition, you can add directories to the default list for source files to include in the coverage reports.  For example, if you're using the Jsecurity plugin and want to see the source for files in the 'realms' directory in the reports, add the following to your BuildConfig.groovy:

{code}
coverage {
     // list of directories to search for source to include in coverage reports
     sourceInclusions = ['grails-app/realms']
}
{code}

h2. Release History:
1.1.5
* Change from using Config.groovy to BuildConfig.groovy to configure exclusions and custom properties
1.1.4
* support for Grails 1.1 RC2
* upgraded to Cobertura 1.9
* Removed TestAppCobertura script, plugin now utilizes the events framework to tie into the main Grails test-app script
* added ability to override exclusions through config.coverage.exclusionListOverride
* added ability to bypass generation of code coverage through the -nocoverage flag
1.1.0
* support for Grails 1.1
0.9
* added post processing of HTML reports to display human readable names for controller actions (Controller.list instead of Controller.$_closure1)
0.8.5
* Support for Grails 1.0.4 (deprecated Events.groovy and use _Events.groovy instead)
* added default exclusions for the Grails Testing Plugin (grails/test/** and org/codehaus/groovy/grails/**)
0.8.3
* add sourceInclusions config option
0.8.2
* updated the default codeCoverageExclusionList in TestAppCobertura.groovy to exclude 'Config' rather than '\*Config\*'
0.8:
* Added scriptEnv = "test" so you no longer need to specify the environment when running the test-app-cobertura script
* fixed a bug where project specific exclusions were not being loaded correctly


