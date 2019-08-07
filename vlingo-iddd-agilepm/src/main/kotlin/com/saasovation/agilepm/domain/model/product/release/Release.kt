package com.saasovation.agilepm.domain.model.product.release

import com.saasovation.agilepm.domain.model.ValueObject
import com.saasovation.agilepm.util.empty

data class ReleaseId(val id: String) : ValueObject {
  companion object {
    val empty = ReleaseId(id = String.empty())
  }
}