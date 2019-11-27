package com.saasovation.agilepm.domain.model

import com.saasovation.agilepm.domain.model.product.Product
import com.saasovation.agilepm.util.EventSourcedEx
import io.vlingo.actors.Definition
import io.vlingo.actors.World

interface BB {
  companion object{
    fun define(world: World, id: String){
      
      val state = world.stage()
      
      val address = world.addressFactory().uniquePrefixedWith("bb-")
      
      val definition = Definition.has(
        BBEntity::class.java, Definition.parameters(id)
      )
      
    }
  }
  
  interface Command {
    
    data class DoSome(
      val payload: String
    ) : Command
    
  }
  
  fun define()
  
  fun accept(command: Command)
  
  data class State(
    val behavior: Behavior
    
  ) {
    
    companion object {
      val empty = State(behavior = Behavior.IN_DEFINITION)
    }
    
    enum class Behavior{
      IN_DEFINITION,
      IN_PROGRESS,
      FINISHED
    }
    
  }
  
}

class BBEntity(val id: String) : BB, EventSourcedEx<BB.State>() {
  
  private var state: BB.State = BB.State.empty
  
  override fun define() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
  
  override fun accept(command: BB.Command) {
    when(state.behavior){
      BB.State.Behavior.IN_DEFINITION -> inDefinitionAccept(command)
      BB.State.Behavior.IN_PROGRESS -> ""
      BB.State.Behavior.FINISHED -> ""
      
      else -> "not known one"
    }
  }
  
  fun inDefinitionAccept(command: BB.Command){
    when(command){
      is BB.Command.DoSome -> command.payload // and apply event
    }
  }
  
  fun inProgressAccept(command: BB.Command){
    when(command){
      is BB.Command.DoSome -> command.payload // and apply in different way
    }
  }
  
  
  override fun snapshotEx(): BB.State? {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
  
  override fun streamName(): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
  
  
}