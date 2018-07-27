package in.tts.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.RecentVoiceAdapter;
import in.tts.model.Audio;
import in.tts.model.AudioModel;
import in.tts.services.MediaPlayerService;
import in.tts.utils.AlertDialogHelper;
import in.tts.utils.CommonMethod;
//import in.tts.utils.RecyclerItemClickListener;
import in.tts.utils.RecyclerItemClickListener;
import in.tts.utils.StorageUtils;

public class RecentVoiceActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener {

    public static final String Broadcast_PLAY_NEW_AUDIO = "audioplayer.PlayNewAudio";

    boolean isMultiSelect = false;

    private ActionMode mActionMode;
    private Menu context_menu;
    private ArrayList<Audio> audioList;

    private RecentVoiceAdapter mAdapter;
    private ArrayList<AudioModel> user_list = new ArrayList<>();
    private ArrayList<AudioModel> multiselect_list = new ArrayList<>();

    private AlertDialogHelper alertDialogHelper;

    private RecyclerView recyclerView;

    private MediaPlayerService player;
    private boolean serviceBound = false;

    @Override
    @AddTrace(name = "onCreateAudioSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_voice);
        CommonMethod.setAnalyticsData(RecentVoiceActivity.this, "MainTab", "Recent Voice", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), RecentVoiceActivity.this, getString(R.string.str_title_recent_voice));
        }

        alertDialogHelper = new AlertDialogHelper(this);

        getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));

        recyclerView = findViewById(R.id.recycleView);
        mAdapter = new RecentVoiceAdapter(RecentVoiceActivity.this, user_list, multiselect_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        // Listening the click events

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                } else {
                    playAudio(user_list.get(position).getText());
//                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
//                    multiselect_list = new ArrayList<AudioModel>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                    Log.d("TAG", "Audio : " + position + ":" + user_list.get(position).isSelected());
                    view.findViewById(R.id.ivSelected).setVisibility(user_list.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
                }

                multi_select(position);

            }
        }));

        loadAudio();
//        if (audioList != null && audioList.size() > 0) {
        //play the first audio in the ArrayList
//            playAudio(audioList.get(0).getData());
//        }
        // playAudio(file.get(0));
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(user_list.get(position)))
                multiselect_list.remove(user_list.get(position));
            else
                multiselect_list.add(user_list.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");


            refreshAdapter();

        }
    }

    public void refreshAdapter() {
        mAdapter.selected_usersList = multiselect_list;
        mAdapter.audioList = user_list;
        mAdapter.setData();
        mAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.selected_audio_menu, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("", "Delete Contact", "DELETE", "CANCEL", 1, false);
                    return true;
                case R.id.action_share:
//                    String sharePath = Environment.getExternalStorageDirectory().getPath()
//                            + "/Soundboard/Ringtones/custom_ringtone.ogg";
//                    Uri uri = Uri.parse(sharePath);
//                    Intent share = new Intent(Intent.ACTION_SEND);
//                    share.setType("audio/*");
//                    for (int i = 0; i < user_list.size(); i++) {
//                        if () {
//                            share.putExtra(Intent.EXTRA_STREAM, uri);
//                        }
//                    }
//                    startActivity(Intent.createChooser(share, "Share Sound File"));
                    if (multiselect_list.size() > 0) {
//                        Intent share = new Intent(Intent.ACTION_SEND);
//                        share.setType("audio/*");
//                        for (int i = 0; i < multiselect_list.size(); i++) {
//                            File fdelete = new File(multiselect_list.get(i).getText());
//                            if (fdelete.exists()) {
////                                if (fdelete.delete()) {
//                                Log.d("TAG", "file Deleted :" + fdelete.getPath());
////                                user_list.remove(multiselect_list.get(i));
////                                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fdelete));
//                                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + multiselect_list.get(i).getText()));
//                            } else {
////                                    Log.d("TAG", "file not Deleted :" + fdelete.getPath());
//                                CommonMethod.toDisplayToast(RecentVoiceActivity.this, " Sorry, unable to share file");
////                                }
//                            }
//                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */

                        ArrayList<Uri> files = new ArrayList<Uri>();

//                        for(String path : filesToSend /* List of the files you want to send */) {
                        for (int i = 0; i < multiselect_list.size(); i++) {
//                            File file = new File(path);
                            File file = new File(multiselect_list.get(i).getText());
                            Uri uri = Uri.fromFile(file);
                            files.add(uri);
                        }

                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

                        startActivity(Intent.createChooser(intent, "Share Sound File"));
                    } else {
                        CommonMethod.toDisplayToast(RecentVoiceActivity.this, " No File selected to share");
                    }
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<>();
            refreshAdapter();
        }
    };

    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if (from == 1) {
            if (multiselect_list.size() > 0) {
                for (int i = 0; i < multiselect_list.size(); i++) {
                    File fdelete = new File(multiselect_list.get(i).getText());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Log.d("TAG", "file Deleted :" + fdelete.getPath());
                            user_list.remove(multiselect_list.get(i));

                        } else {
                            Log.d("TAG", "file not Deleted :" + fdelete.getPath());
                        }
                    }
                }
//            for (int i = 0; i < user_list.size(); i++) {
//                if (user_list.get(i).isSelected()) {
//                    File fdelete = new File(user_list.get(i).getText());
//                    if (fdelete.exists()) {
//                        Log.d("TAG", "file Deleted :" + fdelete.getPath());
//                        user_list.remove(i);
//                    } else {
//                        Log.d("TAG", "file not Deleted :" + i + fdelete.getPath());
//                    }
//                }
//            }

                mAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
//                Toast.makeText(getApplicationContext(), "Delete Click", Toast.LENGTH_SHORT).show();
            }
        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }

//            AudioModel mSample = new AudioModel("Name" + user_list.size(), "Designation" + user_list.size());
//            user_list.add(mSample);
            mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }


    public void getFile(final File dir) {
        try {
            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isDirectory()) {
                        getFile(listFile[i]);
                    } else {
                        boolean booleanpdf = false;
                        if (listFile[i].getName().endsWith(".wav")) {//|| listFile[i].getName().endsWith(".mp3"))
                            for (int j = 0; j < user_list.size(); j++) {
                                if (user_list.get(j).equals(listFile[i].getPath())) {
                                    booleanpdf = true;
//                                } else {
                                }
                            }
                            if (booleanpdf) {
                                booleanpdf = false;
                            } else {
                                Log.d("TAG", " Audio File :" + listFile[i].getAbsolutePath());
//                                file.add(listFile[i].getPath());
                                user_list.add(new AudioModel(listFile[i].getPath()));
                            }
                        }
                    }


                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(RecentVoiceActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(String media) {
        Log.d("TAG", "Media : " + media + ":" + serviceBound);
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtils storage = new StorageUtils(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtils storage = new StorageUtils(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                Log.d("TAG", " AUDIO DATA : " + data + " : " + title + " : " + album + " : " + artist);
                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }
            playAudio(audioList.get(0).getData());
        }
        cursor.close();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}