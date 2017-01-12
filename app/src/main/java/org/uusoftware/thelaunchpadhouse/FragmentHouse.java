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

    ImageView button0, button1, button2, button3, button4, button5, button6, button7;
    int color = Color.parseColor("#000000");
    int color2 = Color.parseColor("#212121");
    Window window;
    ActionBar bar;
    Tracker t;
    boolean premium, tribal, minimal;
    static Intent intent;
    long a, b;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_house, container, false);

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
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
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
                            showAds(0);
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail2:
                        intent = new Intent(getActivity(), ActivityTechHouse.class);
                        if (!premium) {
                            showAds(1);
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail3:
                        intent = new Intent(getActivity(), ActivityReggaeHouse.class);
                        if (!premium) {
                            showAds(2);
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail4:
                        intent = new Intent(getActivity(), ActivityTechnoHouse.class);
                        if (!premium) {
                            showAds(3);
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail5:
                        intent = new Intent(getActivity(), ActivityFutureHouse.class);
                        if (!premium) {
                            showAds(4);
                        } else {
                            getActivity().startActivity(intent);
                        }
                        break;
                    case R.id.img_thumbnail6:
                        if (tribal) {
                            intent = new Intent(getActivity(), ActivityTribalHouse.class);
                            if (!premium) {
                                showAds(5);
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
                                showAds(6);
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

            public void showAds(int position) {
                boolean displayed = MainActivity.displayed;
                boolean displayed2 = MainActivity.displayed2;
                boolean displayed3 = MainActivity.displayed3;
                boolean displayed4 = MainActivity.displayed4;
                a = MainActivity.start;
                b = System.currentTimeMillis();

                if (b - a >= 10000 && !displayed) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.displayAds();
                } else if (b - a >= 60000 && !displayed2) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.displayAds2();
                } else if (b - a >= 180000 && !displayed3) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.displayAds3();
                } else if (b - a >= 360000 && !displayed4) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.displayAds4();
                } else {
                    getActivity().startActivity(intent);
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