package com.fandino.githubuserrecyclerview.data.network

import com.fandino.githubuserrecyclerview.Config
import com.fandino.githubuserrecyclerview.data.network.response.GithubUserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubSearchUserEndpoints {

//    https://api.github.com/search/users?q=ch3rno&page=1&per_page=1000
    @GET(Config.SEARCH_USERS)
    fun getSearchedUsers(
        @Query(Config.Q) username: String,
        @Query(Config.PAGE) page: Int,
        @Query(Config.PER_PAGE) perPage: Int = 100
    ): Call<GithubUserResponse>
}