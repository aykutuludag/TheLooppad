package org.uusoftware.thelaunchpadhouse;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;
import org.uusoftware.thelaunchpadhouse.adapter.NavDrawerListAdapter;
import org.uusoftware.thelaunchpadhouse.model.NavDrawerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import jp.co.recruit_mp.android.rmp_appirater.RmpAppirater;

public class MainActivity extends AppCompatActivity {

    static InterstitialAd interstitial;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    CharSequence mDrawerTitle;
    CharSequence mTitle;
    String[] navMenuTitles;
    TypedArray navMenuIcons;
    ArrayList<NavDrawerItem> navDrawerItems;
    NavDrawerListAdapter adapter;
    FragmentTransaction ft;
    Fragment fragment = null;
    public static long start = System.currentTimeMillis();
    public static boolean theme, alarmon = true;
    ;
    public static boolean doubleBackToExitPressedOnce, displayed, displayed2, displayed3, displayed4, premium, minimal,
            tribal = false;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    File folder;
    static IInAppBillingService mService;
    ServiceConnection mServiceConn;
    public static byte whichPreset;
    public static Context mContext;
    String str4 = "https://www.youtube.com/channel/UCLO_22j_uvR2cItxaLm7TMQ";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GetPreferences
        prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        editor = getSharedPreferences("Preferences", Context.MODE_PRIVATE).edit();
        theme = prefs.getBoolean("Theme", true);
        alarmon = prefs.getBoolean("Alarm", true);
        premium = prefs.getBoolean("Premium", false);
        minimal = prefs.getBoolean("Minimal", false);
        tribal = prefs.getBoolean("Tribal", false);

        // Context and IAP
        mContext = this.getApplicationContext();
        AdMob();
        InAppBilling();

        // Create The Looppad folder
        verifyStoragePermissions();

        // AppRater
        RmpAppirater.appLaunched(this,
                new RmpAppirater.ShowRateDialogCondition() {
                    @Override
                    public boolean isShowRateDialog(
                            long appLaunchCount, long appThisVersionCodeLaunchCount,
                            long firstLaunchDate, int appVersionCode,
                            int previousAppVersionCode, Date rateClickDate,
                            Date reminderClickDate, boolean doNotShowAgain) {
                        // Show rating dialog if user isn't rating yet
                        // && don't select "Not show again"
                        // && launched app more than 5 times.
                        return (rateClickDate == null && !doNotShowAgain && appLaunchCount >= 5);
                    }
                },
                new RmpAppirater.Options(
                        "Uygulamamızı beğendiniz mi?", "Gaia uygulamasını beğendiyseniz 5 yıldız vererek destek olabilirsiniz.",
                        "Oyla", "Sonra",
                        "Hayır, teşekkürler"));
        ;

