package org.uusoftware.thelaunchpadhouse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.uusoftware.thelaunchpadhouse.model.GridItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentRecords extends Fragment {
    int color = Color.parseColor("#000000");
    int color2 = Color.parseColor("#212121");
    File f;
    File file[];

    @SuppressLint("InlinedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_records, container, false);

        // Theme
        ActionBar bar = ((MainActivity) getActivity()).getSupportActionBar();
        Window window = getActivity().getWindow();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
        }

        // Premium & AdMob
        boolean premium = MainActivity.premium;
        AdView adView = (AdView) v.findViewById(R.id.adMob);
        if (premium) {
            adView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("0A83AF9337EAE655A7B29C5B61372D84").build();
            adView.loadAd(adRequest);
        }

        // Analytics
        Tracker t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Records");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        f = new File(Environment.getExternalStorageDirectory(), "The Looppad");
        file = f.listFiles();

        List<GridItem> feedsList = new ArrayList<>();
        if (file == null || file.length == 0) {
            Toast.makeText(getActivity(), getString(R.string.norecord), Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < file.length; i++) {
                GridItem item = new GridItem();
                item.setTitle(file[i].getName());
                feedsList.add(item);
            }
        }

        // Adapter
        RecyclerView.Adapter<GridAdapter.ViewHolder> mAdapter = new GridAdapter(getActivity(), feedsList);
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(final int position) {
                if ((position % 3) == 0) {
                    return (2);
                } else {
                    return (1);
                }
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
        private List<GridItem> feedItemList;

        public GridAdapter(Context context, List<GridItem> feedItemList) {
            this.feedItemList = feedItemList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            GridItem feedItem = feedItemList.get(i);
            // Setting image view image
            if ((i % 3) == 0) {
                viewHolder.image.setBackgroundResource(R.drawable.ic_records2);
            } else {
                viewHolder.image.setBackgroundResource(R.drawable.ic_records);
            }

            // Setting text view title
            viewHolder.text.setText(feedItem.getTitle());

            // Handle click event on both title and image click
            viewHolder.text.setOnClickListener(clickListener);
            viewHolder.image.setOnClickListener(clickListener);

            viewHolder.text.setTag(viewHolder);
            viewHolder.image.setTag(viewHolder);
        }

        @Override
        public int getItemCount() {
            return (null != feedItemList ? feedItemList.size() : 0);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView image;
            public TextView text;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.img_thumbnail);
                text = (TextView) itemView.findViewById(R.id.txt_text);
            }
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) view.getTag();
                final int position = holder.getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.chooseaction);
                builder.setItems(R.array.choose_actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            playFile(file[position]);
                        } else if (which == 1) {
                            shareFile(file[position]);
                        } else {
                            deleteFile(file[position]);
                        }
                    }
                });
                builder.show();
            }
        };
    }

    private void playFile(File file) {
        Intent intent = new Intent(getActivity(), ActivityMediaPlayer.class);
        intent.putExtra("uri", file.getAbsolutePath());
        startActivity(intent);
    }

    private void shareFile(File file) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(share, null));
    }

    private void deleteFile(File file) {
        file.delete();
        Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
        FragmentRecords fragment = (FragmentRecords) getActivity().getSupportFragmentManager()
                .findFragmentByTag("Records");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        fragment = new FragmentRecords();
        ft.replace(R.id.frame_container, fragment, "Records").addToBackStack(null).commit();
    }
}