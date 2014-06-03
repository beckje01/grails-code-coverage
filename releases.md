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