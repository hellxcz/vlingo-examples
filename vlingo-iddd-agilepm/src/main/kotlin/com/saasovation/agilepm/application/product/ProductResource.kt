package com.saasovation.agilepm.application.product

import com.saasovation.agilepm.api.NewProductCommand
import com.saasovation.agilepm.api.RequestProductDiscussionCommand
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability
import com.saasovation.agilepm.domain.model.product.Product
import com.saasovation.agilepm.domain.model.product.ProductId
import com.saasovation.agilepm.domain.model.product.ProductOwnerId
import com.saasovation.agilepm.domain.model.tenant.TenantId
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.http.Response
import io.vlingo.http.ResponseHeader.Location
import io.vlingo.http.ResponseHeader.headers
import io.vlingo.http.ResponseHeader.of
import io.vlingo.http.resource.Resource
import io.vlingo.http.resource.ResourceBuilder.post
import io.vlingo.http.resource.ResourceBuilder.resource

class ProductResource
constructor(
  private val world: World
) {
  companion object {
    val ROOT_URL = "/product"
  }

  val stage = world.stage()

  fun newProduct(cmd: NewProductCommand): Completes<Response> {

    val tenantId = TenantId(cmd.tenantId)

    val productOwner = ProductOwnerId(
      tenantId = tenantId,
      username = cmd.productOwnerId
    )

    val (productId, product) = Product.define(
      world = world,
      tenantId = tenantId,
      productOwnerId = productOwner,
      name = cmd.name,
      description = cmd.description
    )

    return Completes.withSuccess(
      Response.of(
        Response.Status.Created,
        headers(of(Location, urlLocation(productId)))
      )
    )
  }

  fun newProductWithDiscussion() {
    TODO("product.define is missing discussion creation")
  }

  fun requestDiscussion(cmd: RequestProductDiscussionCommand): Completes<Response> {

    val productId = ProductId(cmd.productId)

    return Product
      .find(world, productId)
      .andThenTo { it.requestProductDiscussion(requestDiscussionIfAvailable()) }
      .andThenTo {
        Completes.withSuccess(
          Response.of(Response.Status.Ok)
        )
      }
      .otherwise { Response.of(Response.Status.NotFound, urlLocation(productId)) }

  }

  private fun requestDiscussionIfAvailable(): DiscussionAvailability {
    var availability = DiscussionAvailability.ADD_ON_NOT_ENABLED

    val enabled = true // TODO: determine add-on enabled

    if (enabled) {
      availability = DiscussionAvailability.REQUESTED
    }

    return availability
  }

  fun routes(): Resource<*> =
    resource("Product resource",
      post("$ROOT_URL")
        .body(NewProductCommand::class.java)
        .handle(this::newProduct)
    )

  private fun urlLocation(productId: ProductId): String {
    return ROOT_URL + "/" + productId.id
  }

}