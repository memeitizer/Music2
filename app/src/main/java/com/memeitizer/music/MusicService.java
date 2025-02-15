package com.memeitizer.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    private final IBinder binder = new MusicBinder();
    private MediaPlayer mediaPlayer;
    private ArrayList<String> songList;
    private int currentSongIndex = 0;

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    public void setSongList(ArrayList<String> songs) {
        this.songList = songs;
    }

    public void playMusic() {
        if (songList == null || songList.isEmpty()) return;

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songList.get(currentSongIndex));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSong() {
        if (songList == null || songList.isEmpty()) return;

        currentSongIndex = (currentSongIndex + 1) % songList.size();
        playMusic();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
        }
