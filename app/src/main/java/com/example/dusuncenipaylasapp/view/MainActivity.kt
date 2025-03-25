package com.example.dusuncenipaylasapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dusuncenipaylasapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance() // Firebase Authentication'ı başlat
        val guncelKullanici= auth.currentUser
        if (guncelKullanici != null){
            val intent= Intent(this, DusunceActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun kayitOl(view: View) {
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.passwordText.text.toString()
        val kullaniciAdi=binding.kullaniciAdiText.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val guncelKullanici= auth.currentUser

                        //kullanıcı adını güncelle

                        val profilGuncellemeIstegi = userProfileChangeRequest {
                            displayName = kullaniciAdi
                            photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                        }
                        if(guncelKullanici != null) {
                            guncelKullanici!!.updateProfile(profilGuncellemeIstegi)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(applicationContext, "Kullanıcı adı başarıyla eklendi", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }

                    }

                    val intent= Intent(this, DusunceActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                }
            Toast.makeText(applicationContext, "Kullanıcı başarıyla oluşturuldu", Toast.LENGTH_LONG).show()

        } else {
            Toast.makeText(applicationContext, "Lütfen e-posta ve şifre girin", Toast.LENGTH_LONG).show()
        }


    }

    fun girisYap(view: View) {
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.passwordText.text.toString()

        if (email!="" && password !=""){
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("FirebaseAuth", "Giriş başarılı")
                            Toast.makeText(applicationContext, "Giriş başarılı", Toast.LENGTH_LONG).show()

                            val guncelKullanici = auth.currentUser?.displayName.toString()
                            Toast.makeText(applicationContext, "Hoşgeldin ${guncelKullanici}", Toast.LENGTH_SHORT).show()


                            val intent = Intent(this, DusunceActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(applicationContext, "Lütfen e-posta ve şifre girin", Toast.LENGTH_LONG).show()
            }
        }
    }



}
