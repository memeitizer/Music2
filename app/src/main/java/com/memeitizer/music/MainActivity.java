package com.memeitizer.music;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 1;
    private ArrayList<String> songList;
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = 0;
    private Handler apiHandler = new Handler(Looper.getMainLooper());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mediaPlayer = new MediaPlayer();
        songList = new ArrayList<>();

        Button playButton = findViewById(R.id.playButton);
        Button nextButton = findViewById(R.id.nextButton);
        
        playButton.setOnClickListener(view -> playMusic());
        nextButton.setOnClickListener(view -> nextSong());
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            loadSongs();
        }

        startAPICheck();
    }

    private void loadSongs() {
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = getContentResolver().query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                songList.add(musicCursor.getString(titleColumn));
            } while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    private void playMusic() {
        if (songList.isEmpty()) {
            Toast.makeText(this, "No music found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songList.get(currentSongIndex));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextSong() {
        currentSongIndex = (currentSongIndex + 1) % songList.size();
        playMusic();
    }

    private void startAPICheck() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                APIHelper.checkForNotifications(MainActivity.this);
            }
        }, 0, 300000); // Check every 5 minutes
    }
}
