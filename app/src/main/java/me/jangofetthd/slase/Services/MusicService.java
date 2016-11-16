package me.jangofetthd.slase.Services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import me.jangofetthd.slase.MainActivity;

/**
 * Created by JangoFettHD on 02.09.2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    /**
     * Media player
     */
    private MediaPlayer player;
    /**
     * URL of station
     */
    private String station;
    private final IBinder musicBind = new MusicBinder();

    //---------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        initMusicPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        player.getTrackInfo();
        //@TODO fwfsfas
        //startThread();
        resume();
    }


    //-----------------------------------

    public void playStation() {
        player.reset();
        try {
            player.setDataSource(station);

        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();

    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pause() {
        if (player.isPlaying())
            player.pause();
    }

    public void stop() {
        if (player.isPlaying())
            player.stop();
    }

    public void resume() {
        player.start();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }

    public void setStation(String station) {
        this.station = station;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //new 16.11.16
    public void getTrackInfo(Uri audioFileUri) {
        MediaMetadataRetriever metaRetriever= new MediaMetadataRetriever();
        metaRetriever.setDataSource(getRealPathFromURI(audioFileUri));
        String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        Log.i(title,artist);
    }
    private String getRealPathFromURI(Uri uri) {
        File myFile = new File(uri.getPath().toString());
        String s = myFile.getAbsolutePath();
        return s;
    }
}
