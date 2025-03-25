package com.example.dusuncenipaylasapp.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dusuncenipaylasapp.R
import com.example.dusuncenipaylasapp.adapter.DusunceAdapter
import com.example.dusuncenipaylasapp.model.Paylasim
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DusunceActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db= Firebase.firestore
    var paylasimlistesi= ArrayList<Paylasim>()
    private lateinit var recyclerViewAdapter : DusunceAdapter


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater=menuInflater
        menuinflater.inflate(R.menu.menu_bar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.cikis_yap) {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if(item.itemId== R.id.paylasım_yap){
            val intent= Intent(this, PaylasimYap::class.java)
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dusunce)

        auth= Firebase.auth

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        FirebaseVerileriAl()
        val layoutManager= LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager=layoutManager

        recyclerViewAdapter = DusunceAdapter(paylasimlistesi)
        recyclerView.adapter=recyclerViewAdapter
        



    }
    fun FirebaseVerileriAl(){
        db.collection("Paylasimlar").orderBy("tarih", Query.Direction.DESCENDING ).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreError", "Hata: ${error.localizedMessage}")
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()

            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {

                        val documents = snapshot.documents

                        paylasimlistesi.clear()

                        for (document in documents) {
                            val kullaniciAdi = document.getString("kullanniciAdi") ?: "Bilinmeyen Kullanıcı"
                            val paylasilanYorum = document.getString("paylasimYorum") ?: "Yorum yok"
                            val gorselUrl = document.getString("gorselUrl") ?: ""  // NULL yerine boş string ata


                            Log.d("FirestoreData", "Kullanıcı: $kullaniciAdi, Yorum: $paylasilanYorum")

                            var indirilenPaylasim=Paylasim(kullaniciAdi,paylasilanYorum, gorselUrl)
                            paylasimlistesi.add(indirilenPaylasim)
                        }

                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }

        }


    }
}
