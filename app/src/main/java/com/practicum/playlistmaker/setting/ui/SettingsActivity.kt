package com.practicum.playlistmaker.setting.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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