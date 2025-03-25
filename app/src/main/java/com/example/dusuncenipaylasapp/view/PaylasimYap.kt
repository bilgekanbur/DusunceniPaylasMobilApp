package com.example.dusuncenipaylasapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.example.dusuncenipaylasapp.R
import com.example.dusuncenipaylasapp.databinding.ActivityPaylasimYapBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID


class PaylasimYap : AppCompatActivity() {

    val db= Firebase.firestore
    private lateinit var auth: FirebaseAuth
    val storage =  Firebase.storage
    private lateinit var binding: ActivityPaylasimYapBinding
    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_paylasim_yap)

        binding = ActivityPaylasimYapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth=Firebase.auth

    }

    fun paylas(view: View){
        if(secilenGorsel != null)
        {
            val reference = storage.reference

            val uuid = UUID.randomUUID()
            var gorselIsmi ="${uuid}.jpg"
            val gorselreference=reference.child("resimler").child(gorselIsmi)
            gorselreference.putFile(secilenGorsel!!).addOnSuccessListener { task ->
                //url alınacak
                val yuklenengorselreferansi = reference.child("resimler").child(gorselIsmi)

                yuklenengorselreferansi.downloadUrl.addOnSuccessListener { uri ->
                    val downloadURL= uri.toString()
                    val paylasilan_yorum= binding.editTextText.text.toString()
                    val kullanici_adi= auth.currentUser?.displayName.toString()
                    val tarih= Timestamp.now()

                    val paylasimMap= hashMapOf<String, Any>()
                    paylasimMap.put("paylasimYorum", paylasilan_yorum)
                    paylasimMap.put("kullanniciAdi", kullanici_adi)
                    paylasimMap.put("tarih", tarih)
                    paylasimMap.put("gorselUrl",downloadURL)

                    db.collection("Paylasimlar").add(paylasimMap).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener{exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }else {
            veritabaninaKaydet()

        }
    }

    private fun veritabaninaKaydet(){

        val paylasilan_yorum= binding.editTextText.text.toString()
        val kullanici_adi= auth.currentUser?.displayName.toString()
        val tarih= Timestamp.now()

        val paylasimMap= hashMapOf<String, Any>()
        paylasimMap.put("paylasimYorum", paylasilan_yorum)
        paylasimMap.put("kullanniciAdi", kullanici_adi)
        paylasimMap.put("tarih", tarih)

        db.collection("Paylasimlar").add(paylasimMap).addOnCompleteListener { task ->
            if(task.isSuccessful){
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }
    fun gorselEkle(view: View){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){

            //izin verilmemiş, izin iste
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES),1)

        }else {
            //izin var galeriye git
            var galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }
    //isteklerde izin verildi mi buna bakmalıyım
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if (grantResults.size >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //izin verilmiş
                //izin var galeriye git
                var galeriIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //startactivitynin sonucunda ne döner
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageview= binding.imageView2
        if(requestCode==2 && resultCode == RESULT_OK && data != null){
            imageview.visibility= View.VISIBLE
            secilenGorsel=data.data
            if (secilenGorsel != null){
                if (Build.VERSION.SDK_INT>=28){
                    val source= ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)

                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    imageview.setImageBitmap(secilenBitmap)

                }else{
                    secilenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    imageview.setImageBitmap(secilenBitmap)

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}