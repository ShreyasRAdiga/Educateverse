package com.example.educateverse

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fac_latest_message_row.view.*
import java.lang.Exception
import java.util.*

class AdminHomeActivity : AppCompatActivity() {
    var profImag=""
    var toName = ""
    var toID = ""
    var username=""
    lateinit var admrv: RecyclerView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    private var mAuth: FirebaseAuth?=null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth= FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        drawerLayout=findViewById(R.id.drawerLayout)
        navigationView=findViewById(R.id.nav_view)
        admrv=findViewById(R.id. admin_latest_rv)as RecyclerView
        toggle= ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var i=intent
        username=i.getStringExtra("username").toString()
        loaddata()
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){




                R.id.item1 -> {
                    var i= Intent(applicationContext,StudentRequest::class.java)
                    startActivity(i)

                }
                R.id.item2 -> {
                    var i= Intent(applicationContext,AddFaculty::class.java)
                    startActivity(i)
                }
                R.id.item3 -> {
                    var i= Intent(applicationContext,LogedOut::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }
                R.id.itema->{
                    var i= Intent(applicationContext,AdminAddAdminActivity::class.java)
                    startActivity(i)

                }
                R.id.items->{
                    var i= Intent(applicationContext,AdminStudentChat::class.java)
                    i.putExtra("username",username)
                    startActivity(i)
                }
                R.id.itemf->{
                    var i= Intent(applicationContext,AdminFacultyChat::class.java)
                    i.putExtra("username",username)
                    startActivity(i)
                }
                R.id.itemcgroup ->{
                    try {
                        mFirebaseDatabaseInstances?.collection("GroupingFac")?.get()
                            ?.addOnSuccessListener {
                                for (document in it.documents) {
                                    document.reference.delete()
                                }
                            }
                    }
                    catch(e: Exception){}
                    try {
                        mFirebaseDatabaseInstances?.collection("GroupingStud")?.get()
                            ?.addOnSuccessListener {
                                for (document in it.documents) {
                                    document.reference.delete()
                                }
                            }
                    }
                    catch(e: Exception){}
                    var i= Intent(applicationContext,AdminGroupCreateActivity::class.java)
                    i.putExtra("username",username)
                    startActivity(i)
                }

            }
            true

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
    fun loaddata(){
        var textV = (findViewById<View>(R.id.facNameDisp) as TextView)
        textV.text="Hello, $username"
        try {

            val query: Query? = FirebaseFirestore.getInstance().collection("LatestMessage")?.whereArrayContains("id",username)?.orderBy("timestamp",
                Query.Direction.DESCENDING)
            val chats: FirestoreRecyclerOptions<ChatActivity.LatestChatMessage?> = FirestoreRecyclerOptions.Builder<ChatActivity.LatestChatMessage>()
                .setQuery(query!!, ChatActivity.LatestChatMessage::class.java).build()
            val adapter: FirestoreRecyclerAdapter<*, *> = object :
                FirestoreRecyclerAdapter<ChatActivity.LatestChatMessage?, RecyclerView.ViewHolder?>(chats) {

                override fun onCreateViewHolder(group: ViewGroup, i: Int): ChatHolder {
                    val view: View = LayoutInflater.from(group.context).inflate(R.layout.fac_latest_message_row, group, false)
                    return ChatHolder(view)
                }

                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder,
                    position: Int,
                    chat: ChatActivity.LatestChatMessage
                ) {

                    if (chat.type == "oto") {
                        if (chat.doc == "text") {
                            (holder.itemView.findViewById<View>(R.id.text_view_for_id) as TextView).text =
                                chat.text
                        } else if (chat.doc == "image") {
                            (holder.itemView.findViewById<View>(R.id.text_view_for_id) as TextView).text =
                                "Image"
                        } else if (chat.doc == "pdf") {
                            (holder.itemView.findViewById<View>(R.id.text_view_for_id) as TextView).text =
                                "Document"
                        }
                        (holder.itemView.findViewById<View>(R.id.new_msg_text) as TextView).text =
                            chat.oname.toString()

                        mFirebaseDatabaseInstances?.collection("Student")
                            ?.whereEqualTo("name", chat.oname)
                            ?.addSnapshotListener { snapshot, e ->
                                if (snapshot != null) {
                                    for (dc in snapshot!!.documentChanges) {
                                        profImag = dc.document.get("image").toString()
                                    }
                                    if (profImag != "") {
                                        var imageView =
                                            (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                                        Picasso.get().load(profImag).into(imageView)
                                    } else {

                                    }
                                }

                            }
                        if (profImag == "") {
                            mFirebaseDatabaseInstances?.collection("Faculty")
                                ?.whereEqualTo("name", chat.oname)
                                ?.addSnapshotListener { snapshot, e ->
                                    if (snapshot != null) {
                                        for (dc in snapshot!!.documentChanges) {
                                            profImag =
                                                dc.document.get("image").toString()
                                        }
                                        if (profImag != "") {
                                            var imageView =
                                                (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                                            Picasso.get().load(profImag).into(imageView)
                                        }
                                    }
                                }
                        }


                        /* if(profImag!=""){

                        //var url = URL(profImag)
                        var imageView = (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                        Picasso.get().load(profImag).into(imageView)
                        //Glide.with(this@FacultyHomeActivity).load(profImag).placeholder(R.drawable.person).error(R.drawable.person).override(600, 600).into(imageView);
                    }*/
                    }

                }

            }
            admrv.layoutManager = LinearLayoutManager(this)
            admrv.adapter = adapter
            adapter.startListening()
        } catch (e: Exception) {
            //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Error In Loading The Data")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Error")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }
    }
    fun chat(v: View?) {
        if (v != null) {
            var flag=true

            toName = v.new_msg_text.text.toString()
            // Toast.makeText(this@FacultyHomeActivity,"$toName",Toast.LENGTH_LONG).show()
            var togrp=""
            togrp= toName.lowercase(Locale.getDefault())
            val docRef = mFirebaseDatabaseInstances?.collection("Groups")?.document(togrp!!)
            docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                val grp = documentSnapshot.toObject(Group::class.java) as Group?
                if (grp != null)//User Already exists
                {   toID=togrp
                    intentgroupchange(togrp)

                }
                else{    mFirebaseDatabaseInstances?.collection("Student")
                    ?.whereEqualTo("name", toName)?.addSnapshotListener { snapshot, e ->
                        if (snapshot != null) {
                            for (dc in snapshot!!.documentChanges) {
                                toID = dc.document.get("usn").toString()
                            }
                            if (toID != "") {
                                intentchange(toID)


                            }
                            else{
                                mFirebaseDatabaseInstances?.collection("Faculty")
                                    ?.whereEqualTo("name", toName)?.addSnapshotListener { snapshot, e ->
                                        if (snapshot != null) {
                                            for (dc in snapshot!!.documentChanges) {
                                                toID = dc.document.get("fid").toString()
                                            }
                                            if (toID != "") {
                                                intentchange(toID)
                                            }
                                            else{
                                                mFirebaseDatabaseInstances?.collection("Admin")
                                                    ?.whereEqualTo("username", toName)?.addSnapshotListener { snapshot, e ->
                                                        if (snapshot != null) {
                                                            for (dc in snapshot!!.documentChanges) {
                                                                toID = dc.document.get("username").toString()
                                                            }
                                                            if (toID != "") {
                                                                intentchange(toID)
                                                            }
                                                        }
                                                    }
                                            }
                                        }
                                    }
                            }
                        }

                    }

                }
            }
        }

    }

    fun intentgroupchange(togrp:String){
        var i = Intent(applicationContext, FacultyGroupChatActivity::class.java)
        i.putExtra("fromID", username)
        i.putExtra("fromname", username)
        i.putExtra("toID", togrp)
        i.putExtra("toname", toName)
        startActivity(i)

    }
    fun intentchange(toiD:String){
        var i = Intent(applicationContext, ChatActivity::class.java)
        i.putExtra("fromID", username)
        i.putExtra("fromname",username)
        i.putExtra("toID", toiD)
        i.putExtra("toname", toName)
        startActivity(i)
    }
}