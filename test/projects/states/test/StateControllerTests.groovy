import groovy.mock.interceptor.*

class StateControllerTests extends GroovyTestCase {
	
	def stateController
	def stateMock
	
	void setUp(){
    	stateController = new StateController()
		stateMock = new MockFor(State)
	}
	
	void testIndex(){
		stateController.index()
		assertEquals('/state/list', stateController.response.redirectedUrl)
	}

	void testIndexWithParams(){
		stateController.params.max=10
		stateController.index()
		assertEquals('/state/list?max=10', stateController.response.redirectedUrl)
	}

	void testList(){
		def EXPECTED_LIST = [1,2,3]
			
		stateMock.demand.list { params ->
			assert 10 == params.max
			return EXPECTED_LIST
		}

		stateMock.use{
			def model = stateController.list()
			assertEquals(EXPECTED_LIST, model.stateList)		
		}
	}

	void testListWithMaxParam(){
		def EXPECTED_LIST = [1,2,3]
		def EXPECTED_MAX = 5
		
		stateMock.demand.list { params ->
			assert EXPECTED_MAX == params.max
			return EXPECTED_LIST
		}

		stateMock.use{
			stateController.params.max = 5
			def model = stateController.list()
			assertEquals(EXPECTED_LIST, model.stateList)		
		}
	}

	void testShow() {
		def EXPECTED_ID = 5
		def EXPECTED_STATE = new State(id:EXPECTED_ID)
		stateMock.demand.get { id ->
			assert EXPECTED_ID == id
			return EXPECTED_STATE
		}

		stateMock.use {
		    stateController.params.id = EXPECTED_ID
		    def result = stateController.show()
		    assertEquals(EXPECTED_STATE, result.state)		
		}
	}

	void testShowStateNotFound() {
		def EXPECTED_ID = 5
		stateMock.demand.get { id ->
			assert EXPECTED_ID == id
			return null
		}
		
		stateMock.use {
	    	stateController.params.id = EXPECTED_ID
			stateController.show()
		    assertEquals('/state/list', stateController.response.redirectedUrl)
		    assertEquals("State not found with id ${EXPECTED_ID}", stateController.flash.message)		
		}
	}	

	void testDelete(){
		def EXPECTED_ID = 5

		stateMock.demand.get { id ->
			assert EXPECTED_ID == id
			return new State(id:id)
		}
		stateMock.demand.delete {}

		stateMock.use{
			stateController.params.id = EXPECTED_ID
			stateController.delete()
		    assertEquals('/state/list', stateController.response.redirectedUrl)
		    assertEquals("State ${EXPECTED_ID} deleted", stateController.flash.message)
		}
	}

	void testDeleteStateNotFound(){
		def EXPECTED_ID = 5

		stateMock.demand.get { id ->
			assert EXPECTED_ID == id
			return null
		}

		stateMock.use{
			stateController.params.id = EXPECTED_ID
			stateController.delete()
		    assertEquals('/state/list', stateController.response.redirectedUrl)
		    assertEquals("State not found with id ${EXPECTED_ID}", stateController.flash.message)
		}
	}
	
	void testEdit(){
		def EXPECTED_ID = 5
		def EXPECTED_STATE = new State(id:EXPECTED_ID)
		stateMock.demand.get { id ->
			assert EXPECTED_ID == id
			return EXPECTED_STATE
		}

		stateMock.use {
		    stateController.params.id = EXPECTED_ID
		    def result = stateController.edit()
		    assertEquals(EXPECTED_STATE, result.state)		
		}		
	}
	
	void testEditStateNotFound(){
		def EXPECTED_ID = 5
		stateMock.demand.get { id ->
			assert EXPECTED_ID == id
			return null
		}
	
		stateMock.use {
	    	stateController.params.id = EXPECTED_ID
			stateController.edit()
		    assertEquals('/state/list', stateController.response.redirectedUrl)
		    assertEquals("State not found with id ${EXPECTED_ID}", stateController.flash.message)		
		}	
	}

