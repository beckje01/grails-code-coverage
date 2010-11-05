package sample.plugin

import grails.test.*

class MyControllerTests extends ControllerUnitTestCase {
    void testIndex() {
        controller.index()
        assertEquals '/index', redirectArgs.uri
    }
}
