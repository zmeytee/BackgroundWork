package ru.vladimir.tilikov.backgroundworkx.networking

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface FileApi {

    @GET
    suspend fun getFile(
        @Url url: String
    ): ResponseBody
}