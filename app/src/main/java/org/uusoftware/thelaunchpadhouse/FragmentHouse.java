package org.uusoftware.thelaunchpadhouse;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class FragmentHouse extends Fragment {

    static Intent intent;
    ImageView button0, button1, button2, button3, button4, button5, button6, button7;
    int color = Color.parseColor("#000000");
    int color2 = Color.parseColor("#212121");
    Window window;
    ActionBar bar;
    Tracker t;
    boolean premium, tribal, minimal;
    long a, b;
    boolean displayed, displayed2, displayed3, displayed4 = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_house, container, false);

        a = System.currentTimeMillis();
        b = System.currentTimeMillis();

        // Theme
        bar = ((MainActivity) getActivity()).getSupportActionBar();
        window = getActivity().getWindow();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
        }

        // Premium & AdMob
        premium = MainActivity.premium;
        AdView adView = (AdView) rootView.findViewById(R.id.adMob);
        if (premium) {
            adView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("0A83AF9337EAE655A7B29C5B61372D84").build();
            adView.loadAd(adRequest);
        }

        // Analytics
        t = ((ActivityAnalytics) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("House Loops");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        tribal = MainActivity.tribal;
        minimal = MainActivity.minimal;

        button0 = (ImageView) rootView.findViewById(R.id.img_thumbnail);
        button1 = (ImageView) rootView.findViewById(R.id.img_thumbnail2);
        button2 = (ImageView) rootView.findViewById(R.id.img_thumbnail3);
        button3 = (ImageView) rootView.findViewById(R.id.img_thumbnail4);
        button4 = (ImageView) rootView.findViewById(R.id.img_thumbnail5);
        button5 = (ImageView) rootView.findViewById(R.id.img_thumbnail6);
        button6 = (ImageView) rootView.findViewById(R.id.img_thumbnail7);
        button7 = (ImageView) rootView.findViewById(R.id.img_thumbnail8);

        OnClickListener buttonListener = new OnClickListener() {
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.img_thumbnail:
                        intent = new Intent(getActivity(), ActivityDeepHouse.class);
                        if (!premium) {
                            showAds();
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail2:
                        intent = new Intent(getActivity(), ActivityTechHouse.class);
                        if (!premium) {
                            showAds();
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail3:
                        intent = new Intent(getActivity(), ActivityReggaeHouse.class);
                        if (!premium) {
                            showAds();
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail4:
                        intent = new Intent(getActivity(), ActivityTechnoHouse.class);
                        if (!premium) {
                            showAds();
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail5:
                        intent = new Intent(getActivity(), ActivityFutureHouse.class);
                        if (!premium) {
                            showAds();
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail6:
                        if (tribal) {
                            intent = new Intent(getActivity(), ActivityTribalHouse.class);
                            if (!premium) {
                                showAds();
                            } else {
                                getActivity().startActivity(intent);
                            }
                        } else {
                            try {
                                MainActivity.buyTribal(getActivity());
                            } catch (RemoteException | SendIntentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.img_thumbnail7:
                        if (minimal) {
                            intent = new Intent(getActivity(), ActivityMinimalHouse.class);
                            if (!premium) {
                                showAds();
                            } else {
                                getActivity().startActivity(intent);
                            }
                        } else {
                            try {
                                MainActivity.buyMinimal(getActivity());
                            } catch (RemoteException | SendIntentException e) {
                                // TODOAuto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.img_thumbnail8:
                        Toast.makeText(getActivity(), getString(R.string.comingsoon), Toast.LENGTH_LONG).show();
                        break;
                }
            }

            public void showAds() {
                getActivity().startActivity(intent);
                if (b - a >= 10000 && !displayed) {
                    MainActivity.interstitial.show();
                    displayed = true;
                } else if (b - a >= 60000 && !displayed2) {
                    MainActivity.interstitial.show();
                    displayed2 = true;
                } else if (b - a >= 180000 && !displayed3) {
                    MainActivity.interstitial.show();
                    displayed3 = true;
                } else if (b - a >= 360000 && !displayed4) {
                    MainActivity.interstitial.show();
                    displayed4 = true;
                } else {
                    //Do nothing
                }
            }
        };

        button0.setOnClickListener(buttonListener);
        button1.setOnClickListener(buttonListener);
        button2.setOnClickListener(buttonListener);
        button3.setOnClickListener(buttonListener);
        button4.setOnClickListener(buttonListener);
        button5.setOnClickListener(buttonListener);
        button6.setOnClickListener(buttonListener);
        button7.setOnClickListener(buttonListener);

        return rootView;
    }
}