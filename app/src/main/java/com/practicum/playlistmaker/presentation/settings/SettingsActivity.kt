package com.practicum.playlistmaker.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backIcon = findViewById<ImageView>(R.id.back_from_settings)
        val shareApp = findViewById<TextView>(R.id.share_app)
        val writeToSupport = findViewById<TextView>(R.id.write_to_support)
        val getUserAgreement = findViewById<TextView>(R.id.user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)
        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        backIcon.setOnClickListener {
            finish()
        }

        getUserAgreement.setOnClickListener {
            val link = getString(R.string.user_agreement)
            val userIntent = Intent(Intent.ACTION_VIEW)
            userIntent.data = link.toUri()
            startActivity(userIntent)
        }

        shareApp.setOnClickListener {
            val link = getString(R.string.android_developer_link)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(shareIntent)
        }

        writeToSupport.setOnClickListener {
            val subject = getString(R.string.to_developers)
            val message = getString(R.string.thanks_to_developers)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = "mailto:".toUri()
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }
    }
}