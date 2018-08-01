package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.PdfListAdapter;
import in.tts.utils.CommonMethod;

public class MyBooksListFragment extends Fragment {

    private ArrayList<String> file;
    private PdfListAdapter pdfListAdapter;
    private RecyclerView recyclerView;
    private LinearLayout llCustom_loader;

    private OnFragmentInteractionListener mListener;

    public MyBooksListFragment() {
        // Required empty public constructor
    }

    public static MyBooksListFragment newInstance() {
        return new MyBooksListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_books_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
        try {
            file = new ArrayList<>();

            recyclerView = getActivity().findViewById(R.id.rvList);
            llCustom_loader = getActivity().findViewById(R.id.llCustom_loader);

            fn_permission();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toGetData();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void toGetData() {
        try {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        recyclerView.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(layoutManager);
//                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
//                        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.sk_line_divider));
//                        recyclerView.addItemDecoration(dividerItemDecoration);

                        pdfListAdapter = new PdfListAdapter(getActivity(), file);
//                recyclerView.setAdapter(pdfListAdapter);
                        pdfListAdapter.notifyDataSetChanged();

                        new toGet().execute();

                        llCustom_loader.setVisibility(View.GONE);
                    }
                });

            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
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
                        if (listFile[i].getName().endsWith(".pdf")) {
                            for (int j = 0; j < file.size(); j++) {
                                if (file.get(j).equals(listFile[i].getPath())) {
                                    booleanpdf = true;
                                } else {
                                }
                            }
                            if (booleanpdf) {
                                booleanpdf = false;
                            } else {
                                file.add(listFile[i].getPath());
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class toGet extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(pdfListAdapter);
            pdfListAdapter.notifyDataSetChanged();
        }
    }
}