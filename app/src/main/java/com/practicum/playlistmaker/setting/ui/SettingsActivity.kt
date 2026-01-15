package com.practicum.playlistmaker.setting.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creater.Creator
import com.practicum.playlistmaker.setting.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.interactor.SharingInteractor

class SettingsActivity: AppCompatActivity() {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var sharingInteractor: SharingInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Creator.initApplication(application)
        sharingInteractor = Creator.provideSharingInteractor()
        settingsInteractor = Creator.provideSettingsInteractor()

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory(sharingInteractor, settingsInteractor)).get(SettingsViewModel::class.java)

        val backIcon = findViewById<ImageView>(R.id.back_from_settings)
        val shareApp = findViewById<TextView>(R.id.share_app)
        val writeToSupport = findViewById<TextView>(R.id.write_to_support)
        val getUserAgreement = findViewById<TextView>(R.id.user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)

        viewModel.observeThemeSettings().observe(this) {
            themeSwitcher.isChecked = it.isDarkTheme
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
        }

        backIcon.setOnClickListener {
            finish()
        }

        getUserAgreement.setOnClickListener {
            viewModel.showUserDoc()
        }

        shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        writeToSupport.setOnClickListener {
            viewModel.writeToSupport()
        }
    }
}