// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.saasovation.agilepm.domain.model.product

import com.saasovation.agilepm.domain.model.tenant.TenantId
import io.vlingo.common.Completes
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.EventSourced.registerConsumer

class ProductEntity(val productId: String) : Product, EventSourced() {

  private var state: Product.State

  init {
    state = Product.State.empty
  }

  companion object {
    init {
      val thisClass = ProductEntity::class.java

      registerConsumer(thisClass, ProductEvents.ProductDefined::class.java, ProductEntity::applyProductDefined)
      registerConsumer(thisClass, ProductEvents.ProductOwnerChanged::class.java, ProductEntity::applyProductOwnerChanged)
    }
  }

  override fun streamName(): String {
    return streamNameFrom(":", state.tenantId.id, state.productId.id)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <S> snapshot(): S {
    return if (currentVersion() % 100 == 0) {
      state as S
    } else null as S
  }

  override fun define(tenantId: TenantId, productId: ProductId, productOwner: ProductOwner, name: String, description: String): Completes<Product.State> {
    apply(
      ProductEvents.ProductDefined.fromDefine(
        tenantId = tenantId,
        productId = productId,
        productOwner = productOwner,
        name = name,
        description = description
      ))

    return completes()
  }

  private fun applyProductDefined(e: ProductEvents.ProductDefined) {
    state = state.applyProductDefined(e)
  }

  override fun changeProductOwner(productOwner: ProductOwner): Completes<Product.State> {
    apply(ProductEvents.ProductOwnerChanged(state.tenantId, state.productId, productOwner))

    return completes()
  }

  private fun applyProductOwnerChanged(e: ProductEvents.ProductOwnerChanged) {
    state = state.applyProductOwnerChanged(e)
  }
}




