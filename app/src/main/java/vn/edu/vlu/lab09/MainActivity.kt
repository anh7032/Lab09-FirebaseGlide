package vn.edu.vlu.lab09

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import vn.edu.vlu.lab09.adapter.PhotoAdapter
import vn.edu.vlu.lab09.databinding.ActivityMainBinding
import vn.edu.vlu.lab09.model.PhotoItem

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Guard: a signed-out user must never reach the gallery.
        if (auth.currentUser == null) {
            goToAuth()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.recyclerPhotos.layoutManager = LinearLayoutManager(this)
        binding.recyclerPhotos.adapter = PhotoAdapter(PhotoItem.samplePhotos)
    }

    private fun goToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
