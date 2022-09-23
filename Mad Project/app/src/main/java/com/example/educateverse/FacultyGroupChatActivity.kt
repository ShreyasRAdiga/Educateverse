package com.example.educateverse

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.GroupieAdapter
import kotlinx.android.synthetic.main.activity_faculty_chat.*
import java.util.*
import kotlin.collections.ArrayList

class FacultyGroupChatActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private val PICK_IMAGE_REQUEST = 11
    private val PICK_PDF_REQUEST = 21
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    var toID = ""
    var fromID = ""
    var toname = ""
    var fromname = ""
    var iduser=ArrayList<String>()
    var url = ""
    var docType = ""
    var nametext = ""
    val adapter = GroupieAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_group_chat)
        mAuth = FirebaseAuth.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        var i = intent
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        val button = findViewById<View>(R.id.more_options) as ImageButton
        button.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, button)
            popupMenu.menuInflater.inflate(R.menu.chat_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.image -> {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "SELECT PICTURE"),
                            PICK_IMAGE_REQUEST)

                    }

                    R.id.pdf -> {
                        val intent = Intent()
                        intent.type = "application/pdf"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "SELECT PDF"),
                            PICK_PDF_REQUEST)

                    }
                }
                true
            })
            popupMenu.show()

        }
        recyclerView_chat.adapter = adapter
        toname = intent.getStringExtra("toname").toString()
        toID = intent.getStringExtra("toID").toString()
        fromID = intent.getStringExtra("fromID").toString()
        fromname = intent.getStringExtra("fromname").toString()
        //id= intent.getStringExtra("id").
        supportActionBar?.title = toname
        listenForMessages()
        button_chat.setOnClickListener {
            performSendMessage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            docType = "image"

        } else if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            docType = "pdf"
        }
        if (filePath != null) {
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)
            val urlTask = uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    // Toast.makeText(this,downloadUri.toString(),Toast.LENGTH_LONG).show()
                    addUploadRecordToDb(downloadUri.toString(), docType)
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener {

            }
        } else {
            Toast.makeText(this, "PDF NOT UPLOADED", Toast.LENGTH_SHORT).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Pdf is not uploaded.")
                // positive button text and action
                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                        dialog, id ->
                })


            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Upload Error")
            // show alert dialog
            alert.show()

            //alert box code upto here

        }

    }

    fun addUploadRecordToDb(uri: String, docType: String) {
        //val db = FirebaseFirestore.getInstance()
        //  Toast.makeText(this,"inside..",Toast.LENGTH_LONG).show()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        val data = HashMap<String, Any>()
        url = uri
        performSendDoc(url, docType)
        //Toast.makeText(this,"{$pid,$pname,$cost,$url}",Toast.LENGTH_LONG).show()
    }

    fun performSendDoc(url: String, docT: String) {
        var key = "$fromID$toID${System.currentTimeMillis()}"
        val ref=mFirebaseDatabaseInstances?.collection("GroupMessages")?.document("$toID")?.collection("$toID")?.document(key)
        var chat = ChatMessage(key, url, fromID, toID, System.currentTimeMillis() / 1000, docT,fromname)
        ref?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$fromID$toID${System.currentTimeMillis()}"
                mFirebaseDatabaseInstances?.collection("GroupMessages")?.document("$toID")?.collection("$toID")?.document(key)?.set(chat)
            }

        }
        val lasterMessRef = mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
        lasterMessRef?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$toID"
                var latestChat = LatestChatMessage(url, fromID, toID, toname, System.currentTimeMillis() / 1000,iduser, docT, "grp")
                mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
                    ?.set(latestChat)
            }
        }

        editText_chat.text.clear()
        recyclerView_chat.scrollToPosition(adapter.itemCount)
    }

    private fun listenForMessages() {
        val docRef = mFirebaseDatabaseInstances?.collection("Groups")?.document(toID!!)
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            val grp = documentSnapshot.toObject(Group::class.java) as Group?
            if (grp != null)//User Already exists
            {
                iduser = grp.idusers

            } else {
            }
        }
        //val ref=FirebaseFirestore.getInstance().collection("Messages").whereEqualTo("fromID",fromID)
        mFirebaseDatabaseInstances?.collection("GroupMessages")?.document("$toID")
            ?.collection("$toID")?.orderBy("timestamp")?.addSnapshotListener { snapshot, e ->
                run {
                    if (e != null) {
                        //Toast.makeText(this, "ERROR: MESSAGE IS NOT SEND ", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Message is not send.")
                            // positive button text and action
                            .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                    dialog, id ->
                            })


                        // create dialog box
                        val alert = dialogBuilder.create()
                        // set title for alert dialog box
                        alert.setTitle("Message Error")
                        // show alert dialog
                        alert.show()

                        //alert box code upto here

                    }

                    for (dc in snapshot!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            nametext=dc.document.get("fromName").toString()
                            if (dc.document.get("doc") == "text") {
                                if (dc.document.get("fromID").toString().equals(fromID)) {
                                    adapter.add(ChatFromItem(dc.document.get("text").toString(), "You"))
                                } else {
                                    adapter.add(ChatToItem(dc.document.get("text").toString(), nametext))
                                }
                            } else if (dc.document.get("doc") == "image") {
                                if (dc.document.get("fromID").toString().equals(fromID)) {
                                    var image = dc.document.get("text").toString()
                                    adapter.add(ImageFromItem(image, "You"))
                                } else {
                                    var image = dc.document.get("text").toString()
                                    adapter.add(ImageToItem(image, nametext))
                                }
                            }
                        }
                    }
                }
            }
    }
    class ChatMessage(
        val id: String,
        val text: String,
        val fromID: String,
        val toID: String,
        val timestamp: Long,
        val doc: String,
        val fromName:String
    ) {
        constructor() : this("", "", "", "", -1, "","")
    }

    class LatestChatMessage(
        val text: String,
        val fromID: String,
        val toID: String,
        val oname: String,
        val timestamp: Long,
        val id: ArrayList<String>,
        val doc: String,
        val type: String
    ) {
        constructor() : this("", "", "", "", -1, ArrayList<String>(), "", "")
    }

    fun performSendMessage() {
        val text = editText_chat.text.toString()
        var key = "$fromID$toID${System.currentTimeMillis()}"
        val ref=mFirebaseDatabaseInstances?.collection("GroupMessages")?.document("$toID")?.collection("$toID")?.document(key)
        if (text != "") {
            var chat = ChatMessage(key, text, fromID, toID, System.currentTimeMillis() / 1000, "text",fromname)
            ref?.get()?.addOnSuccessListener { documentSnapshot ->
                run {
                    key = "$fromID$toID${System.currentTimeMillis()}"
                    mFirebaseDatabaseInstances?.collection("GroupMessages")?.document("$toID")?.collection("$toID")?.document(key)?.set(chat)
                }

            }

            /* key = "$fromID $toID"
        val lasterMessRef = mFirebaseDatabaseInstances?.collection("LatestMessages")?.document(key)
        lasterMessRef?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$fromID $toID"
                var id=ArrayList<String>()
                id.add("$fromID")
                var latestChat = LatestChatMessage(text, fromID, toID, toname, System.currentTimeMillis() / 1000, id,"text","oto")
                mFirebaseDatabaseInstances?.collection("LatestMessages")?.document(key)?.set(latestChat)
            }
        }
        key = "$toID $fromID"

        val lasterMessRefToRef = mFirebaseDatabaseInstances?.collection("LatestMessages")?.document(key)
        lasterMessRefToRef?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$toID $fromID"
                var id=ArrayList<String>()
                id.add("$toID")
                var latestChat = LatestChatMessage(text, fromID, toID, fromname, System.currentTimeMillis() / 1000, id,"text","oto")
                mFirebaseDatabaseInstances?.collection("LatestMessages")?.document(key)
                    ?.set(latestChat)
            }
        }*/
            val lasterMessRef = mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
            lasterMessRef?.get()?.addOnSuccessListener { documentSnapshot ->
                run {
                    key = "$toID"
                    var latestChat = LatestChatMessage(text, fromID, toID, toname, System.currentTimeMillis() / 1000, iduser, "text", "grp")
                    mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
                        ?.set(latestChat)
                }
            }
            editText_chat.text.clear()
            recyclerView_chat.scrollToPosition(adapter.itemCount)
        }
    }

    fun setupdummydata() {
        val adapter = GroupieAdapter()
        adapter.add(ChatFromItem("From message here checking", ""))
        adapter.add(ChatToItem("To msg checking here  working orr not lorem40 ", ""))

        recyclerView_chat.setAdapter(adapter)
    }
}