package com.saasovation.agilepm.domain.model.discussion

enum class DiscussionAvailability {
  __EMPTY,
  ADD_ON_NOT_ENABLED,
  FAILED,
  NOT_REQUESTED,
  REQUESTED,
  READY;

  fun isReady(): Boolean  = false
  fun isRequested(): Boolean = false

  companion object {

    val empty = DiscussionAvailability.__EMPTY

  }
}