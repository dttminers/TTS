package in.tts.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.List;

import in.tts.R;
import in.tts.adapters.BookMarkAdapter;
import in.tts.model.BookmarkModel;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class BookmarkActivity extends AppCompatActivity {

    private List<BookmarkModel> bookmarkModelList;
    private RecyclerView rv;
    private BookMarkAdapter adapter;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PrefManager prefManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_bookmark);
            PrefManager.ActivityCount = +1;

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BookmarkActivity.this, getString(R.string.str_bookmarks));
            }

            prefManager = new PrefManager(BookmarkActivity.this);
            bookmarkModelList = prefManager.loadList();

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
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void loadBookmarks() {
        try {
            if (bookmarkModelList != null && bookmarkModelList.size() > 0) {
                adapter = new BookMarkAdapter(BookmarkActivity.this, bookmarkModelList);
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
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
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
        try {
            finish();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

}