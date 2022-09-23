package com.example.educateverse

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
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
import java.lang.Exception

class StudentNewMessageActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mFirebaseDatabaseInstances: FirebaseFirestore? = null
    lateinit var cv: RecyclerView
    var fromName = ""
    var usn = ""
    var fromBr = ""
    var toName = ""
    var toID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_new_message)
        supportActionBar?.title = "Find Faculty"
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabaseInstances = FirebaseFirestore.getInstance()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        cv = findViewById<View>(R.id.rv_new_message) as RecyclerView
        loadData()
    }

    private fun loadData() {
        var i = intent
        fromName = i.getStringExtra("fromName").toString()
        usn = i.getStringExtra("fromID").toString()
        fromBr = i.getStringExtra("fromBr").toString()
        mFirebaseDatabaseInstances?.collection("Faculty")?.whereEqualTo("branch", fromBr)
            ?.addSnapshotListener { snapshot, e ->
                run {

                    val adapter = GroupieAdapter()
                    cv.adapter = adapter
                    for (dc in snapshot!!.documentChanges) {
                        adapter.add(FacultyHolder(dc))
                    }
                }


            }
        /*val query: Query = FirebaseFirestore.getInstance().collection("Student").whereEqualTo("branch", fromBr)

        val options: FirestoreRecyclerOptions<Student?> = FirestoreRecyclerOptions.Builder<Student>().setQuery(query, Student::class.java).build()
        val adapter: FirestoreRecyclerAdapter<*, *> = object : FirestoreRecyclerAdapter<Student?, RecyclerView.ViewHolder?>(options) {


                override fun onCreateViewHolder(group: ViewGroup, i: Int): FacultyHolder {
                    // Using a custom layout called R.layout.message for each item, we create a new instance of the viewholder
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.user_row_new_msg, group, false)
                    return StudentNameHolderNewMsg(view)
                }


                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder,
                    position: Int,
                    model: Student
                ) {
                    (holder.itemView.findViewById<View>(R.id.new_msg_text) as TextView).setText("${model.name} (${model.usn})")

                    studUsername = "${model.name} (${model.usn})"
                    toID = model.usn
                    if (model.image != "") {
                        var url = URL(model.image)
                        var imageView =
                            (holder.itemView.findViewById<View>(R.id.new_msg_img) as ImageView)
                        Glide.with(this@NewMessageActivity).load(model.image)
                            .placeholder(R.drawable.person).error(R.drawable.person)
                            .override(80, 80).into(imageView)
                    }

                }

            }


        cv.layoutManager = LinearLayoutManager(this)
        cv.adapter = adapter
        adapter.startListening()


*/
    }

    fun chat(v: View?) {
        // toID=text_view_for_id.text.toString()
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
                    intent.putExtra("fromID", usn)
                    intent.putExtra("fromname", fromName)
                    startActivity(intent)
                    finish()
                }


            }
        /*
 */


    }
}
class FacultyHolder(val dc: DocumentChange) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_row_new_msg
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var name = dc.document.get("name").toString()
        var honorific = dc.document.get("honorific")


        viewHolder.itemView.new_msg_text.text = "$honorific $name"
        try {
            Picasso.get().load(dc.document.get("image").toString())
                .into(viewHolder.itemView.new_msg_img)
        } catch (e: Exception) {
            viewHolder.itemView.new_msg_img.setBackgroundResource(R.drawable.person)
        }
        viewHolder.itemView.text_view_for_id.text = dc.document.get("fid").toString()

    }
}