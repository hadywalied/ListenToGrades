package com.example.listen2grades

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_PERMISSION: Int = 100
    lateinit var mSpeechRecognizer: SpeechRecognizer
    lateinit var mSpeechRecognizerIntent: Intent


    lateinit var mFirebaseDatabase: FirebaseDatabase

    var deals = arrayListOf<Grades>()

    lateinit var results: ArrayList<String>

    private lateinit var textToSpeech: TextToSpeech

    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayoutCompat>

    lateinit var mDatabaseReference: DatabaseReference

    var name = ""
    var grade = ""
    var age = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()



    }

    override fun onResume() {
        super.onResume()

        //region FireBase Data init


        mFirebaseDatabase = FirebaseDatabase.getInstance()
        deals.clear()
        mDatabaseReference = mFirebaseDatabase.reference.child("Students")
//        mDatabaseReference.push()
//            .setValue(Grades(name = "Hady Walied", grade = "A+", address = "Cairo", age = 21))
//            .addOnFailureListener { Log.e("Error","not uploaded") }
//            .addOnSuccessListener { Log.e("Error","uploaded")  }
//        mDatabaseReference.push()
//            .setValue(Grades(name = "Sam Willson", grade = "B", address = "Cairo", age = 22))
//        mDatabaseReference.push()
//            .setValue(Grades(name = "Jake Brown", grade = "C", address = "Cairo", age = 25))
//        mDatabaseReference.push()
//            .setValue(Grades(name = "Muhammed Ahmed", grade = "F", address = "Cairo", age = 27))
//        mDatabaseReference.push()
//            .setValue(Grades(name = "Mustafa Muhammed", grade = "F", address = "Cairo", age = 28))
        mDatabaseReference.push()
            .setValue(Grades(name = "Hello", grade = "F", address = "Cairo", age = 28))
        //endregion


        //        region TTS
        textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ttsLang = textToSpeech.setLanguage(Locale.US)

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language is not supported!")
                } else {
                    Log.i("TTS", "Language Supported.")
                }
                Log.i("TTS", "Initialization success.")
            } else {
                Toast.makeText(applicationContext, "TTS Initialization failed!", Toast.LENGTH_SHORT).show()
            }
        })
//        endregion


        //region recognition initialization
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)

        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        ).putExtra(
            RecognizerIntent.EXTRA_RESULTS,
            true
        )
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        //endregion


        //region recognition listener set
        mSpeechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onEndOfSpeech() {

            }

            override fun onError(p0: Int) {
                onDetails()
                item_details.setText("Error")
            }

            override fun onResults(bundle: Bundle?) {
                results = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                if (!results.isEmpty()) {
                    //the recognition is a success
                    Toast.makeText(this@MainActivity, "result is $results", Toast.LENGTH_SHORT).show()

                    Log.e("Deal", "Before" + deals.toString())
                    getDataFromFirebase()



                } else {
                    Toast.makeText(this@MainActivity, "an error occured", Toast.LENGTH_SHORT).show()
                    onDetails()
                    item_details.setText("Error")
                    listeningEvent()
                }
            }
        })
//endregion

        listeningEvent()

        edit_order.setOnClickListener {
            onDetails()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            listeningEvent()
        }

    }

    fun getDataFromFirebase() {

        val callback: ChildEventListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val data = dataSnapshot.getValue(Grades::class.java)
                data?.id = dataSnapshot.key.toString()
                if (data != null) {
                    deals.add(data)
                    Log.e("Deal: ", data.name)
                }

                for (data in deals) {
                    for (result in results) {
                        if (data.name == result) {
                            name = data.name
                            grade = data.grade
                            age = data.age
                            onDetails()
                            item_details.setText(
                                "Name: $name \nGrade: $grade \nAge: $age"
                            )
                            ttsStart("Student Name is $name of an Age $age got a Grade of $grade")
                            break
                        }
                    }
                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }

        mDatabaseReference.addChildEventListener(callback)

    }

    fun ttsStart(str: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null)
        }

    }


    fun onDetails() {
        val bslayout: LinearLayoutCompat = findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bslayout)

        item_details.text = "Listening..."

        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }


    private fun listeningEvent() {

        mSpeechRecognizer.startListening(mSpeechRecognizerIntent)

        Handler().postDelayed({
            mSpeechRecognizer.stopListening()
        }, 2000)


    }

    public override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }


    //region permissions
    private fun checkPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_REQUEST_PERMISSION
                )
            }
        } else {
            // Permission has already been granted
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    finish()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }

    }
    //endregion


}

