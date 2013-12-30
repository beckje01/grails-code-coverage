# Test Code Coverage Plugin

This plugin will generate code coverage reports using Cobertura.

## To run:

    grails test-app


If you are using a Grails version prior to 1.1, you need to use the provided script:

    grails test-app-cobertura


## Command line options:
By default, the script will create HTML reports and place them in the tests/report/cobertura directory.  If you would prefer XML reports (e.g for a Continuous Integration server), specify the @-xml@ flag like this:

    grails test-app -xml


The plugin does some post processing on the HTML reports to make the Controller action names appear in human readable form (like @Controller.list@ instead of @Controller.$_closure1@ ).  If you don't require this or it is too resource intensive, you can tell the plugin to skip post processing of HTML files by passing the @-nopost@ flag.

    grails test-app -nopost


The default is to run coverage whenever tests are run, but there may be times you would like to bypass this default (for speed of test execution, for example).  You can do this by using the -nocoverage flag on the command line:

    grails test-app -nocoverage


To force coverage to run (if you have set the enabledByDefault flag to false; see config options for details) you can turn it back on for a single test run using the folling command line option:

    grails test-app -coverage


## Configuration Options:

If you want to disable coverage by default, you can set the enabledByDefault config attribute equal to false

    coverage {
	    enabledByDefault = false
    }


You can add your own exclusions from the reports by adding an entry in your BuildConfig.groovy file like this:


    coverage {
	   exclusions = ["**/ExcludedController*", "**/excludedDir/**"]
    }


The exclusions are by class name, and package, not path.  Here's some more examples, that will filter out jsecurity classes and richui classes, if you are running cobertura with webtest.:


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

By default, the plugin excludes several items from the coverage results including:

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


As of version 1.1.4, you can completely override the default list of exclusions and provide your own.

    coverage {
	  exclusionListOverride = [
          "**/grails/test/**",
          "*GrailsPlugin*"]
    }


In addition, you can add directories to the default list for source files to include in the coverage reports.  For example, if you're using the Jsecurity plugin and want to see the source for files in the 'realms' directory in the reports, add the following to your BuildConfig.groovy:


    coverage {
      // list of directories to search for source to include in coverage reports
      sourceInclusions = ['grails-app/realms']
    }


By default, the Grails Code Coverage plugin removes the cobertura.ser file, if it exists, at the start of the test phase. If you want to keep the coverage results from a previous set of tests, say, your unit tests, and add the coverage results from a separate run of integration tests, you can now append to the cobertura.ser file. The coverage results will be from the combined runs:


    coverage {
      appendCoverageResults = true // This may not be generally useful but is available as an option
    }


or from the command line, the following will create combined results for the unit and integration tests:


    grails test-app :unit -coverage -xml
    grails test-app :integration -coverage -xml -append


## Release History:

* 1.2.6
  * Fix [GPCODECOVERAGE-50](http://jira.grails.org/browse/GPCODECOVERAGE-50) 
  * Fix [GPCODECOVERAGE-47](http://jira.grails.org/browse/GPCODECOVERAGE-47) - Thanks @noamt
* 1.1.6
  * Added config and command line arguments to disable code coverage by default
* 1.1.5
  * Change from using Config.groovy to BuildConfig.groovy to configure exclusions and custom properties
* 1.1.4
  * support for Grails 1.1 RC2
  * upgraded to Cobertura 1.9
  * Removed TestAppCobertura script, plugin now utilizes the events framework to tie into the main Grails test-app script
  * added ability to override exclusions through config.coverage.exclusionListOverride
  * added ability to bypass generation of code coverage through the -nocoverage flag
* 1.1.0
  * support for Grails 1.1
* 0.9
  * added post processing of HTML reports to display human readable names for controller actions (Controller.list instead of Controller.$_closure1)
* 0.8.5
  * Support for Grails 1.0.4 (deprecated Events.groovy and use _Events.groovy instead)
  * added default exclusions for the Grails Testing Plugin (grails/test/** and org/codehaus/groovy/grails/**)
* 0.8.3
  * add sourceInclusions config option
* 0.8.2
  * updated the default codeCoverageExclusionList in TestAppCobertura.groovy to exclude 'Config' rather than '\*Config\*'
* 0.8
  * Added scriptEnv = "test" so you no longer need to specify the environment when running the test-app-cobertura script
  * fixed a bug where project specific exclusions were not being loaded correctly




[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/beckje01/grails-code-coverage/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

