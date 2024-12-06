import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message,
    val logprobs: String?,
    val finish_reason: String
)

@Serializable
data class Message(
    val role: String,
    val content: String,
    val refusal: String?
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
    val prompt_tokens_details: TokenDetails,
    val completion_tokens_details: TokenDetails
)

@Serializable
data class TokenDetails(
    val cached_tokens: Int? = null,
    val audio_tokens: Int? = null,
    val reasoning_tokens: Int? = null,
    val accepted_prediction_tokens: Int? = null,
    val rejected_prediction_tokens: Int? = null
)