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
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_faculty_chat.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.image_from_row.view.*
import kotlinx.android.synthetic.main.image_from_row.view.name_from
import kotlinx.android.synthetic.main.image_to_row.view.*
import kotlinx.android.synthetic.main.image_to_row.view.name_to
import kotlinx.android.synthetic.main.pdf_from_row.view.*
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth?=null
    private val PICK_IMAGE_REQUEST = 11
    private val PICK_PDF_REQUEST = 21
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore?=null
    var toID=""
    var fromID=""
    var toname=""
    var fromname=""
    var url=""
    var docType=""
    val adapter= GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_chat)
        mAuth= FirebaseAuth.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        var i=intent
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        //setSupportActionBar(toolbar:Toolbar)
        val button=findViewById<View>(R.id.more_options)as ImageButton
        button.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this,button)
            popupMenu.menuInflater.inflate(R.menu.chat_options,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener{ item ->
                when(item.itemId) {
                    R.id.image ->{
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)

                    }

                    R.id.pdf ->{
                        val intent= Intent()
                        intent.type = "application/pdf"
                        intent.action= Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent,"SELECT PDF"),PICK_PDF_REQUEST)

                    }
                }
                true
            })
            popupMenu.show()

        }
        recyclerView_chat.adapter=adapter
        toname=intent.getStringExtra("toname").toString()
        toID= intent.getStringExtra("toID").toString()
        fromID= intent.getStringExtra("fromID").toString()
        fromname=intent.getStringExtra("fromname").toString()
        if(toID.length<5)
        {
            supportActionBar?.title=toname
        }
        else{
            supportActionBar?.title="$toID ($toname)"
        }
        // setupdummydata()
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
            docType="image"

        } else if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            docType="pdf"
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
                    addUploadRecordToDb(downloadUri.toString(),docType)
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener {

            }
        } else {
            //Toast.makeText(this, "PDF NOT UPLOADED", Toast.LENGTH_SHORT).show()

            //alert box code

            val dialogBuilder = AlertDialog.Builder(this)

            // set message of alert dialog
            dialogBuilder.setMessage("Pdf not uploaded.")
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

    fun addUploadRecordToDb(uri: String,docType:String){
        //val db = FirebaseFirestore.getInstance()
        //  Toast.makeText(this,"inside..",Toast.LENGTH_LONG).show()
        mFirebaseDatabaseInstances= FirebaseFirestore.getInstance()
        val data = HashMap<String, Any>()
        url = uri
        performSendDoc(url,docType)
        //Toast.makeText(this,"{$pid,$pname,$cost,$url}",Toast.LENGTH_LONG).show()
    }
    fun performSendDoc(url:String,docT:String){
        var key="$fromID$toID${System.currentTimeMillis()}"
        val docRef = mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$fromID")?.collection("$toID")?.document(key)
        val toRef=mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$toID")?.collection("$fromID")?.document(key)
        var chat = ChatMessage(key, url, fromID, toID, System.currentTimeMillis() / 1000, docT)
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$fromID$toID${System.currentTimeMillis()}"
                //var chat = ChatMessage(key, text, fromID, toID, System.currentTimeMillis() / 1000)
                mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$fromID")?.collection("$toID")?.document(key)?.set(chat)
            }

        }
        toRef?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$fromID$toID${System.currentTimeMillis()}"
                mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$toID")?.collection("$fromID")?.document(key)?.set(chat)
            }

        }


        key = "$fromID $toID"
        val lasterMessRef = mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
        lasterMessRef?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$fromID $toID"
                var id=ArrayList<String>()
                id.add("$fromID")
                var latestChat = LatestChatMessage(url, fromID, toID, toname, System.currentTimeMillis() / 1000, id,docT,"oto")
                mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)?.set(latestChat)
            }
        }
        key = "$toID $fromID"

        val lasterMessRefToRef = mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
        lasterMessRefToRef?.get()?.addOnSuccessListener { documentSnapshot ->
            run {
                key = "$toID $fromID"
                var id=ArrayList<String>()
                id.add("$toID")
                var latestChat = LatestChatMessage(url, fromID, toID, fromname, System.currentTimeMillis() / 1000, id,docT,"oto")
                mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
                    ?.set(latestChat)
            }
        }
        editText_chat.text.clear()
        recyclerView_chat.scrollToPosition(adapter.itemCount)
    }

    private fun listenForMessages() {
        //val ref=FirebaseFirestore.getInstance().collection("Messages").whereEqualTo("fromID",fromID)
        try {
            val ref = FirebaseFirestore.getInstance().collection("UserMessages").document(toID)
                .collection(fromID)
            ref.addSnapshotListener { snapshot, e ->
                run {
                    if (e != null) {
                        //Toast.makeText(this, "ERROR: MESSAGE IS NOT SEND ", Toast.LENGTH_LONG).show()

                        //alert box code

                        val dialogBuilder = AlertDialog.Builder(this)

                        // set message of alert dialog
                        dialogBuilder.setMessage("Message is not send.")
                            // positive button text and action
                            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                            })


                        // create dialog box
                        val alert = dialogBuilder.create()
                        // set title for alert dialog box
                        alert.setTitle("Message Error")
                        // show alert dialog
                        alert.show()

                        //alert box code upto here

                    }

                    /*  if (snapshot != null) {
                    for (dc in snapshot.getDocumentChanges()){

                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                dc.toString()

                            }
                            DocumentChange.Type.MODIFIED -> {
                            }
                            DocumentChange.Type.REMOVED -> {
                            }
                        }
                    }
                }*/
                    for (dc in snapshot!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            /* if(dc.document.get("toID").toString().equals(toID))
                            adapter.add(ChatToItem(dc.document.get("text")as String))
                        else*/

                            if (dc.document.get("doc") == "text") {
                                if (dc.document.get("fromID").toString().equals(fromID)) {
                                    adapter.add(
                                        ChatFromItem(
                                            dc.document.get("text").toString(),
                                            "You"
                                        )
                                    )
                                } else {
                                    adapter.add(
                                        ChatToItem(
                                            dc.document.get("text").toString(),
                                            toname
                                        )
                                    )
                                }
                            } else if (dc.document.get("doc") == "image") {
                                if (dc.document.get("fromID").toString().equals(fromID)) {
                                    var image = dc.document.get("text").toString()
                                    adapter.add(ImageFromItem(image, "You"))
                                } else {
                                    var image = dc.document.get("text").toString()
                                    adapter.add(ImageToItem(image, toname))
                                }
                            } else if (dc.document.get("doc") == "pdf") {
                                if (dc.document.get("fromID").toString().equals(fromID)) {
                                    var pdf = dc.document.get("text").toString()
                                    adapter.add(PDFFromItem(pdf, "You"))
                                } else {
                                    //var image=dc.document.get("text").toString()
                                    // adapter.add(ImageToItem(image,toname))
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(e: RuntimeException){
            Toast.makeText(this, "WELCOME!!!", Toast.LENGTH_SHORT).show()

        }
    }
    class ChatMessage(val id:String,val text:String,val fromID:String,val toID:String, val timestamp: Long,val doc:String){
        constructor():this("","","","",-1,"")
    }
    class LatestChatMessage(val text:String,val fromID:String,val toID:String,val oname:String ,val timestamp: Long,val id:ArrayList<String>,val doc:String,val type:String){
        constructor():this("","","","",-1,ArrayList<String>(),"","")
    }

    fun performSendMessage(){
        val text = editText_chat.text.toString()
        var key="$fromID$toID${System.currentTimeMillis()}"
        val docRef = mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$fromID")
            ?.collection("$toID")?.document(key)
        val toRef=mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$toID")
            ?.collection("$fromID")?.document(key)

        // val lasterMessRefToRef=mFirebaseDatabaseInstances?.collection("LatestMessages")?.document(key)
        if(text!="") {
            var chat = ChatMessage(key, text, fromID, toID, System.currentTimeMillis() / 1000, "text")
            docRef?.get()?.addOnSuccessListener { documentSnapshot ->
                run {
                    key = "$fromID$toID${System.currentTimeMillis()}"
                    //var chat = ChatMessage(key, text, fromID, toID, System.currentTimeMillis() / 1000)
                    mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$fromID")
                        ?.collection("$toID")?.document(key)?.set(chat)
                }

            }
            toRef?.get()?.addOnSuccessListener { documentSnapshot ->
                run {
                    key = "$fromID$toID${System.currentTimeMillis()}"
                    mFirebaseDatabaseInstances?.collection("UserMessages")?.document("$toID")
                        ?.collection("$fromID")?.document(key)?.set(chat)
                }

            }


            key = "$fromID $toID"
            val lasterMessRef = mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
            lasterMessRef?.get()?.addOnSuccessListener { documentSnapshot ->
                run {
                    key = "$fromID $toID"
                    var id=ArrayList<String>()
                    id.add("$fromID")
                    var latestChat = LatestChatMessage(text, fromID, toID, toname, System.currentTimeMillis() / 1000, id,"text","oto")
                    mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)?.set(latestChat)
                }
            }
            key = "$toID $fromID"

            val lasterMessRefToRef = mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
            lasterMessRefToRef?.get()?.addOnSuccessListener { documentSnapshot ->
                run {
                    key = "$toID $fromID"
                    var id=ArrayList<String>()
                    id.add("$toID")
                    var latestChat = LatestChatMessage(text, fromID, toID, fromname, System.currentTimeMillis() / 1000, id,"text","oto")
                    mFirebaseDatabaseInstances?.collection("LatestMessage")?.document(key)
                        ?.set(latestChat)
                }
            }
            editText_chat.text.clear()
            recyclerView_chat.scrollToPosition(adapter.itemCount)
        }
    }
    fun setupdummydata(){
        val adapter = GroupieAdapter()
        adapter.add(ChatFromItem("From message here checking",""))
        adapter.add(ChatToItem("To msg checking here  working orr not lorem40 ",""))

        recyclerView_chat.setAdapter(adapter)
    }
}
class ChatToItem(val text:String,val name:String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_to_row.text=text
        viewHolder.itemView.name_to.text=name
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
class ChatFromItem(val text:String,val name:String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_from_row.text=text
        viewHolder.itemView.name_from.text=name
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ImageToItem(val image:String,val name:String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.name_to.text=name
        try {
            Picasso.get().load(image).into(viewHolder.itemView.imageto)
        } catch (e: Exception) {
            //viewHolder.itemView.imageto.setBackgroundResource(R.drawable.person)
        }
    }

    override fun getLayout(): Int {
        return R.layout.image_to_row
    }
}
class ImageFromItem(val image:String,val name:String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.name_from.text=name
        try {
            Picasso.get().load(image).into(viewHolder.itemView.imagefrom)
        } catch (e: Exception) {
            viewHolder.itemView.imagefrom.setBackgroundResource(R.drawable.person)
        }
    }

    override fun getLayout(): Int {
        return R.layout.image_from_row
    }
}
class PDFFromItem(val pdf:String,val name:String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.name_from.text=name
        try {
            var webview=viewHolder.itemView.web_from
            webview.getSettings().setJavaScriptEnabled(true)
            webview.loadUrl("$pdf")
            //Picasso.get().load(image).into(viewHolder.itemView.imagefrom)
        } catch (e: Exception) {
            viewHolder.itemView.imagefrom.setBackgroundResource(R.drawable.person)
        }
    }

    override fun getLayout(): Int {
        return R.layout.pdf_from_row
    }
}