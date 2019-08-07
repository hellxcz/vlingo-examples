package com.saasovation.agilepm.util

import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.Sourced
import io.vlingo.symbio.Source

val emptyString = ""

val emptyInt = Int.MAX_VALUE

fun String.Companion.empty() = emptyString

fun Int.Companion.empty() = emptyInt

abstract class EventSourcedEx<SNAPSHOT> : EventSourced() {
  companion object {
    fun <SOURCED : Sourced<*>, SOURCE : Source<*>> registerConsumerEx(
      sourcedType: Class<SOURCED>,
      sourceType: Class<SOURCE>,
      consumer: SOURCED.(SOURCE) -> Unit
    ) {
      registerConsumer(sourcedType, sourceType, consumer)
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun <S> snapshot(): S {
    return snapshotEx() as S
  }

  abstract fun snapshotEx(): SNAPSHOT?

}