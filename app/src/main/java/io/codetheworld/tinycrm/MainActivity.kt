package io.codetheworld.tinycrm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.codetheworld.tinycrm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            debugTextView.text = "Debug: ${BuildConfig.DEBUG}"
            versionTextView.text = "Version: ${BuildConfig.VERSION_NAME}"
            baseUrlTextView.text = "Base Url: ${BuildConfig.BASE_URL}"
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
