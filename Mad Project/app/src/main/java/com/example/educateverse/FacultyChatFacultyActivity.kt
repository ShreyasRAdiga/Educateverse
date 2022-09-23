package com.example.educateverse

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.GroupieAdapter
import kotlinx.android.synthetic.main.user_row_new_msg.view.*
import java.lang.Exception

class FacultyChatFacultyActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    lateinit var cv: RecyclerView
    var fromName = ""
    var fromId = ""
    var branch = ""
    var toName = ""
    var toID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_chat_faculty)
        supportActionBar?.title = "Find Faculty"
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        cv = findViewById<View>(R.id.rv_new_message) as RecyclerView

    }
    fun search(v: View?) {
        var i = intent
        fromName = i.getStringExtra("name").toString()
        fromId = i.getStringExtra("FID").toString()
        branch = (findViewById<View>(R.id.branch)as Spinner).selectedItem.toString()
        try {
            mFirebaseDatabaseInstances?.collection("Faculty")?.whereEqualTo("branch", branch)
                ?.addSnapshotListener { snapshot, e ->
                    run {

                        val adapter = GroupieAdapter()
                        cv.adapter = adapter
                        for (dc in snapshot!!.documentChanges) {
                            if(dc.document.get("fid").toString()==fromId){
                                continue
                            }
                            adapter.add(FacultyHolder(dc))
                        }
                    }


                }
        }
        catch (e: Exception){

        }
    }

    fun chat(v: View?) {
        val pattern = Regex("\\.")
        var res = ""
        if (v != null) {
            res = v.new_msg_text.text.toString()
        }
        val ans: List<String> = pattern.split(res)
        toName = ans[1].trim()
        mFirebaseDatabaseInstances?.collection("Faculty")?.whereEqualTo("name", toName)
            ?.addSnapshotListener { snapshot, e ->
                run {

                    for (dc in snapshot!!.documentChanges) {
                        toName = dc.document.get("name").toString()
                        toID = dc.document.get("fid").toString()
                    }
                    //Toast.makeText(this, "toName = $toName toID = $toID fromID = $usn fromname=$fromName",Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, ChatActivity::class.java)
                    intent.putExtra("toname", toName)
                    intent.putExtra("toID", toID)
                    intent.putExtra("fromID", fromId)
                    intent.putExtra("fromname", fromName)
                    startActivity(intent)
                    finish()
                }


            }

    }
}
