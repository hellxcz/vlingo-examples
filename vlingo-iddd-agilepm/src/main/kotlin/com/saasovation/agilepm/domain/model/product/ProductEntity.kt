// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.saasovation.agilepm.domain.model.product

import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor
import com.saasovation.agilepm.domain.model.tenant.TenantId
import com.saasovation.agilepm.util.EventSourcedEx
import io.vlingo.common.Completes

class ProductEntity(val productId: String) : Product, EventSourcedEx<Product.State>() {

  private var state: Product.State

  init {
    state = Product.State.empty
  }

  companion object {
    init {
      val thisClass = ProductEntity::class.java

      registerConsumerEx(thisClass, ProductEvents.ProductDefined::class.java) { state = state.applyProductDefined(it) }
      registerConsumerEx(thisClass, ProductEvents.ProductOwnerChanged::class.java) {state = state.applyProductOwnerChanged(it)}
      registerConsumerEx(thisClass, ProductEvents.ProductDiscussionRequested::class.java) {state = state.applyProductDiscussionRequested(it)}
    }
  }

  override fun streamName(): String {
    return streamNameFrom(":", state.tenantId.id, state.productId.id)
  }

  override fun snapshotEx(): Product.State? {
    return when {
      currentVersion() % 100 == 0 -> state
      else -> null
    }
  }

  override fun define(tenantId: TenantId, productId: ProductId, productOwnerId: ProductOwnerId, name: String, description: String): Completes<Product.State> {
    apply(
      ProductEvents.ProductDefined.fromDefine(
        tenantId = tenantId,
        productId = productId,
        productOwnerId = productOwnerId,
        name = name,
        description = description
      )
    )

    return completes()
  }

  override fun changeProductOwner(productOwnerId: ProductOwnerId): Completes<Product.State> {
    apply(
      ProductEvents.ProductOwnerChanged(
        tenantId = state.tenantId,
        productId = state.productId,
        productOwnerId = productOwnerId
      )
    )

    return completes()
  }

  override fun requestProductDiscussion(discussionAvailability: DiscussionAvailability): Completes<Unit> {

    if (!state.productDiscussion.availability.isReady()) {

      apply(
        ProductEvents.ProductDiscussionRequested(
          tenantId = state.tenantId,
          productId = state.productId,
          productOwnerId = state.productOwnerId,
          name = state.name,
          description = state.description,
          discussionAvailability = discussionAvailability
        )
      )
    }

    return completes()
  }
  
  override fun initiateDiscussion(descriptor: DiscussionDescriptor): Completes<Unit> {
    
    if (state.productDiscussion.availability.isRequested()) {
      
      apply(
        ProductEvents.ProductDiscussionInitiated(
          tenantId = state.tenantId,
          productId = state.productId,
          productDiscussion = state.productDiscussion
        )
      )
      
    }
    
    return completes()
  }
}




