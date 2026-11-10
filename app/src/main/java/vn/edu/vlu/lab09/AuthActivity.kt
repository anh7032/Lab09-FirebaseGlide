package vn.edu.vlu.lab09

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import vn.edu.vlu.lab09.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var isRegisterMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auto-login: skip the form when a session is already cached.
        if (auth.currentUser != null) {
            goToMain()
            return
        }

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() {
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            isRegisterMode = checkedId == R.id.btnRegister
            applyMode()
        }

        binding.btnSubmit.setOnClickListener { submit() }
        binding.tvForgotPassword.setOnClickListener { sendPasswordResetEmail() }

        applyMode()
    }

    /** Keeps confirm-password visibility and the submit label in sync with the selected mode. */
    private fun applyMode() {
        binding.tilConfirmPassword.visibility = if (isRegisterMode) View.VISIBLE else View.GONE
        binding.btnSubmit.setText(if (isRegisterMode) R.string.btn_register else R.string.btn_login)

        if (!isRegisterMode) {
            binding.etConfirmPassword.text?.clear()
            binding.tilConfirmPassword.error = null
        }
    }

    private fun submit() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()

        if (!validateInput(email, password, confirmPassword)) return

        setLoading(true)

        val task = if (isRegisterMode) {
            auth.createUserWithEmailAndPassword(email, password)
        } else {
            auth.signInWithEmailAndPassword(email, password)
        }

        task.addOnSuccessListener {
            setLoading(false)
            Toast.makeText(
                this,
                if (isRegisterMode) R.string.msg_register_success else R.string.msg_login_success,
                Toast.LENGTH_SHORT
            ).show()
            goToMain()
        }.addOnFailureListener { exception ->
            setLoading(false)
            handleAuthError(exception)
        }
    }

    /** Bonus: send a reset link for the email currently typed in the form. */
    private fun sendPasswordResetEmail() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            return
        }

        binding.tilEmail.error = null
        setLoading(true)

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, getString(R.string.msg_reset_sent, email), Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                setLoading(false)
                handleAuthError(exception)
            }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        clearErrors()
        var isValid = true

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            binding.tilPassword.error = getString(R.string.error_short_password)
            isValid = false
        }

        if (isRegisterMode && password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.error_confirm_mismatch)
            isValid = false
        }

        return isValid
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    private fun handleAuthError(exception: Exception) {
        when (exception) {
            is FirebaseAuthWeakPasswordException ->
                binding.tilPassword.error = getString(R.string.error_weak_password)

            is FirebaseAuthUserCollisionException ->
                binding.tilEmail.error = getString(R.string.error_user_collision)

            is FirebaseAuthInvalidCredentialsException ->
                binding.tilPassword.error = getString(R.string.error_invalid_credentials)

            is FirebaseNetworkException ->
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_LONG).show()

            else -> Toast.makeText(
                this,
                getString(R.string.error_unknown, exception.message ?: ""),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !loading
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
