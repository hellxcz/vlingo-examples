package com.saasovation.agilepm.domain.model.discussion

import com.saasovation.agilepm.domain.model.ValueObject
import com.saasovation.agilepm.util.empty

data class DiscussionDescriptor(val id: String) : ValueObject {
  companion object {
    val empty = DiscussionDescriptor(id = String.empty())
  }

}