        // AlarmManager
        AlarmManager();

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTitle = mDrawerTitle = getTitle();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        // House
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Records
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Records
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Premium
        if (premium) {
            // Do nothing
        } else {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        }
        // Footer
        navDrawerItems.add(new NavDrawerItem("", 0));
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_slider_drawer, R.string.app_name) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            displayView(0);
        }
    }

    public void InAppBilling() {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                try {
                    checkPremium();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void checkPremium() throws RemoteException {
        Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
        if (ownedItems.getInt("RESPONSE_CODE") == 0) {
            ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            if (ownedSkus.contains("ad_free")) {
                editor.putBoolean("Premium", true).commit();
            } else {
                editor.putBoolean("Premium", false).commit();
            }
            if (ownedSkus.contains("minimal_house")) {
                editor.putBoolean("Minimal", true).commit();
            } else {
                editor.putBoolean("Minimal", false).commit();
            }
            if (ownedSkus.contains("tribal_house")) {
                editor.putBoolean("Tribal", true).commit();
            } else {
                editor.putBoolean("Tribal", false).commit();
            }
        }
    }

    public void buyPremium() throws RemoteException, SendIntentException {
        Toast.makeText(MainActivity.this, getString(R.string.buyPremium), Toast.LENGTH_LONG).show();
        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), "ad_free", "inapp", "A1000");
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0),
                Integer.valueOf(0), Integer.valueOf(0));
    }

    public static void buyTribal(Context context) throws RemoteException, SendIntentException {
        Toast.makeText(context, context.getString(R.string.buyTribal), Toast.LENGTH_LONG).show();
        Bundle buyIntentBundle = mService.getBuyIntent(3, context.getPackageName(), "tribal_house", "inapp", "A1001");
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        ((MainActivity) context).startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));

    }

    public static void buyMinimal(Context context) throws RemoteException, SendIntentException {
        Toast.makeText(context, context.getString(R.string.buyMinimal), Toast.LENGTH_LONG).show();
        Bundle buyIntentBundle = mService.getBuyIntent(3, context.getPackageName(), "minimal_house", "inapp", "A1002");
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        ((MainActivity) context).startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(),
                Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
    }

    public static void AdMob() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("0A83AF9337EAE655A7B29C5B61372D84").build();
        interstitial = new InterstitialAd(mContext);
        interstitial.setAdUnitId("ca-app-pub-1576175228836763/8910020935");
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mContext.startActivity(FragmentHouse.intent);
                AdMob();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }
        });
        interstitial.loadAd(adRequest);
    }

    public static void displayAds() {
        if (interstitial.isLoaded()) {
            interstitial.show();
            displayed = true;
        } else {
            mContext.startActivity(FragmentHouse.intent);
        }
    }

    public static void displayAds2() {
        if (interstitial.isLoaded()) {
            interstitial.show();
            displayed2 = true;
        } else {
            mContext.startActivity(FragmentHouse.intent);
        }
    }

    public static void displayAds3() {
        if (interstitial.isLoaded()) {
            interstitial.show();
            displayed3 = true;
        } else {
            mContext.startActivity(FragmentHouse.intent);
        }
    }

    public static void displayAds4() {
        if (interstitial.isLoaded()) {
            interstitial.show();
            displayed4 = true;
        } else {
            mContext.startActivity(FragmentHouse.intent);
        }
    }

    public void AlarmManager() {
        int week = 1000 * 60 * 60 * 24 * 7;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);

        Calendar calendar2 = Calendar.getInstance();

        if (alarmon) {
            if (calendar2.getTimeInMillis() < calendar.getTimeInMillis()) {
                alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), week, pendingIntent);
            } else {
                alarmManager.setInexactRepeating(AlarmManager.RTC, 1000 * 60 * 60 * 24 * 7 + calendar.getTimeInMillis(),
                        week, pendingIntent);
            }
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void verifyStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permission = ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permission2 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
            if (permission != PackageManager.PERMISSION_GRANTED && permission2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_ID_MULTIPLE_PERMISSIONS);
            } else {
                createFolder();
            }
        } else {
            createFolder();
        }
    }

    private void createFolder() {
        folder = new File(Environment.getExternalStorageDirectory() + "/The Looppad");
        folder.mkdirs();
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }

    private void displayView(int position) {
        ft = getSupportFragmentManager().beginTransaction();

        // Fragments
        FragmentHouse fragment0 = (FragmentHouse) getSupportFragmentManager().findFragmentByTag("House");
        FragmentRecords fragment1 = (FragmentRecords) getSupportFragmentManager().findFragmentByTag("Records");

        switch (position) {
            case 0:
                if (fragment0 != null && fragment0.isVisible()) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    setTitle(navMenuTitles[0]);
                    mDrawerLayout.closeDrawer(mDrawerList);

                    fragment = new FragmentHouse();
                    ft.replace(R.id.frame_container, fragment, "House").commit();
                }
                break;
            case 1:
                if (fragment1 != null && fragment1.isVisible()) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    setTitle(navMenuTitles[1]);
                    mDrawerLayout.closeDrawer(mDrawerList);

                    fragment = new FragmentRecords();
                    ft.replace(R.id.frame_container, fragment, "Records").commit();
                }
                break;
            case 2:
                Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(str4));
                startActivity(intent4);
                break;
            case 3:
                if (premium) {
                    // Do nothing
                } else {
                    try {
                        buyPremium();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (SendIntentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String sku = null;
            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    sku = jo.getString("productId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (sku == "ad_free") {
                    editor.putBoolean("Premium", true).commit();
                } else if (sku == "minimal_house") {
                    editor.putBoolean("Minimal", true).commit();
                } else {
                    editor.putBoolean("Tribal", true).commit();
                }

                Toast.makeText(MainActivity.this, getString(R.string.purchaseok), Toast.LENGTH_LONG).show();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.purchasenot), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createFolder();
                    Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.purchasenot, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, ActivitySettings.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                Intent intent2 = new Intent(this, ActivityAboutUs.class);
                startActivity(intent2);
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
        mDrawerLayout.closeDrawer(mDrawerList);
        ft = getSupportFragmentManager().beginTransaction();

        // Fragments
        FragmentHouse fragment0 = (FragmentHouse) getSupportFragmentManager().findFragmentByTag("House");
        FragmentRecords fragment3 = (FragmentRecords) getSupportFragmentManager().findFragmentByTag("Records");

        // HouseLoops OnBackPressed
        if (fragment0 != null) {
            if (fragment0.isVisible()) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                MainActivity.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }

        // Records OnBackPressed
        if (fragment3 != null) {
            if (fragment3.isVisible()) {
                displayView(0);
            }
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}