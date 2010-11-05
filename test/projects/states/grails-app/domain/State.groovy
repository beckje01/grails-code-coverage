class State { 
	String name
	String postalCode
	
	def isMinnesota(){
		return 'MN' == postalCode
	}

	def isWisconsin(){
		return 'WI' == postalCode
	}
}	
