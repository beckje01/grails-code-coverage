            
class StateController {
    
    def index = { 
		redirect(action:list,params:params) 
	}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ stateList: State.list( params ) ]
    }

    def show = {
        def state = State.get( params.id )

        if(!state) {
            flash.message = "State not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ state : state ] }
    }

    def delete = {
        def state = State.get( params.id )
        if(state) {
            state.delete()
            flash.message = "State ${params.id} deleted"
            redirect(action:list)
        }
        else {
            flash.message = "State not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def state = State.get( params.id )

        if(!state) {
            flash.message = "State not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ state : state ]
        }
    }

    def update = {
        def state = State.get( params.id )
        if(state) {
            state.properties = params
	        if(!state.hasErrors() && state.save()) {
                flash.message = "State ${params.id} updated"
                redirect(action:show,id:state.id)
            }
            else {
                render(view:'edit',model:[state:state])
            }
        }
        else {
            flash.message = "State not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def state = new State()
        state.properties = params
        return ['state':state]
    }

    def save = {
        def state = new State(params)
        if(!state.hasErrors() && state.save()) {
            flash.message = "State ${state.id} created"
            redirect(action:show,id:state.id)
        }
        else {
            render(view:'create',model:[state:state])
        }
    }
}