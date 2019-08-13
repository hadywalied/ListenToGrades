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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
                Toast