package com.example.educateverse

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fac_latest_message_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class AdminFinilizeGroupActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    lateinit var rvf: RecyclerView
    lateinit var rvs: RecyclerView
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_finilize_group)
        supportActionBar?.title = "Finalize Group"
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        rvf = findViewById(R.id.rvf)
        rvs = findViewById(R.id.rvs)
        var i = intent
        username = i.getStringExtra("username").toString()

        loadData()
    }

    class facrow(val dc: DocumentChange) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var honorific = dc.document.get("honorific")
            var name = dc.document.get("name")
            viewHolder.itemView.new_msg_text.text = "$honorific $name"
            if (dc.document.get("image") != "") {
                Picasso.get().load(dc.document.get("image").toString())
                    .into(viewHolder.itemView.new_msg_img)
            }
        }

        override fun getLayout(): Int {
            return R.layout.user_row_new_msg
        }

    }

    class studrow(val dc: DocumentChange) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var usn = dc.document.get("usn")
            var name = dc.document.get("name")
            viewHolder.itemView.new_msg_text.text = "$name ($usn)"
            if (dc.document.get("image").toString() != "") {
                Picasso.get().load(dc.document.get("image").toString())
                    .into(viewHolder.itemView.new_msg_img)
            }
        }

        override fun getLayout(): Int {
            return R.layout.user_row_new_msg
        }

    }

    fun loadData() {
        val ref = mFirebaseDatabaseInstances?.collection("GroupingFac")
        if (ref != null) {
            ref.addSnapshotListener { snapshot, error ->
                run {
                    val adapter = GroupieAdapter()
                    rvf.adapter = adapter
                    for (dc in snapshot!!.documentChanges) {
                        adapter.add(facrow(dc))
                    }
                }
            }
        }
        val refs = mFirebaseDatabaseInstances?.collection("GroupingStud")
        if (refs != null) {
            refs.addSnapshotListener { snapshot, error ->
                run {
                    val adapter = GroupieAdapter()
                    rvs.adapter = adapter
                    for (dc in snapshot!!.documentChanges) {
                        adapter.add(studrow(dc))
                    }
                }
            }
        }
    }

    fun cancel(v: View) {
        var i = Intent(applicationContext, AdminHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra("username", username)
        startActivity(i)
    }

    fun create(v: View) {
        var name = (findViewById<View>(R.id.name) as EditText).text.toString()
        if (name == "") {
            //Toast.makeText(this, "ENTER GROUP NAME", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Enter The Group Name")
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

        } else if ((name.length < 8)) {
            //Toast.makeText(this, "GROUP NAME LENGTH SHOULD BE GREATER THAN 8 CHARACTERS", Toast.LENGTH_LONG).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Group Name Length Should Be Greater Than 8 Characters")
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
            var farray = ArrayList<String>()
            var sarray = ArrayList<String>()
            val ref = mFirebaseDatabaseInstances?.collection("GroupingFac")
            if (ref != null) {
                ref.addSnapshotListener { snapshot, error ->
                    run {
                        for (dc in snapshot!!.documentChanges) {
                            farray.add(dc.document.get("fid").toString())
                        }
                    }
                }
            }
            val refs = mFirebaseDatabaseInstances?.collection("GroupingStud")
            if (refs != null) {
                refs.addSnapshotListener { snapshot, error ->
                    run {
                        for (dc in snapshot!!.documentChanges) {
                            sarray.add(dc.document.get("usn").toString())
                        }
                    }
                }
            }
            val docRef = mFirebaseDatabaseInstances?.collection("Groups")?.document(name.lowercase(
                Locale.getDefault())!!)
            docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                val group = documentSnapshot.toObject(Group::class.java) as Group?
                if (group != null)//User Already exists
                {
                   // Toast.makeText(this, "ERROR: GROUP RECORD EXIST", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Group Record Exists")
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
                    var i = Intent(applicationContext, AdminHomeActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("username", username)
                    startActivity(i)

                    //alert box code upto here


                } else {
                    var jointList=ArrayList<String>()
                    jointList.addAll(farray)
                    jointList.addAll(sarray)
                    var u = Group(name,name.lowercase(Locale.getDefault()),false,farray,sarray,jointList)
                    mFirebaseDatabaseInstances?.collection("Groups")?.document(name.lowercase(Locale.getDefault())!!)?.set(u)
                    var latestChat = ChatActivity.LatestChatMessage("", "", name.lowercase(Locale.getDefault()), name, System.currentTimeMillis() / 1000,jointList,
                        "text",
                        "grp")
                    mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(name.lowercase(
                        Locale.getDefault()))
                        ?.set(latestChat)
                    Toast.makeText(this, " SUCCESS: GROUP CREATED ", Toast.LENGTH_LONG).show()

                    //alert box code

                    val dialogBuilder = AlertDialog.Builder(this)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Group Created")
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                dialog, id ->
                        })


                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Success")
                    // show alert dialog
                    alert.show()
                    var i = Intent(applicationContext, AdminHomeActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra("username", username)
                    startActivity(i)

                    //alert box code upto here

                }
            }

        }
    }
}
class Group{
    var name:String=""
    var id:String=""
    var property:Boolean=false
    var flist=ArrayList<String>()
    var slist=ArrayList<String>()
    var idusers=ArrayList<String>()
    constructor(name:String,id:String,property:Boolean,flist:ArrayList<String>,slist:ArrayList<String>,idusers:ArrayList<String>)  {
        this.name=name
        this.id=id
        this.property=property
        this.flist=flist
        this.slist=slist
        this.idusers=idusers
    }
    constructor()
}