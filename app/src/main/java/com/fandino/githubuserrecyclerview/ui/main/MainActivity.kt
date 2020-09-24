package com.fandino.githubuserrecyclerview.ui.main

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fandino.githubuserrecyclerview.R
import com.fandino.githubuserrecyclerview.data.network.GithubSearchUserEndpoints
import com.fandino.githubuserrecyclerview.data.network.ServiceBuilder
import com.fandino.githubuserrecyclerview.data.network.response.GithubUserResponse
import com.fandino.githubuserrecyclerview.ui.recycler.EndlessOnScrollListener
import com.fandino.githubuserrecyclerview.ui.recycler.UserAdapter
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    // lateinit -> in java, it's like declaring a null object, but initializing it soon
    private lateinit var userAdapter: UserAdapter

    // store last submitted username
    private var username: String = ""

    // Page as API parameter
    private var currentPage: Int = 1

    // Need to know whether we need to load more or not
    private var totalResult: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI
        initUi()
    }

    private fun initUi() {
        // Initialize Recycler View
        main_recycler_user.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            userAdapter = UserAdapter()
            adapter = userAdapter
            addOnScrollListener(scrollData())
        }

        // Run onBtnSearchClicked(view) when you press "Enter" (the search icon)
        main_edit_username.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                onBtnSearchClicked()
                return@OnKeyListener true
            }
            false
        })

        main_btn_search.setOnClickListener {
            onBtnSearchClicked()
        }
    }

    // Endless scrolling onScrollListener
    private fun scrollData(): EndlessOnScrollListener {
        return object : EndlessOnScrollListener() {
            override fun onLoadMore() {
                // check if userList have already reached totalResult. if yes, then load more
                if (userAdapter.itemCount < totalResult){
                    currentPage++
                    getGithubSearchUser(username, currentPage)
                }
            }
        }
    }

    private fun onBtnSearchClicked() {
        if (main_edit_username.text.isNullOrBlank()) {
            main_layout_username.error = "Please fill username"
        }
        else {
            main_layout_username.error = null

            // reset the number of Page as API parameter
            currentPage = 1

            // hide keyboard
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(main_edit_username.windowToken, 0)

            // Hide Warning text view, show Recycler View
            main_txt_noUser.visibility = View.GONE
            main_recycler_user.visibility = View.VISIBLE

            // initialized username
            username = main_edit_username.text.toString()

            // Call using retrofit
            getGithubSearchUser(
                username,
                currentPage
            )
        }
    }

    // show Dialog when returning error
    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Error")
            setMessage(message)
            show()
        }
    }

    private fun getGithubSearchUser(username: String, page: Int) {
        main_progress_bar.visibility = View.VISIBLE

        val searchUserRequest = ServiceBuilder.buildService(GithubSearchUserEndpoints::class.java)
        val searchUserCall = searchUserRequest.getSearchedUsers(username, page)

        searchUserCall.enqueue(object : Callback<GithubUserResponse> {
            override fun onResponse(
                call: Call<GithubUserResponse>,
                response: Response<GithubUserResponse>
            ) {
                main_progress_bar.visibility = View.INVISIBLE
                if (response.isSuccessful) {
                    if (response.body()!!.totalCount == 0) {
                        // show that no user found
                        main_txt_noUser.visibility = View.VISIBLE
                        main_txt_totalResult.visibility = View.GONE
                        main_recycler_user.visibility = View.GONE
                    } else {
                        // post is to avoid overlap in notifyDataSetChanged() if the process was too fast
                        main_recycler_user.post{
                            userAdapter.addUserToList(response.body()!!.userList, page)
                        }
                        totalResult = response.body()!!.totalCount

                        if (page == 1) {
                            main_txt_totalResult.visibility = View.VISIBLE
                            val userOrUsers = if (totalResult==1) "user" else "users" // grammatical s a t i s f a c t i o n
                            main_txt_totalResult.text = "Total Result: $totalResult $userOrUsers"
                        }
                    }
                }

                // throw error message if response does not return isSuccessful
                else {
                    showDialog("Error code: ${response.code()}\nError message: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GithubUserResponse>, t: Throwable) {
                main_progress_bar.visibility = View.INVISIBLE

                // throw error message if on fail
                showDialog("Error on calling API.\nError message: ${t.message}")
            }
        })
    }
}