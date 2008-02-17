eventSetClasspath = { rootLoader ->
	if (System.properties.'cobertura.code.coverage') {
		new File("${pluginHome}/lib/cobertura").eachFile {
			rootLoader.addURL(it.toURL())
		}
	}
}