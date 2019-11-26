package com.saasovation.agilepm.api

data class NewProductCommand(
  val tenantId: String,
  val productOwnerId: String,
  val name: String,
  val description: String
)

data class RequestProductDiscussionCommand(
  val tenantId: String,
  val productId: String
)

data class InitiateDiscussionCommand(
  val tenantId: String,
  val productId: String,
  val discussionId: String
)