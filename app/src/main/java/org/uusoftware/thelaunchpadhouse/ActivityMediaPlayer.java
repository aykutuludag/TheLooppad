package org.uusoftware.thelaunchpadhouse;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.IOException;

public class ActivityMediaPlayer extends AppCompatActivity {

    MediaPlayer mp;
    String path;
    File file;
    Button button;
    Tracker t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);

        // AdMob
        boolean premium = MainActivity.premium;
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.adView);
        if (premium) {
            adView.setVisibility(View.GONE);
        } else {
            adView.loadAd(new AdRequest.Builder().addTestDevice("0A83AF9337EAE655A7B29C5B61372D84").build());
        }

        // Analytics
        t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("MediaPlayer");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        path = getIntent().getExtras().getString("uri");
        file = new File(path);

        TextView textview = (TextView) findViewById(R.id.textView1);
        textview.setText(file.getName());

        button = (Button) findViewById(R.id.buttonPlay);
        button.setPressed(true);
        button.setClickable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                    button.setBackgroundResource(R.drawable.ic_play);
                } else {
                    mp.start();
                    button.setBackgroundResource(R.drawable.ic_pause);
                    button.setPressed(true);
                }
            }
        });
        try {
            initializeMp();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initializeMp() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        mp = new MediaPlayer();
        mp.setDataSource(this, Uri.fromFile(file));
        mp.prepareAsync();
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                button.setBackgroundResource(R.drawable.ic_pause);
            }
        });
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                button.setBackgroundResource(R.drawable.ic_play);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main5, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(share, null));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        mp.pause();
        button.setBackgroundResource(R.drawable.ic_play);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}