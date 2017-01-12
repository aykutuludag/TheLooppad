package org.uusoftware.thelaunchpadhouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ActivitySettings extends AppCompatActivity {

    Switch mySwitch, mySwitch2;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    boolean theme, alarmon, premium, minimal, tribal;
    int color = Color.parseColor("#616161");
    int color2 = Color.parseColor("#9E9E9E");
    Window window;
    ActionBar bar;
    PackageInfo pInfo;
    Tracker t;
    TextView text, text2, text3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Premium & AdMob
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        theme = prefs.getBoolean("Theme", false);
        alarmon = prefs.getBoolean("Alarm", true);
        premium = MainActivity.premium;
        minimal = MainActivity.minimal;
        tribal = MainActivity.tribal;

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
        t.setScreenName("Settings");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        editor = getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit();
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        mySwitch2 = (Switch) findViewById(R.id.mySwitch2);
        text = (TextView) findViewById(R.id.textView2);
        text2 = (TextView) findViewById(R.id.textView4);
        text3 = (TextView) findViewById(R.id.textView6);

        // set the switch to ON
        if (alarmon) {
            mySwitch.setChecked(true);
        } else {
            mySwitch.setChecked(false);
        }

        // attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("Alarm", true);
                } else {
                    editor.putBoolean("Alarm", false);
                }
            }
        });

        // set the switch2 to Theme: White
        if (theme) {
            mySwitch2.setChecked(true);
        } else {
            mySwitch2.setChecked(false);
        }

        // Change switch textes
        mySwitch2.setTextOn(getString(R.string.light));
        mySwitch2.setTextOff(getString(R.string.dark));

        // attach a listener to check for changes in state
        mySwitch2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("Theme", true);
                } else {
                    editor.putBoolean("Theme", false);
                }
            }
        });

        if (premium) {
            text.setText("Premium" + " - " + version);
        } else {
            text.setText("Standart" + " - " + version);
        }
        if (tribal) {
            text2.setText(getString(R.string.available));
        } else {
            text2.setText(getString(R.string.locked));
        }
        if (minimal) {
            text3.setText(getString(R.string.available));
        } else {
            text3.setText(getString(R.string.locked));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                editor.commit();
                Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
                finish();
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
}