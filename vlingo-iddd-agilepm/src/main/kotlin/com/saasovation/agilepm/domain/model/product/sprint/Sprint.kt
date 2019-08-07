package com.saasovation.agilepm.domain.model.product.sprint

import com.saasovation.agilepm.domain.model.ValueObject
import com.saasovation.agilepm.util.empty

data class SprintId(val id: String) : ValueObject {
  companion object {
    val empty = SprintId(id = String.empty())
  }
}