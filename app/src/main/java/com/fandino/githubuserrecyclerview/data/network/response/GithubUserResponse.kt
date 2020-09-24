package com.fandino.githubuserrecyclerview.data.network.response


import com.fandino.githubuserrecyclerview.data.db.User
import com.google.gson.annotations.SerializedName

data class GithubUserResponse(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val userList: List<User>
)