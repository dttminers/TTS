package in.tts.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.BookMarkAdapter;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class BookmarkActivity extends AppCompatActivity {

    ArrayList<String> list;
    RecyclerView rv;
//    ListView listView;
    BookMarkAdapter adapter;
    LinearLayout linearLayout;
    SwipeRefreshLayout mSwipeRefreshLayout;
    PrefManager prefManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_bookmark);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BookmarkActivity.this, getString(R.string.str_bookmarks));
            }

            prefManager = new PrefManager(BookmarkActivity.this);
            list = prefManager.populateSelectedSearch();

//            listView = findViewById(R.id.listView);
            rv = findViewById(R.id.rvBookmarkList);
            linearLayout = findViewById(R.id.emptyList);

            mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadBookmarks();

                }
            });

            loadBookmarks();
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        int position, long id) {
//
//                    Object o = listView.getAdapter().getItem(position);
//                    if (o instanceof Map) {
//                        Map map = (Map) o;
//                        Intent in = new Intent(BookmarkActivity.this, BrowserActivity.class);
//                        in.putExtra("url", list.get(position));
//                        startActivity(in);
//                    }
//
//                }
//            });
//
//            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    Object o = listView.getAdapter().getItem(position);
//                    if (o instanceof Map) {
//                        Map map = (Map) o;
//                        deleteBookmark("Delete", list.get(position), position);
//                    }
//
//                    return true;
//                }
//            });
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    private void loadBookmarks() {
        try {
            if (list != null && list.size() > 0) {
                adapter = new BookMarkAdapter(BookmarkActivity.this, list);
                rv.setLayoutManager(new LinearLayoutManager(BookmarkActivity.this));
                rv.setHasFixedSize(true);
                rv.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
                rv.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            } else {
                rv.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }

        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    private void deleteBookmark(final String title, final String link, final int position) {

        new AlertDialog.Builder(this)
                .setTitle("DELETE")
                .setMessage("Confirm that you want to delete this bookmark?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        list.remove(position);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
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
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class LoadBookmarks {
    }
}
/*
package in.tts.activities;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.LinearLayout;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;

        import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;
        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

        import in.tts.R;
        import in.tts.utils.CommonMethod;

public class BookmarkActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> listRowData;

    public static String TAG_TITLE = "title";
    public static String TAG_LINK = "link";

    ListView listView;
    ListAdapter adapter;
    LinearLayout linearLayout;
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_bookmark);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BookmarkActivity.this, getString(R.string.str_bookmarks));
            }

            listView = findViewById(R.id.listView);
            linearLayout = findViewById(R.id.emptyList);

            mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new LoadBookmarks().execute();

                }
            });

            new LoadBookmarks().execute();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Object o = listView.getAdapter().getItem(position);
                    if (o instanceof Map) {
                        Map map = (Map) o;
                        Intent in = new Intent(BookmarkActivity.this, BrowserActivity.class);
                        in.putExtra("url", String.valueOf(map.get(TAG_LINK)));
                        startActivity(in);
                    }


                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object o = listView.getAdapter().getItem(i);
                    if (o instanceof Map) {
                        Map map = (Map) o;
                        deleteBookmark(String.valueOf(map.get(TAG_TITLE)), String.valueOf(map.get(TAG_LINK)));
                    }

                    return true;
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    private class LoadBookmarks extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

//                    SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
//                    String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
//                    String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);
//                    listRowData = new ArrayList<>();
//
//                    if (jsonLink != null && jsonTitle != null) {
//
//                        Gson gson = new Gson();
//                        ArrayList<String> linkArray = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
//                        }.getType());
//
//                        ArrayList<String> titleArray = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
//                        }.getType());
//
//
//                        for (int i = 0; i < linkArray.size(); i++) {
//                            HashMap<String, String> map = new HashMap<>();
//
//                            if (titleArray.get(i).length() == 0)
//                                map.put(TAG_TITLE, "Bookmark " + (i + 1));
//                            else
//                                map.put(TAG_TITLE, titleArray.get(i));
//
//                            map.put(TAG_LINK, linkArray.get(i));
//                            listRowData.add(map);
//                        }
//
//                        adapter = new SimpleAdapter(BookmarkActivity.this,
//                                listRowData, R.layout.bookmark_item,
//                                new String[]{TAG_TITLE, TAG_LINK},
//                                new int[]{R.id.title, R.id.link});
//
//                        listView.setAdapter(adapter);
//                    }
//
//                    linearLayout.setVisibility(View.VISIBLE);
//                    listView.setEmptyView(linearLayout);


                }
            });
            return null;
        }

        protected void onPostExecute(String args) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    private void deleteBookmark(final String title, final String link) {

        new AlertDialog.Builder(this)
                .setTitle("DELETE")
                .setMessage("Confirm that you want to delete this bookmark?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
//                        String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
//                        String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);
//
//
//                        if (jsonLink != null && jsonTitle != null) {
//
//
//                            Gson gson = new Gson();
//                            ArrayList<String> linkArray = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
//                            }.getType());
//
//                            ArrayList<String> titleArray = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
//                            }.getType());
//
//
//                            linkArray.remove(link);
//                            titleArray.remove(title);
//
//
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString(WEB_LINKS, new Gson().toJson(linkArray));
//                            editor.putString(WEB_TITLE, new Gson().toJson(titleArray));
//                            editor.apply();

//                            new LoadBookmarks().execute();
//                        }
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
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
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}


*/