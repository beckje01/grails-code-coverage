class StateTests extends GroovyTestCase {

	def state

	void setUp(){
		state = new State()
	}
	
	void testSave(){
		state.name = 'Minnesota'
		state.postalCode = 'MN'
		
		assertNotNull(state.save())
	}
	
	void testList(){
		state.name = 'Wisconsin'
		state.postalCode = 'WI'
	
		assertNotNull(state.save())
		assertEquals(1, State.list().size())
	}
	
	void testIsMinnesota() {
		state.postalCode = 'NOT mn'
		assertFalse('should be false when state is not MN', state.isMinnesota())
		state.postalCode = 'MN'
		assertTrue('should be true when state is MN', state.isMinnesota())
	}

	void testIsWisconsin() {
		state.postalCode = 'NOT wi'
		assertFalse('should be false when state is not WI', state.isWisconsin())
		state.postalCode = 'WI'
		assertTrue('should be true when state is WI', state.isWisconsin())
	}
}
