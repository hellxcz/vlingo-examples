package com.saasovation.agilepm.domain.model.team

import com.saasovation.agilepm.domain.model.ValueObject
import com.saasovation.agilepm.domain.model.tenant.TenantId
import com.saasovation.agilepm.util.empty

data class TeamMemberId(val id: String, val tenantId: TenantId): ValueObject {
  companion object{
    val empty = TeamMemberId(id = String.empty(), tenantId = TenantId.empty)
  }
}