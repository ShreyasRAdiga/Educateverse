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
import java.util.*

class FacultyHomeActivity : AppCompatActivity() {
    var username = ""
    var FID = ""
    var FBr = ""
    var name = ""
    var toID = ""
    var toName = ""
    var ActualName = ""
    var image=""
    var profImag=""
    lateinit var facrv: RecyclerView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    private var mAuth: FirebaseAuth? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_home)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        drawerLayout = findViewById(R.id.drawerLayout2)
        navigationView = findViewById(R.id.nav_view_stud)
        facrv=findViewById(R.id. fac_latest_rv)as RecyclerView
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loaddata()
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {



                R.id.itemp -> {
                    var i = Intent(applicationContext, FacultyProfileActivity::class.java)
                    i.putExtra("FID", FID)
                    startActivity(i)

                }

                R.id.iteml -> {

                    var i = Intent(applicationContext, LogedOut::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }
                R.id.item2 -> {
                    var i = Intent(applicationContext, NewMessageActivity::class.java)
                    i.putExtra("FID", FID)
                    i.putExtra("username", username)
                    i.putExtra("name", name)
                    i.putExtra("FBr", FBr)
                    startActivity(i)
                }
                R.id.itemf ->{
                    var i= Intent(applicationContext,FacultyChatFacultyActivity::class.java)
                    i.putExtra("FID",FID)
                    i.putExtra("name",ActualName)
                    startActivity(i)

                }

            }
            true

        }
        //listenForLatestMessages()
        //setUpDummyRows()

    }

    /*class lastestMessageRow(val dc: DocumentChange, val image: String) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var text = dc.document.get("text").toString()
            var name = dc.document.get("oname").toString()
            var toID = dc.document.get("toID").toString()

            viewHolder.itemView.text_view_for_id.text = text
            viewHolder.itemView.new_msg_text.text = name
            viewHolder.itemView.toId.text = toID
            if (image != "") {
                Picasso.get().load(image).into(viewHolder.itemView.new_msg_img)
            }
        }

        override fun getLayout(): Int {
            return R.layout.fac_latest_message_row
        }

    }*/
    /*fun listenForLatestMessages() {
        val ref = mFirebaseDatabaseInstances?.collection("LatestMessages")
        if (ref != null) {

            ref.addSnapshotListener { snapshot, error ->
                run {
                    val adapter = GroupieAdapter()
                    fac_latest_rv.adapter = adapter
                    for (dc in snapshot!!.documentChanges) {
                        try {
                            mFirebaseDatabaseInstances?.collection("Student")!!.document(dc.document.get("toID").toString()).get()
                                .addOnSuccessListener { result ->
                                    val stud = result.toObject(Student::class.java)
                                    if (stud== null) {
                                        try {
                                            mFirebaseDatabaseInstances?.collection("Faculty")!!.document(dc.document.get("toID").toString()).get()
                                                .addOnSuccessListener { result ->
                                                    val fac = result.toObject(Faculty::class.java)
                                                    if (fac== null) {

                                                    } else {
                                                       val image=URL(fac.image)
                                                        adapter.add(lastestMessageRow(dc,image))

                                                    }
                                                }
                                        }


                                        catch(e:Exception)
                                        {
                                            Toast.makeText(this, "UNEXPECTED ERROR", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                      val image= URL(stud.image)
                                        adapter.add(lastestMessageRow(dc,image))

                                        }
                                    }
                                }


                        catch(e:Exception)
                        {
                            Toast.makeText(this, "UNEXPECTED ERROR", Toast.LENGTH_LONG).show()
                        }



                    }

                }
            }
        }
    }
  /*  fun setUpDummyRows(){
        val adapter=GroupieAdapter()
        adapter.add(LastestMessageRow())
        adapter.add(LastestMessageRow())
        adapter.add(LastestMessageRow())
        adapter.add(LastestMessageRow())
        fac_latest_rv.adapter=adapter
    }*/*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }

    fun loaddata() {
        var i = intent
        var u = i.getStringExtra("username")
        if (u != null) {
            username = u
        }
        try {
            mFirebaseDatabaseInstances?.collection("Faculty")!!.document(username.toString()).get().addOnSuccessListener { result ->
                val fac = result.toObject(Faculty::class.java)
                if (fac == null) {
                    //Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Error in loading data.")
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

                } else {
                    var textV = (findViewById<View>(R.id.facNameDisp) as TextView)
                    name = "${fac.honorific} ${fac.name}"
                    textV.setText("Hello, $name")
                    ActualName = fac.name
                    FID = fac.FID
                    FBr = fac.branch
                    val query: Query? = FirebaseFirestore.getInstance().collection("LatestMessage")?.whereArrayContains("id",FID)?.orderBy("timestamp",
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
                            else if(chat.type=="grp"){
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
                                var imageView = (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                                imageView.setBackgroundResource(R.drawable.ic_group)
                                /*  (holder.itemView.findViewById<View>(R.id.new_msg_text)as TextView).setOnClickListener{
                                      var txt=(findViewById<View>(R.id.new_msg_text)as TextView).text.toString()
                                      Toast.makeText(this@FacultyHomeActivity,"$txt",Toast.LENGTH_LONG).show()
                                  }*/
                            }

                        }

                    }
                    facrv.layoutManager = LinearLayoutManager(this)
                    facrv.adapter = adapter
                    adapter.startListening()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "ERROR IN LOADING THE DATA", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Error in loading data.")
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
        i.putExtra("fromID", FID)
        i.putExtra("fromname", ActualName)
        i.putExtra("toID", togrp)
        i.putExtra("toname", toName)
        startActivity(i)

    }
    fun intentchange(toiD:String){
        var i = Intent(applicationContext, ChatActivity::class.java)
        i.putExtra("fromID", FID)
        i.putExtra("fromname", ActualName)
        i.putExtra("toID", toiD)
        i.putExtra("toname", toName)
        startActivity(i)
    }
}
class ChatHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val oname: TextView
    val text: TextView

    init {
        oname = itemView.findViewById(R.id.new_msg_text)
        text = itemView.findViewById(R.id.text_view_for_id)
    }
}