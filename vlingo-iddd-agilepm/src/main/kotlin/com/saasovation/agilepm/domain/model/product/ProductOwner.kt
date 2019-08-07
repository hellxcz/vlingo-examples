// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.saasovation.agilepm.domain.model.product

import com.saasovation.agilepm.domain.model.tenant.TenantId
import com.saasovation.agilepm.util.empty

data class ProductOwner(val tenantId: TenantId, val username: String){
  companion object{
    val empty = ProductOwner(
      tenantId = TenantId.empty,
      username = String.empty()
    )
  }
}
