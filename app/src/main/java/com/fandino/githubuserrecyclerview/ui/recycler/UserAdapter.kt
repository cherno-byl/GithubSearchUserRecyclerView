package com.fandino.githubuserrecyclerview.ui.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fandino.githubuserrecyclerview.R
import com.fandino.githubuserrecyclerview.data.db.User
import kotlinx.android.synthetic.main.row_user.view.*

class UserAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var users: List<User> = ArrayList()
    private var users = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                holder.bind(users.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun addUserToList(userList: List<User>, page: Int) {
        if (page==1) users.clear()
        users.addAll(userList)
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userImg = itemView.userRow_img_userPic
        val userName = itemView.userRow_txt_username

        fun bind(user: User) {
            val defaultRequestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(defaultRequestOptions)
                .load(user.avatarUrl)
                .into(userImg)

            userName.text = user.username
        }
    }
}