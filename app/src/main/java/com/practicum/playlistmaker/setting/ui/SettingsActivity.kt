package com.practicum.playlistmaker.setting.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creater.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.setting.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.interactor.SharingInteractor

class SettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var sharingInteractor: SharingInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Creator.initApplication(application)
        sharingInteractor = Creator.provideSharingInteractor()
        settingsInteractor = Creator.provideSettingsInteractor()

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory(sharingInteractor, settingsInteractor)).get(SettingsViewModel::class.java)

        viewModel.observeThemeSettings().observe(this) {
            binding.themeSwitcher.isChecked = it.isDarkTheme
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme(checked)
        }

        binding.backFromSettings.setOnClickListener {
            finish()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.showUserDoc()
        }

        binding.shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.writeToSupport()
        }
    }
}