package com.johny.mediaverse.data.model.podcast

import kotlinx.serialization.Serializable

@Serializable
data class PodcastDto(
    val audio_length_sec: Int,
    val country: String,
    val description: String,
    val earliest_pub_date_ms: Long,
    val email: String,
    val explicit_content: Boolean,
    val extra: ExtraDto,
    val genre_ids: List<Int>,
    val has_guest_interviews: Boolean,
    val has_sponsors: Boolean,
    val id: String,
    val image: String,
    val is_claimed: Boolean,
    val itunes_id: Long,
    val language: String,
    val latest_episode_id: String,
    val latest_pub_date_ms: Long,
    val listen_score: String,
    val listen_score_global_rank: String,
    val listennotes_url: String,
    val looking_for: LookingForDto,
    val publisher: String,
    val rss: String,
    val thumbnail: String,
    val title: String,
    val total_episodes: Int,
    val type: String,
    val update_frequency_hours: Int,
    val website: String?
)