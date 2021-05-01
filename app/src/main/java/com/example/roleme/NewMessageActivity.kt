package com.example.roleme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roleme.data.model.LoggedInUser
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = getString(R.string.select_user)
        val recycleview = findViewById<RecyclerView>(R.id.recycler_view_new_message)

        Log.d("New message", "***fetching users...")
        fetchUsers()
    }

    private fun fetchUsers() {
        val database_str = getString(R.string.database_url)
        var ref = FirebaseDatabase.getInstance(database_str).getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                Log.d("New message", "***adapter ok")

                snapshot.children.forEach {
                    val user = it.getValue(LoggedInUser::class.java)
                    if (user != null) {
                        Log.d("New message", "***Showing user $user")
                        adapter.add(UserItem(user))
                    }
                }

                var recycleview = findViewById<RecyclerView>(R.id.recycler_view_new_message)
                recycleview.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    class UserItem(val user: LoggedInUser): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.username_row).text = user.displayName
        }
        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }
    }
}