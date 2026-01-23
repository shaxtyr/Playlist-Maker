package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        //enableEdgeToEdge()
        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        binding.viewPagerMedia.adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayoutMedia, binding.viewPagerMedia) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.my_favorite_tracks)
                1 -> tab.text = getString(R.string.my_playlists)
            }
        }
        tabMediator.attach()

        binding.backFromMedia.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}