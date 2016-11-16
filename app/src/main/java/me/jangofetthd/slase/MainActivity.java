package me.jangofetthd.slase;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import me.jangofetthd.slase.Services.IcyStreamMeta;
import me.jangofetthd.slase.Services.MusicService;

public class MainActivity extends Activity {
    private ImageButton btn;
    public static TextView nametrack;
    public static TextView authorTrack;
    public static String tSong = "", tArtist = "";
    Handler h;

    /**
     * help to toggle between play and pause.
     */
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    /**
     * remain false till media is not completed, inside OnCompletionListener make it true.
     */
    private boolean intialStage = true;

    MusicService musicService;
    private Intent playIntent;

    private boolean musicBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (ImageButton) findViewById(R.id.radio);
        nametrack = (TextView) findViewById(R.id.nametrack);
        authorTrack = (TextView) findViewById(R.id.authorTrack);
        /**
         * Service starting
         */
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);

        }


        btn.setOnClickListener(v -> {
            if (musicService.isPlaying()) {
                musicService.stop();
                btn.setImageResource(R.drawable.play_circular_button);

            } else {
                musicService.playStation();
                btn.setImageResource(R.drawable.pause_circular_button);

            }
        });

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                if (msg.what == 0) {
                    authorTrack.setText(tArtist);
                    nametrack.setText(tSong);
                }
            }

            ;
        };

        startThread();
        //startThread();

    }


    /**
     * Connection to service
     */


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            musicService = binder.getService();
            musicService.setStation("http://radio.slase.ru:8017/play");
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    public void startThread() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                URL url;
                //Message msg = h.handleMessage();
                try {
                    url = new URL("http://radio.slase.ru:8017/play");
                    IcyStreamMeta icy = new IcyStreamMeta(url);

                    //Log.d("SONG", icy.getTitle());
                    tSong = icy.getTitle();
                    //Toast.makeText(MusicService.this, icy.getTitle(), Toast.LENGTH_SHORT).show();
                    //msg.obj = icy.getTitle();

                    //Log.d("ARTITSi", icy.getArtist());
                    tArtist = icy.getArtist();
                    h.sendEmptyMessage(0);
                    //handler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, 0, 10000);

    }

}