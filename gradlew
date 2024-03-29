package com.example.travelmantics

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_insert.*

class InsertActivity : AppCompatActivity() {


    lateinit var mfirebaseDatabase: FirebaseDatabase
    lateinit var mDatabaseReference: DatabaseReference
    private var deal = TravelDeals()
    private val PICTURE_RESULT = 42


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        mfirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference



        if (intent.getSerializableExtra("Deal") != null) this.deal = intent.getSerializableExtra("Deal") as TravelDeals
        txtTitle.setText(deal.title)
        txtDescription.setText(deal.description)
        txtPrice.setText(deal.price)


        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICTURE_RESULT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)

        menu?.findItem(R.id.delete_menu)?.isVisible = FirebaseUtil.isAdmin
        menu?.findItem(R.id.save_menu)?.isVisible = FirebaseUtil.isAdmin
        enableEditTexts(FirebaseUtil.isAdmin)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.save_menu -> {
                saveDeal()
//                Toast.makeText(this@InsertActivity, "Deal Saved", Toast.LENGTH_LONG).show()
                clean()
                backToList()
                true
            }

            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this@InsertActivity, "Deal Deleted", Toast.LENGTH_SHORT).show()
                backToList()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val ref = FirebaseUtil.mStorageRef.child(imageUri!!.lastPathSegment.toString())
            ref.putFile(imageUri).addOnSuccessListener(this@InsertActivity,object:OnSuccessListener<UploadTask.TaskSnapshot>{
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    taskSnapshot.getDownloadUrl().toString();
                }
            })
        }

    }

    private fun clean() {

        txtTitle.setText("")
        txtDescription.setText("")
        txtPrice.setText("")


    }

    private fun saveDeal() {
        deal.title = txtTitle.text.toString()
        deal.description = txtDescription.text.toString()
        deal.price = txtPrice.text.toString()
        if (TextUtils.isEmpty(deal.id)) {
            if (!TextUtils.isEmpty(deal.title)) mDatabaseReference.push().setValue(deal)
                .addOnFailureListener {
                    Toast.makeText(this@InsertActivity, "Failed", Toast.LENGTH_SHORT).show()
                    Log.e("True", " Failed")
                }
                .addOnCompleteListener {
                    Toast.makeText(this@InsertActivity, "Completed", Toast.LENGTH_SHORT).show()
                    Log.e("True", "Completed")
                }
                .addOnCanceledListener {
                    Toast.makeText(this@InsertActivity, "Canceled", Toast.LENGTH_SHORT).show()
                    Log.e("True", "Canceled")
                }
        }//end if
        else {
            mDatabaseReference.child(deal.id).setValue(deal)
        }


    }//saveDeal

    private fun deleteDeal() {
        if (TextUtils.isEmpty(deal.title)) {
            Toast.makeText(this@InsertActivity, "Please Save Before Deleting", Toast.LENGTH_SHORT).show()
            return
        }
        mDatabaseReference.child(deal.id).removeValue()
    }

    private fun backToList() {
        startActivity(Intent(this@InsertActivity, ListActivity::class.java))
    }

    private fun enableEditTexts(isEnabled: Boolean) {
        txtTitle.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                       