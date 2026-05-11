package com.practicum.playlistmaker.player.ui

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.media.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.services.MusicService
import com.practicum.playlistmaker.utils.WithoutNetworkBroadcastReceiver
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment(){

    private lateinit var withoutNetworkBroadcastReceiver: WithoutNetworkBroadcastReceiver
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlayerViewModel
    //private var pendingAction: (() -> Unit)? = null
    private lateinit var openTrack: Track

    private val playlistsAdapter = PlaylistPlayerAdapter { playlist ->
        viewModel.checkTrackIdInPlaylist(playlist)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            val binder = service as MusicService.MusicServiceBinder
            viewModel.setPlayerControl(binder.getService())

            //pendingAction?.invoke()
            //pendingAction = null
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removePlayerControl()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Если выдали разрешение — запускаем сервис.
            startForegroundService()
            //bindMusicService()
            //viewModel.playbackControl()
        } else {
            // Иначе просто покажем ошибку
            Toast.makeText(requireContext(), "Can't start foreground service!", Toast.LENGTH_LONG).show()
        }
    }

    //region onCreateView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    //endregion

    //region onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        withoutNetworkBroadcastReceiver = WithoutNetworkBroadcastReceiver(requireContext())

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        openTrack = requireArguments().get(OPEN_TRACK_KEY) as Track

        viewModel = getKoin().get { parametersOf(openTrack) }

        viewModel.getPlaylists()

        viewModel.observePlayerState().observe(viewLifecycleOwner) {

            if (it.addedTrackToPlaylistState != null) {

                val message = when(it.addedTrackToPlaylistState) {

                    is AddedTrackToPlaylistState.AlreadyInPlaylist -> {
                        getString(R.string.track_already_added, it.addedTrackToPlaylistState.playlistName)
                    }

                    is AddedTrackToPlaylistState.AddedToPlayList -> {
                        viewModel.getPlaylists()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        getString(R.string.track_added, it.addedTrackToPlaylistState.playlistName)

                    }

                }

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            if (it.isFavorite) {
                binding.likeButton.setImageResource(R.drawable.ic_like_with_heart_51)
            } else {
                binding.likeButton.setImageResource(R.drawable.ic_like_51)
            }

            when(it.stateMode) {
                EnumStateMode.PLAYING ->  binding.playButton.switchPlayButton(true)
                else -> binding.playButton.switchPlayButton(false)
            }

            binding.progressBarAudioPlayer.text = it.progressTime
            showPlaylistsContent(it.playlists)

        }
        setOtherInfoFromTrack()

        bindMusicService()

        binding.recyclerViewPlaylistBottom.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPlaylistBottom.adapter = playlistsAdapter

        binding.playButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение уже есть
                    startForegroundService()
                    //bindMusicService()
                    //viewModel.playbackControl()
                } else {
                    // Разрешения нет — запроси
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                // Для старых версий ничего не запрашиваем
                startForegroundService()

            }
            viewModel.playbackControl()

        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistFromBottom.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        findNavController().navigate(R.id.action_playerFragment_to_creatingPlaylistFragment)
                        bottomSheetBehavior.removeBottomSheetCallback(this)
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        binding.backFromAudioPlayer.setOnClickListener {

            requireContext().stopService(Intent(context, MusicService::class.java))
            unbindMusicService()
            findNavController().navigateUp()

        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f)/2
            }
        })

    }
    //endregion

    //region onResume
    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            requireContext(),
            withoutNetworkBroadcastReceiver,
            IntentFilter(ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        viewModel.hideNotify()
    }
    //endregion

    //region onPause
    override fun onPause() {
        super.onPause()
        //viewModel.pausePlayer()
        if (viewModel.observePlayerState().value?.stateMode == EnumStateMode.PAUSED) {
            viewModel.hideNotify()
        } else {
            viewModel.showNotify()
        }

        requireContext().unregisterReceiver(withoutNetworkBroadcastReceiver)
    }
    //endregion

    //region onDestroyView
    override fun onDestroyView() {
        unbindMusicService()
        super.onDestroyView()
        _binding = null
    }
    //endregion

    private fun bindMusicService() {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putExtra(SONG_URL_KEY, openTrack.previewUrl)
            putExtra(TRACK_NAME_KEY, openTrack.trackName)
            putExtra(ARTIST_NAME_KEY, openTrack.artistName)
        }


        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        //ContextCompat.startForegroundService(requireContext(), intent)

        //viewModel.playbackControl()
    }

    private fun startForegroundService() {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putExtra(SONG_URL_KEY, openTrack.previewUrl)
            putExtra(TRACK_NAME_KEY, openTrack.trackName)
            putExtra(ARTIST_NAME_KEY, openTrack.artistName)
        }


        //requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }

    private fun setOtherInfoFromTrack() {
        Glide.with(requireContext())
            .load(openTrack.getCoverArtwork())
            .placeholder(R.drawable.placeholder_312)
            .into(binding.coverFromCardview)

        binding.trackNameAudioPlayer.text = openTrack.trackName
        binding.artistNameAudioPlayer.text = openTrack.artistName
        binding.timeValueAudioPlayer.text = openTrack.trackTime

        if (openTrack.collectionName.isEmpty()) {
            binding.albumGroup.isVisible = false
        } else {
            binding.albumGroup.isVisible = true
            binding.albumValueAudioPlayer.text = openTrack.collectionName
        }

        binding.yearGroup.isVisible = true
        binding.yearValueAudioPlayer.text = openTrack.getYearFromReleaseDate()

        binding.genreValueAudioPlayer.text = openTrack.primaryGenreName
        binding.countryValueAudioPlayer.text = openTrack.country
    }

    fun showPlaylistsContent(playlists: List<Playlist>) {
        binding.recyclerViewPlaylistBottom.isVisible = true

        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val OPEN_TRACK_KEY = "open_track"
        private const val SONG_URL_KEY = "song_url"
        private const val TRACK_NAME_KEY = "track_name"
        private const val ARTIST_NAME_KEY = "artist_name"
        const val ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

        fun createArgs(track: Track): Bundle =
            bundleOf(OPEN_TRACK_KEY to track)
    }

}