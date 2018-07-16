package in.tts.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.metrics.AddTrace;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import in.tts.R;
import in.tts.activities.LoginActivity;
import in.tts.activities.MainActivity;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.utils.CommonMethod;

public class LoginFragment extends Fragment {

    private TextView mTvLogin;

    // Google
    private GoogleSignInClient mGoogleSignInClient;
    private RelativeLayout relativeLayoutGoogle;

    //Facebook
    private RelativeLayout relativeLayoutFb;
    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginManager mFbLoginManager;

    // Parameters
    private int RC_SIGN_IN = 123;
    private static final String EMAIL = "email";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    @AddTrace(name = "onCreateLoginFragment", enabled = true)
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "Login", null);

            FacebookSdk.sdkInitialize(getContext());

            mTvLogin = getActivity().findViewById(R.id.txtLogin);

            SpannableString ss1 = new SpannableString(getString(R.string.str_login_data));
            ss1.setSpan(new RelativeSizeSpan(1.5f), 38, 50, 0); // set size
//            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 38, 49, 0);// set color
            mTvLogin.setText(ss1);

            // View
            getActivity().findViewById(R.id.llSignUp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        getContext().startActivity(new Intent(getContext(), LoginActivity.class).putExtra("LOGIN", "register"));
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
            });
            getActivity().findViewById(R.id.txtSkipLogin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        getContext().startActivity(new Intent(getContext(), MainActivity.class));
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
            });

            // Google
            relativeLayoutGoogle = getActivity().findViewById(R.id.rlGoogleLogin);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
            relativeLayoutGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
            });
            // Google

            //Facebook
            callbackManager = CallbackManager.Factory.create();

            mFbLoginManager = LoginManager.getInstance();

            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                    Log.d("TAG", " fb 5 " + oldToken + " : " + newToken);
                }
            };

            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
//                    displayMessage(newProfile);
                    Log.d("TAG", " fb 6 " + oldProfile + " : " + newProfile);
                    Log.d("TAG", " fb 7 " + newProfile.getId() + " : " + newProfile.getFirstName());

                }
            };

            relativeLayoutFb = getActivity().findViewById(R.id.rlFacebookLogin);
            relativeLayoutFb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", " fb 1 " + mFbLoginManager.getAuthType() + AccessToken.getCurrentAccessToken() + " : " + accessTokenTracker.isTracking() + profileTracker.isTracking());
                    if (accessTokenTracker.isTracking()) {
                        Log.d("TAG", " fb 2 " + accessTokenTracker.isTracking());
                        mFbLoginManager.logOut();
                        accessTokenTracker.stopTracking();
                        profileTracker.stopTracking();
                    } else {
                        Log.d("TAG", " fb 3 " + accessTokenTracker.isTracking());
                        accessTokenTracker.startTracking();
                        mFbLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));//, "user_birthday"));
                        mFbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.d("TAG", " 00 fb " + loginResult.getAccessToken());
                                AccessToken accessToken = loginResult.getAccessToken();
                                ProfileTracker profileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                        if (currentProfile != null) {
                                            this.stopTracking();
                                            Profile.setCurrentProfile(currentProfile);
                                            User user = User.getUser(getContext());
                                            user.setId(currentProfile.getId());
                                            user.setName1(currentProfile.getFirstName());
                                            user.setName2(currentProfile.getLastName() + currentProfile.getMiddleName());
                                            user.setName(currentProfile.getName());
                                            user.setPicPath(currentProfile.getProfilePictureUri(1000, 1000).toString());
                                            toExit();
                                        }
                                    }
                                };
                                profileTracker.startTracking();
                            }

                            @Override
                            public void onCancel() {
                                Log.d("TAG", " fb is cancel");

                            }

                            @Override
                            public void onError(FacebookException e) {
                                // here write code when get error
                                Log.d("TAG", "fb onError " + e.getMessage());
                            }
                        });
                    }
                }
            });

            accessTokenTracker.startTracking();
            profileTracker.startTracking();
            //Facebook

        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", " fb  result " + resultCode + ":" + requestCode + " :");
        // Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Log.d("TAG", " Name : " + account.getDisplayName());
                Log.d("TAG", " ID : " + account.getId());
                User user = User.getUser(getContext());
                user.setEmail(account.getEmail());
                user.setId(account.getId());
                user.setFcmToken(account.getIdToken());
                user.setName(account.getDisplayName());
                user.setPicPath(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);
                user.setName1(account.getGivenName());
                user.setName2(account.getFamilyName());
                toExit();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    // Called when successfully logged in
    private void toExit() {
        try {
            new PrefManager(getContext()).setUserInfo();
            startActivity(new Intent(getContext(), MainActivity.class));
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }

    private OnFragmentInteractionListener mListener;

    public static LoginFragment newInstance() {
        return new LoginFragment();
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
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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

}