package com.saasovation.agilepm.domain.model.product.backlogitem

import com.saasovation.agilepm.domain.model.Entity
import com.saasovation.agilepm.domain.model.ValueObject
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor
import com.saasovation.agilepm.domain.model.product.ProductId
import com.saasovation.agilepm.domain.model.product.release.ReleaseId
import com.saasovation.agilepm.domain.model.product.sprint.SprintId
import com.saasovation.agilepm.domain.model.team.TeamMemberId
import com.saasovation.agilepm.domain.model.tenant.TenantId

import com.saasovation.agilepm.util.empty
import java.time.Instant
import java.time.LocalTime

data class BacklogItemId(val id: String) : ValueObject {
  companion object{
    val empty = BacklogItemId(id = String.empty())
  }
}

data class TaskId(val id: String) : ValueObject {
  companion object{
    val empty = TaskId(id = String.empty())
  }
}

interface BacklogItemEvents {



}

interface BacklogItem {

  data class State (

    val associatedIssueId: String,
    val backlogItemId: BacklogItemId,
    val businessPriority: BusinessPriority,
    val category: String,
    val discussion: BacklogItemDiscussion,
    val discussionInitiationId: String,
    val productId: ProductId,
    val releaseId: ReleaseId,
    val sprintId: SprintId,
    val status: BacklogItemStatus,
    val story: String,
    val storyPoints: StoryPoints,
    val summary: String,
    val tasks: Set<Task>,
    val tenantId: TenantId,
    val type: BacklogItemType

  ) {

    companion object {

      fun define(tenantId: TenantId,
                 productId: ProductId,
                 backlogItemId: BacklogItemId,
                 summary: String,
                 category: String,
                 type: BacklogItemType,
                 status: BacklogItemStatus,
                 storyPoints: StoryPoints): State =
        State(
          associatedIssueId = String.empty(),
          backlogItemId = backlogItemId,
          businessPriority = BusinessPriority.empty,
          category = category,
          discussion = BacklogItemDiscussion.empty,
          discussionInitiationId = String.empty(),
          productId = productId,
          releaseId = ReleaseId.empty,
          sprintId = SprintId.empty,
          status = status,
          story = String.empty(),
          storyPoints = storyPoints,
          summary = summary,
          tasks = setOf(),
          tenantId = tenantId,
          type = type
        )

    }

    data class BusinessPriority(
      val businessPriorityRatings: BusinessPriorityRatings
    ) : ValueObject {
      companion object {
        val empty = BusinessPriority(businessPriorityRatings = BusinessPriorityRatings.empty)
      }
    }

    data class BusinessPriorityRatings(
      val benefit: Int,
      val cost: Int,
      val penalty: Int,
      val risk: Int
    ) : ValueObject {
      companion object{
        val empty = BusinessPriorityRatings(
          benefit = Int.empty(),
          cost = Int.empty(),
          penalty = Int.empty(),
          risk = Int.empty()
        )
      }
    }

    data class BacklogItemDiscussion(

      val availability: DiscussionAvailability,
      val descriptor: DiscussionDescriptor

    ) : ValueObject {
      companion object{
        val empty = BacklogItemDiscussion(
          availability = DiscussionAvailability.empty,
          descriptor = DiscussionDescriptor.empty
        )
      }
    }

    enum class BacklogItemStatus {
      PLANNED,
      SCHEDULED,
      COMMITTED,
      DONE,
      REMOVED
    }

    enum class StoryPoints(val value: Int) {
      ZERO(0),
      ONE(1),
      TWO(2),
      THREE(3),
      FIVE(5),
      EIGHT(8),
      THIRTEEN(13),
      TWENTY(20),
      FORTY(40),
      ONE_HUNDRED(100)
    }

    data class Task(
      val backlogItemId: BacklogItemId,
      val description: String,
      val estimationLog: List<EstimationLogEntry>,
      val hoursRemaining: Int,
      val name: String,
      val status: TaskStatus,
      val taskId: TaskId,
      val tenantId: TenantId,
      val volunteer: TeamMemberId
      ) : Entity {

    }

    data class EstimationLogEntry(
      val date : Instant,
      val hoursRemaining : Int,
      val taskId : TaskId,
      val tenantId : TenantId
    ) : Entity {


      companion object{
        fun currentLogDate() : Instant =
          Instant.from(LocalTime.MIDNIGHT)
      }
    }

    enum class TaskStatus {
      NOT_STARTED,
      IN_PROGRESS,
      IMPEDED,
      DONE
    }

    enum class BacklogItemType {
      FEATURE,
      ENHANCEMENT,
      DEFECT,
      FOUNDATION,
      INTEGRATION
    }


  }
}