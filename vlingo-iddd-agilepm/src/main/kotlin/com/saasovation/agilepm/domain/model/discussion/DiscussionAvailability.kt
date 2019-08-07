package com.saasovation.agilepm.domain.model.discussion

enum class DiscussionAvailability {
  __EMPTY,
  ADD_ON_NOT_ENABLED,
  FAILED,
  NOT_REQUESTED,
  REQUESTED,
  READY;

  companion object {

    val empty = DiscussionAvailability.__EMPTY

  }
}