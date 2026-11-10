package vn.edu.vlu.lab09

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
        supportActionBar?.setTitle(R.string.title_gallery)

        binding.recyclerPhotos.layoutManager = LinearLayoutManager(this)
        binding.recyclerPhotos.adapter = PhotoAdapter(PhotoItem.samplePhotos)

        loadAvatar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Bonus: round avatar picked from the signed-in user's email. */
    private fun loadAvatar() {
        val avatarUrl = "https://i.pravatar.cc/150?u=" + (auth.currentUser?.email ?: "guest")

        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_broken_image)
            .circleCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivAvatar)
    }

    private fun logout() {
        auth.signOut()
        goToAuth()
    }

    private fun goToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
