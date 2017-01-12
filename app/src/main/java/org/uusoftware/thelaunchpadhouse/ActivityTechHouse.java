package org.uusoftware.thelaunchpadhouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ActivityTechHouse extends AppCompatActivity {

    int color = Color.parseColor("#01579B");
    int color2 = Color.parseColor("#0288D1");
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    Runnable runnable0;
    SoundPool mSoundPool = null;
    int[] soundId = new int[24];
    int[] streamId = new int[24];
    boolean[] play = new boolean[24];
    Button[] button = new Button[24];
    ProgressBar[] pb = new ProgressBar[24];
    CountDownTimer mCountDownTimer;
    Tracker t;
    Window window;
    ActionBar bar;
    ProgressBar progressbar;
    boolean isLoaded, isRecording, isPlaying= false;
    Drawable record;
    Animation mAnimation;
    MenuItem mRecord;
    private static final int RECORDER_BPP = 16;
    private static final String FILE_EXT = ".wav";
    private static final String THE_LOOPPAD_FOLDER = "The Looppad";
    private static final String RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private int bufferSize;
    private Thread recordingThread = null;
    AudioManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_techhouse);

        Arrays.fill(play, Boolean.FALSE);
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);

        // Theme
        bar = this.getSupportActionBar();
        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else {
            bar.setBackgroundDrawable(new ColorDrawable(color2));
        }

        // LoaderProgres
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            record = ContextCompat.getDrawable(this, R.drawable.ic_record);
        } else {
            record = getResources().getDrawable(R.drawable.ic_record);
        }

        // Theme
        SharedPreferences prefs = getSharedPreferences("Preferences", MODE_PRIVATE);
        boolean theme = prefs.getBoolean("Theme", false);
        PercentRelativeLayout lyt = (PercentRelativeLayout) findViewById(R.id.layout1);
        if (theme) {
            lyt.setBackgroundColor(Color.parseColor("#212121"));
            progressbar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        } else {
            progressbar.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }

        // Premium & AdMob
        boolean premium = MainActivity.premium;
        AdView adView = (AdView) findViewById(R.id.adMob);
        if (premium) {
            adView.setVisibility(View.GONE);
        } else {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("0A83AF9337EAE655A7B29C5B61372D84").build();
            adView.loadAd(adRequest);
        }

        // Analytics
        t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("Tech House");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        // Buttons
        button[0] = (Button) findViewById(R.id.button0);
        button[1] = (Button) findViewById(R.id.button1);
        button[2] = (Button) findViewById(R.id.button2);
        button[3] = (Button) findViewById(R.id.button3);
        button[4] = (Button) findViewById(R.id.button4);
        button[5] = (Button) findViewById(R.id.button5);
        button[6] = (Button) findViewById(R.id.button6);
        button[7] = (Button) findViewById(R.id.button7);
        button[8] = (Button) findViewById(R.id.button8);
        button[9] = (Button) findViewById(R.id.button9);
        button[10] = (Button) findViewById(R.id.button10);
        button[11] = (Button) findViewById(R.id.button11);
        button[12] = (Button) findViewById(R.id.button12);
        button[13] = (Button) findViewById(R.id.button13);
        button[14] = (Button) findViewById(R.id.button14);
        button[15] = (Button) findViewById(R.id.button15);
        button[16] = (Button) findViewById(R.id.button16);
        button[17] = (Button) findViewById(R.id.button17);
        button[18] = (Button) findViewById(R.id.button18);
        button[19] = (Button) findViewById(R.id.button19);
        button[20] = (Button) findViewById(R.id.button20);
        button[21] = (Button) findViewById(R.id.button21);
        button[22] = (Button) findViewById(R.id.button22);
        button[23] = (Button) findViewById(R.id.button23);

        // ProgressBars
        pb[0] = (ProgressBar) findViewById(R.id.progressBar0);
        pb[1] = (ProgressBar) findViewById(R.id.progressBar1);
        pb[2] = (ProgressBar) findViewById(R.id.progressBar2);
        pb[3] = (ProgressBar) findViewById(R.id.progressBar3);
        pb[4] = (ProgressBar) findViewById(R.id.progressBar4);
        pb[5] = (ProgressBar) findViewById(R.id.progressBar5);
        pb[6] = (ProgressBar) findViewById(R.id.progressBar6);
        pb[7] = (ProgressBar) findViewById(R.id.progressBar7);
        pb[8] = (ProgressBar) findViewById(R.id.progressBar8);
        pb[9] = (ProgressBar) findViewById(R.id.progressBar9);
        pb[10] = (ProgressBar) findViewById(R.id.progressBar10);
        pb[11] = (ProgressBar) findViewById(R.id.progressBar11);
        pb[12] = (ProgressBar) findViewById(R.id.progressBar12);
        pb[13] = (ProgressBar) findViewById(R.id.progressBar13);
        pb[14] = (ProgressBar) findViewById(R.id.progressBar14);
        pb[15] = (ProgressBar) findViewById(R.id.progressBar15);
        pb[16] = (ProgressBar) findViewById(R.id.progressBar16);
        pb[17] = (ProgressBar) findViewById(R.id.progressBar17);
        pb[18] = (ProgressBar) findViewById(R.id.progressBar18);
        pb[19] = (ProgressBar) findViewById(R.id.progressBar19);
        pb[20] = (ProgressBar) findViewById(R.id.progressBar20);
        pb[21] = (ProgressBar) findViewById(R.id.progressBar21);
        pb[22] = (ProgressBar) findViewById(R.id.progressBar22);
        pb[23] = (ProgressBar) findViewById(R.id.progressBar23);

        // SoundPool and SoundLoader
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

            mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(6).build();
        } else {
            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        SoundLoader rssTask = new SoundLoader();
        rssTask.execute();

        // Check objects are loaded
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                for (byte i = 0; i <= 23; i++) {
                    if (i <= 22) {
                        if (sampleId == soundId[i]) {
                            button[i].setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (sampleId == soundId[i]) {
                            button[23].setVisibility(View.VISIBLE);
                            progressbar.setVisibility(View.GONE);
                            isLoaded = true;
                        }
                    }
                }
            }
        });

        // CountDownTimer
        mCountDownTimer = new CountDownTimer(4000, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                for (byte i = 0; i <= 23; i++) {
                    pb[i].setProgress((int) (4000 - millisUntilFinished));
                }
            }

            @Override
            public void onFinish() {
                for (byte i = 0; i <= 23; i++) {
                    pb[i].setProgress(0);
                }
                mCountDownTimer.start();
            }
        };

        // Animation
        mAnimation = new AlphaAnimation(1F, 0.5F);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(250);
        mAnimation.setRepeatCount(2);

        OnClickListener buttonListener = new OnClickListener() {
            public void onClick(final View v) {
                if (isLoaded) {
                    switch (v.getId()) {
                        case R.id.button0:
                            if (!play[0]) {
                                startAudio(0, 1, 4, 5);
                            } else {
                                stopAudio(0);
                            }
                            break;
                        case R.id.button1:
                            if (!play[1]) {
                                startAudio(1, 0, 4, 5);
                            } else {
                                stopAudio(1);
                            }
                            break;
                        case R.id.button2:
                            if (!play[2]) {
                                startAudio(2, 3, 6, 7);
                            } else {
                                stopAudio(2);
                            }
                            break;
                        case R.id.button3:
                            if (!play[3]) {
                                startAudio(3, 2, 6, 7);
                            } else {
                                stopAudio(3);
                            }
                            break;
                        case R.id.button4:
                            if (!play[4]) {
                                startAudio(4, 0, 1, 5);
                            } else {
                                stopAudio(4);
                            }
                            break;
                        case R.id.button5:
                            if (!play[5]) {
                                startAudio(5, 0, 1, 4);
                            } else {
                                stopAudio(5);
                            }
                            break;
                        case R.id.button6:
                            if (!play[6]) {
                                startAudio(6, 2, 3, 7);
                            } else {
                                stopAudio(6);
                            }
                            break;
                        case R.id.button7:
                            if (!play[7]) {
                                startAudio(7, 2, 3, 6);
                            } else {
                                stopAudio(7);
                            }
                            break;
                        case R.id.button8:
                            if (!play[8]) {
                                startAudio(8, 9, 12, 13);
                            } else {
                                stopAudio(8);
                            }
                            break;
                        case R.id.button9:
                            if (!play[9]) {
                                startAudio(9, 8, 12, 13);
                            } else {
                                stopAudio(9);
                            }
                            break;
                        case R.id.button10:
                            if (!play[10]) {
                                startAudio(10, 11, 14, 15);
                            } else {
                                stopAudio(10);
                            }
                            break;
                        case R.id.button11:
                            if (!play[11]) {
                                startAudio(11, 10, 14, 15);
                            } else {
                                stopAudio(11);
                            }
                            break;
                        case R.id.button12:
                            if (!play[12]) {
                                startAudio(12, 8, 9, 13);
                            } else {
                                stopAudio(12);
                            }
                            break;
                        case R.id.button13:
                            if (!play[13]) {
                                startAudio(13, 8, 9, 12);
                            } else {
                                stopAudio(13);
                            }
                            break;
                        case R.id.button14:
                            if (!play[14]) {
                                startAudio(14, 10, 11, 15);
                            } else {
                                stopAudio(14);
                            }
                            break;
                        case R.id.button15:
                            if (!play[15]) {
                                startAudio(15, 10, 11, 14);
                            } else {
                                stopAudio(15);
                            }
                            break;
                        case R.id.button16:
                            if (!play[16]) {
                                startAudio(16, 17, 20, 21);
                            } else {
                                stopAudio(16);
                            }
                            break;
                        case R.id.button17:
                            if (!play[17]) {
                                startAudio(17, 16, 20, 21);
                            } else {
                                stopAudio(17);
                            }
                            break;
                        case R.id.button18:
                            if (!play[18]) {
                                startAudio(18, 19, 22, 23);
                            } else {
                                stopAudio(18);
                            }
                            break;
                        case R.id.button19:
                            if (!play[19]) {
                                startAudio(19, 18, 22, 23);
                            } else {
                                stopAudio(19);
                            }
                            break;
                        case R.id.button20:
                            if (!play[20]) {
                                startAudio(20, 16, 17, 21);
                            } else {
                                stopAudio(20);
                            }
                            break;
                        case R.id.button21:
                            if (!play[21]) {
                                startAudio(21, 16, 17, 20);
                            } else {
                                stopAudio(21);
                            }
                            break;
                        case R.id.button22:
                            if (!play[22]) {
                                startAudio(22, 18, 19, 23);
                            } else {
                                stopAudio(22);
                            }
                            break;
                        case R.id.button23:
                            if (!play[23]) {
                                startAudio(23, 18, 19, 22);
                            } else {
                                stopAudio(23);
                            }
                            break;
                    }
                    if (!isPlaying) {
                        runnable0 = new Runnable() {
                            @Override
                            public void run() {
                                for (byte i = 0; i <= 23; i++) {
                                    if (play[i]) {
                                        streamId[i] = mSoundPool.play(soundId[i], 1, 1, 1, 0, 1.0f);
                                    } else {
                                        mSoundPool.stop(streamId[i]);
                                    }
                                }
                            }
                        };
                        executor.scheduleAtFixedRate(runnable0, 0, 4000, TimeUnit.MILLISECONDS);
                        mCountDownTimer.start();
                        isPlaying = true;
                    }
                } else {
                    Toast.makeText(ActivityTechHouse.this, getString(R.string.loading), Toast.LENGTH_SHORT).show();
                }
            }
        };

        for (byte i = 0; i <= 23; i++) {
            button[i].setOnClickListener(buttonListener);
        }
    }

    class SoundLoader extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            // Load the sample IDs
            soundId[0] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_bass_1, 1);
            soundId[1] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_bass_2, 1);
            soundId[4] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_bass_3, 1);
            soundId[5] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_bass_4, 1);
            soundId[2] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_fx_1, 1);
            soundId[3] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_fx_2, 1);
            soundId[6] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_fx_3, 1);
            soundId[7] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_fx_4, 1);
            soundId[8] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_kick, 1);
            soundId[9] = soundId[8];
            soundId[12] = soundId[8];
            soundId[13] = soundId[8];
            soundId[10] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_lead_1, 1);
            soundId[11] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_lead_2, 1);
            soundId[14] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_lead_3, 1);
            soundId[15] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_lead_4, 1);
            soundId[16] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_loop_1, 1);
            soundId[17] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_loop_2, 1);
            soundId[20] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_loop_3, 1);
            soundId[21] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_loop_4, 1);
            soundId[18] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_perc_1, 1);
            soundId[19] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_perc_2, 1);
            soundId[22] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_perc_3, 1);
            soundId[23] = mSoundPool.load(ActivityTechHouse.this, R.raw.techhouse_perc_4, 1);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public void startAudio(final int i, final int j, final int k, final int l) {
        pb[i].setVisibility(View.VISIBLE);
        pb[j].setVisibility(View.INVISIBLE);
        pb[k].setVisibility(View.INVISIBLE);
        pb[l].setVisibility(View.INVISIBLE);

        button[i].startAnimation(mAnimation);
        button[j].clearAnimation();
        button[k].clearAnimation();
        button[l].clearAnimation();

        play[i] = true;
        play[j] = false;
        play[k] = false;
        play[l] = false;
    }

    public void stopAudio(final int i) {
        pb[i].setVisibility(View.INVISIBLE);
        button[i].clearAnimation();
        play[i] = false;
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), THE_LOOPPAD_FOLDER);
        CharSequence filename = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date());
        return (file.getPath() + File.separator + filename + FILE_EXT);
    }

    private String getTempFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), THE_LOOPPAD_FOLDER);
        return (file.getPath() + File.separator + RECORDER_TEMP_FILE);
    }

    private void startRecording() {
        isRecording = true;
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat.CHANNEL_IN_STEREO,
                RECORDER_AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, RECORDER_SAMPLERATE,
                AudioFormat.CHANNEL_OUT_STEREO, RECORDER_AUDIO_ENCODING, bufferSize);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            NoiseSuppressor.create(recorder.getAudioSessionId());
            AutomaticGainControl.create(recorder.getAudioSessionId());
            AcousticEchoCanceler.create(recorder.getAudioSessionId());
        } else {
            am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            am.setParameters("noise_suppression=auto");
        }
        int i = recorder.getState();
        if (i == 1) {
            recorder.startRecording();
            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    writeAudioDataToFile();
                }
            }, "AudioRecorder Thread");
            recordingThread.start();
        } else {
            System.out.println(i);
        }
        record.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;
        int read = 0;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (null != os) {
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecording() {
        if (null != recorder) {
            isRecording = false;
            int i = recorder.getState();
            if (i == 1) {
                recorder.stop();
                recorder.release();

                recorder = null;
                recordingThread = null;
            } else {
                System.out.println(i);
            }
        }
        copyWaveFile(getTempFilename(), getFilename());
        deleteTempFile();
        record.clearColorFilter();
        Toast.makeText(ActivityTechHouse.this, getString(R.string.record_saved), Toast.LENGTH_SHORT).show();
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;
        byte[] data = new byte[bufferSize];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());
        file.delete();
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    private void killSoundPoolandExecutor() {
        mSoundPool.release();
        executor.shutdown();
        mSoundPool = null;
        executor = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main4, menu);
        mRecord = menu.findItem(R.id.action_record);
        mRecord.setIcon(record);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isRecording) {
                    stopRecording();
                }
                if (mSoundPool != null) {
                    killSoundPoolandExecutor();
                }
                finish();
                return true;
            case R.id.action_record:
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
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
        if (isRecording) {
            stopRecording();
        }
        if (mSoundPool != null) {
            killSoundPoolandExecutor();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSoundPool == null) {
            Intent intent = new Intent(this, ActivityDeepHouse.class);
            startActivity(intent);
            finish();
        } else {
            // Do nothing
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isRecording) {
            stopRecording();
        }
        if (mSoundPool != null) {
            killSoundPoolandExecutor();
        }
        finish();
    }
}