package vn.edu.hcmuaf.fit.ttltmobile.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.edu.hcmuaf.fit.ttltmobile.databinding.ActivitySplashBinding
import vn.edu.hcmuaf.fit.ttltmobile.ui.admin.AdminDashboardActivity
import vn.edu.hcmuaf.fit.ttltmobile.ui.home.MainActivity

class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(3000)

            if (isUserLoggedIn()) {
                navigateBasedOnRole()
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        return !token.isNullOrEmpty()
    }

    private fun navigateBasedOnRole() {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userRole = sharedPref.getString("user_role", "USER")

        val intent = if (userRole == "ADMIN") {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
    }
}