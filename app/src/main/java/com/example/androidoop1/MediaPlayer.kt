package com.example.AndroidOOP1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File

class MediaPlayer : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var playButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var volumeBar: SeekBar
    private lateinit var trackList: ListView
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var musicFiles = emptyArray<File>()
    private var musicTitles = emptyArray<String>()
    private var currentSongIndex = -1
    private var progressUpdater: android.os.Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        initializeViews()
        setupAudioManager()
        setupClickListeners()
        setupSeekBars()
        requestPermissions()
    }

    private fun initializeViews() {
        titleText = findViewById(R.id.trackTitleTextView)
        playButton = findViewById(R.id.btn_play_pause)
        stopButton = findViewById(R.id.btn_stop)
        prevButton = findViewById(R.id.btn_prev)
        nextButton = findViewById(R.id.btn_next)
        seekBar = findViewById(R.id.trackSeekBar)
        volumeBar = findViewById(R.id.volumeSeekBar)
        trackList = findViewById(R.id.tracksListView)
        currentTimeText = findViewById(R.id.currentTimeText)
        totalTimeText = findViewById(R.id.totalTimeText)
    }

    private fun setupAudioManager() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeBar.max = maxVolume
        volumeBar.progress = currentVolume

        volumeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupClickListeners() {
        playButton.setOnClickListener {
            togglePlayback()
        }
        stopButton.setOnClickListener {
            stopPlayback()
        }
        prevButton.setOnClickListener {
            playPrevious()
        }
        nextButton.setOnClickListener {
            playNext()
        }

        trackList.setOnItemClickListener { _, _, position, _ -> playSongAt(position) }
    }

    private fun setupSeekBars() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun requestPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            loadMusicFromStorage()
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) loadMusicFromStorage()
                else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }.launch(permission)
        }
    }

    private fun loadMusicFromStorage() {
        val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val musicList = mutableListOf<File>()
        val titleList = mutableListOf<String>()

        fun scanDirectory(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    scanDirectory(file)
                } else if (file.extension in listOf("mp3", "flac", "ogg")) {
                    musicList.add(file)
                    titleList.add(file.nameWithoutExtension)
                }
            }
        }

        scanDirectory(musicDir)
        musicFiles = musicList.toTypedArray()
        musicTitles = titleList.toTypedArray()

        if (musicFiles.isNotEmpty()) {
            trackList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, musicTitles.toList())
        } else {
            Toast.makeText(this, "No music found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePlayback() {
        if (currentSongIndex == -1) {
            Toast.makeText(this, "Select a song", Toast.LENGTH_SHORT).show()
            return
        }

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            playButton.text = "▶"
            stopProgressUpdater()
        } else {
            mediaPlayer?.start()
            playButton.text = "||"
            startProgressUpdater()
        }
    }

    private fun stopPlayback() {
        stopProgressUpdater()
        mediaPlayer?.apply {
            stop()
            release()
            mediaPlayer = null
        }
        resetUI()
    }

    private fun resetUI() {
        playButton.text = "▶"
        seekBar.progress = 0
        titleText.text = "Track Title"
        currentTimeText.text = "0:00"
        totalTimeText.text = "0:00"
        currentSongIndex = -1
    }

    private fun playPrevious() {
        if (currentSongIndex > 0) playSongAt(currentSongIndex - 1)
        else Toast.makeText(this, "First song", Toast.LENGTH_SHORT).show()
    }

    private fun playNext() {
        if (currentSongIndex < musicFiles.size - 1) playSongAt(currentSongIndex + 1)
        else Toast.makeText(this, "Last song", Toast.LENGTH_SHORT).show()
    }

    private fun playSongAt(index: Int) {
        stopPlayback()
        currentSongIndex = index

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(musicFiles[index].absolutePath)
                prepare()
                start()

                setOnCompletionListener {
                    stopPlayback()
                    titleText.text = "Track Title"
                }
            }

            titleText.text = musicTitles[index]
            playButton.text = "||"
            seekBar.max = mediaPlayer!!.duration
            totalTimeText.text = formatTime(mediaPlayer!!.duration)

            startProgressUpdater()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            resetUI()
        }
    }

    private fun startProgressUpdater() {
        progressUpdater = android.os.Handler(mainLooper)
        updateProgress()
    }

    private fun updateProgress() {
        if (mediaPlayer?.isPlaying == true) {
            seekBar.progress = mediaPlayer!!.currentPosition
            currentTimeText.text = formatTime(mediaPlayer!!.currentPosition)
            progressUpdater?.postDelayed({ updateProgress() }, 1000)
        }
    }

    private fun stopProgressUpdater() {
        progressUpdater?.removeCallbacksAndMessages(null)
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        return "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            playButton.text = "▶"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }
}