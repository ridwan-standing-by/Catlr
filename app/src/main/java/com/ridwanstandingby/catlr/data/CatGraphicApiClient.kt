package com.ridwanstandingby.catlr.data

import com.ridwanstandingby.catlr.BuildConfig
import com.ridwanstandingby.catlr.domain.CatGraphic
import com.ridwanstandingby.catlr.domain.CatGraphicMode
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class CatGraphicApiClient {

    private val client by lazy {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    suspend fun getCatGraphics(page: Int, graphicMode: CatGraphicMode): List<CatGraphic> =
        withContext(Dispatchers.IO) {
            client.get("https://api.thecatapi.com/v1/images/search") {
                header("x-api-key", BuildConfig.CAT_API_KEY)
                parameter("mime_types", graphicMode.toApiString())
                parameter("order", "random")
                parameter("limit", CatGraphicPagingSource.PAGE_SIZE)
                parameter("page", page)
            }.body<List<ApiCatGraphic>>().map { it.toDomain() }
        }
}

fun CatGraphicMode.toApiString() =
    when (this) {
        CatGraphicMode.IMAGES -> "jpg,png"
        CatGraphicMode.GIFS -> "gif"
        CatGraphicMode.BOTH -> "jpg,png,gif"
    }

@Serializable
data class ApiCatGraphic(
    val id: String,
    val url: String,
    val categories: List<ApiCatCategory>? = null
)

@Serializable
data class ApiCatCategory(
    val id: Int,
    val name: String
)

fun ApiCatGraphic.toDomain() =
    CatGraphic(
        id = id,
        url = url,
        categories = categories?.map { it.name }
    )