	void testUpdate(){
		def EXPECTED_ID = 5
		stateMock.demand.get { return new State(id:EXPECTED_ID) }
		stateMock.demand.setProperties {}
		stateMock.demand.hasErrors { return false }
		stateMock.demand.save { return true }
		stateMock.demand.getId {EXPECTED_ID}
		stateMock.use{
			stateController.params.id=EXPECTED_ID
			stateController.update()
		    assertEquals("/state/show/${EXPECTED_ID}", stateController.response.redirectedUrl)
		    assertEquals("State ${EXPECTED_ID} updated", stateController.flash.message)					
		}
	}

	void testUpdateHasErrors(){
		def EXPECTED_ID = 5
		stateMock.demand.get { return new State(id:EXPECTED_ID) }
		stateMock.demand.setProperties {}
		stateMock.demand.hasErrors { return true }
		stateMock.demand.getId {EXPECTED_ID}
		stateMock.use{
			stateController.params.id=EXPECTED_ID
			stateController.update()
		    assertEquals('/state/edit', stateController.modelAndView.viewName)
		    assertEquals(EXPECTED_ID, stateController.modelAndView.model.state.id)
		}
	}

	void testUpdateSaveFails(){
		def EXPECTED_ID = 5
		stateMock.demand.get { return new State(id:EXPECTED_ID) }
		stateMock.demand.setProperties {}
		stateMock.demand.hasErrors { return false }
		stateMock.demand.save { return false }
		stateMock.demand.getId {EXPECTED_ID}
		stateMock.use{
			stateController.params.id=EXPECTED_ID
			stateController.update()
		    assertEquals('/state/edit', stateController.modelAndView.viewName)
		    assertEquals(EXPECTED_ID, stateController.modelAndView.model.state.id)
		}
	}

	void testUpdateStateNotFound(){
		def EXPECTED_ID = 5
		stateMock.demand.get { return null }
		stateMock.use{
			stateController.params.id=EXPECTED_ID
			stateController.update()
		    assertEquals("/state/edit/${EXPECTED_ID}", stateController.response.redirectedUrl)
		    assertEquals("State not found with id ${EXPECTED_ID}", stateController.flash.message)					
		}
	}

	void testCreate(){
		def state = new State(name:'Minnesota', postalCode:'MN')
		
		stateController.params.name = state.name
		stateController.params.postalCode = state.postalCode
		def model = stateController.create()
		
		assertEquals(state.name, model.state.name)		
		assertEquals(state.postalCode, model.state.postalCode)		
	}

	void testSave(){
		def EXPECTED_ID = 5
		stateMock.demand.hasErrors { return false }
		stateMock.demand.save { return true }
		stateMock.demand.getId {EXPECTED_ID}
		stateMock.demand.getId {EXPECTED_ID}
		stateMock.use{
			stateController.params.id=EXPECTED_ID
			stateController.save()
		    assertEquals("/state/show/${EXPECTED_ID}", stateController.response.redirectedUrl)
		    assertEquals("State ${EXPECTED_ID} created", stateController.flash.message)					
		}
	}

	void testSaveHasErrors(){
		def EXPECTED_ID = 5
		stateMock.demand.hasErrors { return true }
		stateMock.demand.getId {EXPECTED_ID}
		stateMock.use{
			stateController.params.id=EXPECTED_ID
			stateController.save()

		    assertEquals('/state/create', stateController.modelAndView.viewName)
		    assertEquals(EXPECTED_ID, stateController.modelAndView.model.state.id)
		}
	}

	void testSaveFails(){
		def EXPECTED_ID = 5
		stateMock.demand.hasErrors { return false }
		stateMock.demand.save { return false }
		stateMock.demand.getId {EXPECTED_ID}
		stateMock.use{
			stateController.params.id=EXPECTED_ID
			stateController.save()

		    assertEquals('/state/create', stateController.modelAndView.viewName)
		    assertEquals(EXPECTED_ID, stateController.modelAndView.model.state.id)
		}
	}
}