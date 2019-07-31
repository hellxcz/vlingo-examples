// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.saasovation.agilepm.domain.model.product

import com.saasovation.agilepm.domain.model.tenant.TenantId
import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.util.UUID

data class ProductId(val id: String) {
  companion object {
    val empty = ProductId(id = "")
  }
}

interface ProductEvents {

  data class ProductDefined(
    val tenantId: TenantId,
    val productId: ProductId,
    val productOwner: ProductOwner,
    val name: String,
    val description: String)
    : DomainEvent() {

    companion object {

      fun fromDefine(define: Product.Companion.Define) =
        ProductDefined(
          tenantId = define.tenantId,
          productId = define.productId,
          productOwner = define.productOwner,
          name = define.name,
          description = define.description
        )
    }
  }

  data class ProductOwnerChanged(
    val tenantId: TenantId,
    val productId: ProductId,
    val productOwner: ProductOwner)
    : DomainEvent()

}

interface Product {

  companion object {
    fun define(stage: Stage, tenantId: TenantId, productOwner: ProductOwner, name: String, description: String): Pair<ProductId, Product> {

      val product = stage.actorFor(Product::class.java, ProductEntity::class.java)
      val productId = ProductId(UUID.randomUUID().toString())

      product.define(
        Define(
          tenantId = tenantId,
          productId = productId,
          productOwner = productOwner,
          name = name,
          description = description
        )
      )

      return Pair(productId, product)
    }

    data class Define(
      val tenantId: TenantId,
      val productId: ProductId,
      val productOwner: ProductOwner,
      val name: String,
      val description: String
    )
  }

  fun changeProductOwner(productOwner: ProductOwner): Completes<State>
  fun define(define: Define): Completes<State>

  data class State(
    val tenantId: TenantId,
    val productId: ProductId,
    val productOwner: ProductOwner,
    val name: String,
    val description: String) {

    fun applyProductDefined(e: ProductEvents.ProductDefined): State =
      copy(
        tenantId = e.tenantId,
        productId = e.productId,
        productOwner = e.productOwner,
        name = e.name,
        description = e.description
      )

    fun applyProductOwnerChanged(e: ProductEvents.ProductOwnerChanged): State =
      copy(
        productOwner = e.productOwner
      )

    companion object {
      val empty = State(
        tenantId = TenantId.empty,
        productId = ProductId.empty,
        productOwner = ProductOwner.empty,
        name = "",
        description = ""
      )
    }
  }
}
