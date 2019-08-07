// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.saasovation.agilepm.domain.model.product

import com.saasovation.agilepm.domain.model.ValueObject
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItem
import com.saasovation.agilepm.domain.model.tenant.TenantId
import com.saasovation.agilepm.util.empty
import io.vlingo.actors.Definition
import io.vlingo.actors.Stage
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.util.UUID

data class ProductId(val id: String) : ValueObject {
  companion object {
    val empty = ProductId(id = String.empty())
  }
}

interface ProductEvents {

  data class ProductDefined(
    val tenantId: TenantId,
    val productId: ProductId,
    val productOwnerId: ProductOwnerId,
    val name: String,
    val description: String)
    : DomainEvent() {

    companion object {

      fun fromDefine(tenantId: TenantId,
                     productId: ProductId,
                     productOwnerId: ProductOwnerId,
                     name: String,
                     description: String) =
        ProductDefined(
          tenantId = tenantId,
          productId = productId,
          productOwnerId = productOwnerId,
          name = name,
          description = description
        )
    }
  }

  data class ProductOwnerChanged(
    val tenantId: TenantId,
    val productId: ProductId,
    val productOwnerId: ProductOwnerId)
    : DomainEvent()

  data class ProductDiscussionRequested(
    val tenantId: TenantId,
    val productId: ProductId,
    val productOwnerId: ProductOwnerId,
    val name: String,
    val description: String,
    val discussionAvailability: DiscussionAvailability
  ) : DomainEvent()

}

interface Product {

  companion object {
    fun define(world: World, tenantId: TenantId, productOwnerId: ProductOwnerId, name: String, description: String): Pair<ProductId, Product> {
      val stage = world.stage()

      val productAddress = world.addressFactory().uniquePrefixedWith("product-")

      val productId = ProductId(productAddress.idString())

      val definition = Definition.has(
        ProductEntity::class.java, Definition.parameters(productId.id)
      )

      val product = stage.actorFor(
        Product::class.java,
        definition,
        productAddress
      )
      product.define(
        tenantId = tenantId,
        productId = productId,
        productOwnerId = productOwnerId,
        name = name,
        description = description
      )

      return Pair(productId, product)
    }

    fun find(world: World, productId: ProductId): Completes<Product> {

      val stage = world.stage()
      val addressFactory = world.addressFactory()

      return stage.actorOf(
        Product::class.java,
        addressFactory.from(productId.id)
      )

    }

  }

  fun changeProductOwner(productOwnerId: ProductOwnerId): Completes<State>
  fun define(tenantId: TenantId,
             productId: ProductId,
             productOwnerId: ProductOwnerId,
             name: String,
             description: String): Completes<State>

  fun requestProductDiscussion(discussionAvailability: DiscussionAvailability): Completes<Unit>

  data class State(
    val backlogItems: Set<BacklogItem>,
    val description: String,
    val productDiscussion: ProductDiscussion,
    val discussionInitiationId: String,
    val name: String,
    val productId: ProductId,
    val productOwnerId: ProductOwnerId,
    val tenantId: TenantId

  ) {

    data class ProductDiscussion(

      val availability: DiscussionAvailability,
      val descriptor: DiscussionDescriptor

    ) : ValueObject {
      companion object {
        fun fromAvailability(availability: DiscussionAvailability): ProductDiscussion {
          return ProductDiscussion(
            availability = availability,
            descriptor = DiscussionDescriptor.unavailable
          )
        }

        val empty = ProductDiscussion(
          availability = DiscussionAvailability.empty,
          descriptor = DiscussionDescriptor.empty
        )
      }
    }

    fun applyProductDefined(e: ProductEvents.ProductDefined): State =
      copy(
        tenantId = e.tenantId,
        productId = e.productId,
        productOwnerId = e.productOwnerId,
        name = e.name,
        description = e.description
      )

    fun applyProductOwnerChanged(e: ProductEvents.ProductOwnerChanged): State =
      copy(
        productOwnerId = e.productOwnerId
      )

    fun applyProductDiscussionRequested(e: ProductEvents.ProductDiscussionRequested): State {

      val productDiscussion = ProductDiscussion.fromAvailability(e.discussionAvailability)

      return copy(
        productDiscussion = productDiscussion
      )
    }

    companion object {
      val empty = State(

        backlogItems = setOf(),
        description = String.empty(),
        productDiscussion = ProductDiscussion.empty,
        discussionInitiationId = String.empty(),
        name = String.empty(),
        productId = ProductId.empty,
        productOwnerId = ProductOwnerId.empty,
        tenantId = TenantId.empty

      )
    }
  }
}
