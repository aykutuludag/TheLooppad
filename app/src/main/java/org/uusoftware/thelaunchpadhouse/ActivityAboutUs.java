package org.uusoftware.thelaunchpadhouse;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ActivityAboutUs extends AppCompatActivity {

    String str1 = "https://uusoftware.org";
    String str2 = "https://www.facebook.com/uusoftware";
    String str3 = "https://twitter.com/uusoftware1";
    String str4 = "https://instagram.com/uusoftware";
    String str5 = "https://www.youtube.com/channel/UCpzVBPCN4XSJt8sL5u8X_FQ";
    String str6 = "https://plus.google.com/115518080824239135242";

    Tracker t;
    int color = Color.parseColor("#616161");
    int color2 = Color.parseColor("#9E9E9E");
    Window window;
    ActionBar bar;

    ImageView image1, image2, image3, image4, image5, image6, image7;
    Intent intent1, intent2, intent3, intent4, intent5, intent6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        // Premium & AdMob
        boolean premium = MainActivity.premium;
        AdView adView = (AdView) findViewById(R.id.adMob);
        if (premium) {
            adView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("0A83AF9337EAE655A7B29C5B61372D84").build();
            adView.loadAd(adRequest);
        }

        // Theme
        bar = this.getSupportActionBar();
        window = this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
        }

        // Analytics
        t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("About us");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        // Initializing
        image1 = (ImageView) findViewById(R.id.imageView1);
        image2 = (ImageView) findViewById(R.id.imageView2);
        image3 = (ImageView) findViewById(R.id.imageView3);
        image4 = (ImageView) findViewById(R.id.imageView4);
        image5 = (ImageView) findViewById(R.id.imageView5);
        image6 = (ImageView) findViewById(R.id.imageView6);
        image7 = (ImageView) findViewById(R.id.imageView7);

        OnClickListener buttonListener = new OnClickListener() {

            public void onClick(final View v) {

                switch (v.getId()) {
                    case R.id.imageView1:
                        intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(str1));
                        startActivity(intent1);
                        break;
                    case R.id.imageView2:
                        intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(str2));
                        startActivity(intent2);
                        break;
                    case R.id.imageView3:
                        intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(str3));
                        startActivity(intent3);
                        break;
                    case R.id.imageView4:
                        intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(str4));
                        startActivity(intent4);
                        break;
                    case R.id.imageView5:
                        intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse(str5));
                        startActivity(intent5);
                        break;
                    case R.id.imageView6:
                        intent6 = new Intent(Intent.ACTION_VIEW, Uri.parse(str6));
                        startActivity(intent6);
                        break;
                    case R.id.imageView7:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("plain/text");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"uusoftware@outlook.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Günlük Burçlar");
                        try {
                            startActivity(Intent.createChooser(i, "E-posta gönder..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ActivityAboutUs.this, "Cihazınızda e-posta uygulaması bulunmamaktadır!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        };

        image1.setOnClickListener(buttonListener);
        image2.setOnClickListener(buttonListener);
        image3.setOnClickListener(buttonListener);
        image4.setOnClickListener(buttonListener);
        image5.setOnClickListener(buttonListener);
        image6.setOnClickListener(buttonListener);
        image7.setOnClickListener(buttonListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_support:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "The Looppad on Google Play: https://play.google.com/store/apps/details?id=org.uusoftware.thelaunchpadhouse @thelooppad";
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
};