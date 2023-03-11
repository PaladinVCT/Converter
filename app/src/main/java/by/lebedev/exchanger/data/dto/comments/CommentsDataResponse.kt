//package com.funstore.easyfasting.data.dto.comments
//
//
//import com.squareup.moshi.Json
//import com.squareup.moshi.JsonClass
//
//@JsonClass(generateAdapter = true)
//data class CommentsDataResponse(
//    @Json(name = "caption")
//    val caption: CaptionResponse? = null,
//    @Json(name = "comment_count")
//    val commentCount: Int? = null,
//    @Json(name = "comments")
//    val comments: List<CommentResponse?>? = null,
//    @Json(name = "next_min_id")
//    val nextMinId: String? = null,
//    @Json(name = "status")
//    val status: String? = null
//)
//
//@JsonClass(generateAdapter = true)
//data class CaptionResponse(
//    @Json(name = "bit_flags")
//    val bitFlags: Int? = null,
//    @Json(name = "content_type")
//    val contentType: String? = null,
//    @Json(name = "created_at")
//    val createdAt: Int? = null,
//    @Json(name = "did_report_as_spam")
//    val didReportAsSpam: Boolean? = null,
//    @Json(name = "is_covered")
//    val isCovered: Boolean? = null,
//    @Json(name = "pk")
//    val pk: Long? = null,
//    @Json(name = "private_reply_status")
//    val privateReplyStatus: Int? = null,
//    @Json(name = "share_enabled")
//    val shareEnabled: Boolean? = null,
//    @Json(name = "status")
//    val status: String? = null,
//    @Json(name = "text")
//    val text: String? = null,
//    @Json(name = "type")
//    val type: Int? = null,
//    @Json(name = "user")
//    val userWhoPosted: UserResponse? = null,
//    @Json(name = "user_id")
//    val userId: Long? = null
//)
//
//@JsonClass(generateAdapter = true)
//data class CommentResponse(
//    @Json(name = "comment_like_count")
//    val commentLikeCount: Int? = null,
//    @Json(name = "created_at")
//    val createdAt: Int? = null,
//    @Json(name = "did_report_as_spam")
//    val didReportAsSpam: Boolean? = null,
//    @Json(name = "has_liked_comment")
//    val hasLikedComment: Boolean? = null,
//    @Json(name = "has_translation")
//    val hasTranslation: Boolean? = null,
//    @Json(name = "is_covered")
//    val isCovered: Boolean? = null,
//    @Json(name = "pk")
//    val pk: Long? = null,
//    @Json(name = "share_enabled")
//    val shareEnabled: Boolean? = null,
//    @Json(name = "status")
//    val status: String? = null,
//    @Json(name = "text")
//    val text: String? = null,
//    @Json(name = "user")
//    val userWhoCommented: UserResponse? = null,
//    @Json(name = "user_id")
//    val userId: Long? = null
//)
//
//@JsonClass(generateAdapter = true)
//data class UserResponse(
//    @Json(name = "full_name")
//    val fullName: String? = null,
//    @Json(name = "is_mentionable")
//    val isMentionable: Boolean? = null,
//    @Json(name = "is_private")
//    val isPrivate: Boolean? = null,
//    @Json(name = "is_verified")
//    val isVerified: Boolean? = null,
//    @Json(name = "latest_besties_reel_media")
//    val latestBestiesReelMedia: Int? = null,
//    @Json(name = "latest_reel_media")
//    val latestReelMedia: Int? = null,
//    @Json(name = "pk")
//    val pk: Long? = null,
//    @Json(name = "profile_pic_id")
//    val profilePicId: String? = null,
//    @Json(name = "profile_pic_url")
//    val profilePicUrl: String? = null,
//    @Json(name = "username")
//    val username: String? = null
//)
//
