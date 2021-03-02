package com.neo.livestream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;

import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.Timer;
import java.util.TimerTask;

public class Stream extends AppCompatActivity {
    private static final String TAG = "ActivityStreamPlayer";
    String url, link;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DataSource.Factory mediaDataSourceFactory;
    private Handler mainHandler;
    private ProgressBar progressBar;


    private Context context = Stream.this;
    private ImageButton imageButtonFullScreen;
    private Boolean isFullScreen = true;

    private Timer timer;
    private TimerTask timerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//            getActionBar().hide();
//            this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//            getActionBar().hide();
        setContentView(R.layout.activity_stream);
            Intent i = getIntent();
            link = i.getStringExtra("channelling");

        url = "rtmp://95.216.226.165:1935/babatv  ";



            progressBar = findViewById(R.id.progressBar);

            mediaDataSourceFactory = buildDataSourceFactory(true);


            mainHandler = new Handler();
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            RenderersFactory renderersFactory = new DefaultRenderersFactory(this);

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

            playerView = findViewById(R.id.exoPlayerView);


            playerView.setPlayer(player);
            playerView.setUseController(true);
            playerView.requestFocus();

            Uri uri = Uri.parse(url);

            MediaSource mediaSource = buildMediaSource(uri, null);

            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
            playerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            });

            player.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                    Log.d(TAG, "onTimelineChanged: ");
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.d(TAG, "onTracksChanged: " + trackGroups.length);
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.d(TAG, "onLoadingChanged: " + isLoading);
                    if (!isLoading){
                        player.stop();
                        Intent i=new Intent(Stream.this,Stream.class);
                        startActivity(i);
                        finish();
                    }

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.d(TAG, "onPlayerStateChanged: " + playWhenReady);
                    if (playbackState == PlaybackState.STATE_PLAYING) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    Log.e(TAG, "onPlayerError: ", error);
                    Uri uri = Uri.parse(url);
                    MediaSource mediaSource = buildMediaSource(uri, null);
                    player.prepare(mediaSource);
                    player.setPlayWhenReady(true);
                }

                @Override
                public void onPositionDiscontinuity(int reason) {
                    Log.d(TAG, "onPositionDiscontinuity: true");
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });

            Log.d("INFO", "ActivityVideoPlayer");

        imageButtonFullScreen = findViewById(R.id.exo_fullscreen_button);
        imageButtonFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flagsFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
                if (isFullScreen) {
                    getWindow().addFlags(flagsFullScreen); // set full screen
           getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    //If the above does not work, you can replace it with the following.
                    if (getSupportActionBar() != null)
                        getSupportActionBar().hide();    //App title bar
                    isFullScreen = false;
                } else { //Exit Full Screen
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= (~flagsFullScreen);
                    getWindow().setAttributes(attrs);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    if (getSupportActionBar() != null)
                        getSupportActionBar().show();    //App title bar
                    isFullScreen = true;
                }
            }
        });
        playerView = findViewById(R.id.exoPlayerView);

        }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "ExoPlayerDemo"), bandwidthMeter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
        Intent i =new Intent(Stream.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    protected void onStart() {
        super.onStart();
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri, null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }



    public void errorDialog() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Oops!")
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.msg_failed))
                .setPositiveButton(getResources().getString(R.string.option_retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        retryLoad();
//                        startTimer();
                    }

                })
                .setNegativeButton(getResources().getString(R.string.option_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
//        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }
//    public void initializeTimerTask() {
//        timerTask = new TimerTask() {
//            public void run() {
////                Log.i("in timer", "in timer ++++  "+ (counter++));
//                retryLoad();
//            }
//        };
//    }

    public void retryLoad() {
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri, null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

}


