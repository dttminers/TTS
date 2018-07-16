package in.tts.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.gson.Gson;

import java.util.Arrays;

import in.tts.R;
import in.tts.activities.LoginActivity;
import in.tts.activities.MainActivity;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.utils.CommonMethod;

public class RegisterFragment extends Fragment {

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

    public RegisterFragment() {
    }

    @Override

    @AddTrace(name = "onCreateRegisterFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.setAnalyticsData(getContext(), "MainTab", "Register", null);
        try {
            FacebookSdk.sdkInitialize(getContext());
            //View
            getActivity().findViewById(R.id.txtAlreadyAccountReg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Loading....");
                        getContext().startActivity(new Intent(getContext(), LoginActivity.class).putExtra("LOGIN", "login"));
                        getActivity().finish();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                    }
                }
            });
            getActivity().findViewById(R.id.txtSkipRegisterReg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Loading....");
                        getContext().startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
//                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        CommonMethod.toCloseLoader();
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
                        CommonMethod.toCallLoader(getContext(), "Login in with Facebook ");
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        Log.d("TAG", " Login" + e.getMessage());
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
                    CommonMethod.toCallLoader(getContext(), "Login with Facebook ");
                    Log.d("TAG", " fb 1 " + mFbLoginManager.getAuthType() + AccessToken.getCurrentAccessToken() + " : " + accessTokenTracker.isTracking() + profileTracker.isTracking());
                    if (accessTokenTracker.isTracking()) {
                        Log.d("TAG", " fb 2 " + accessTokenTracker.isTracking());
                        mFbLoginManager.logOut();
                        accessTokenTracker.stopTracking();
                        profileTracker.stopTracking();
                        CommonMethod.toCloseLoader();
                        CommonMethod.toDisplayToast(getContext(), " Click again  to Register");
                    } else {
                        Log.d("TAG", " fb 3 " + accessTokenTracker.isTracking());
                        accessTokenTracker.startTracking();
                        mFbLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));//, "user_birthday"));
                        mFbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                try {

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
                                                user.setLoginFrom(2);
                                                user.setPicPath(currentProfile.getProfilePictureUri(1000, 1000).toString());
                                                Log.d("TAG", "Userinfo fb  " + new Gson().toJson(User.getUser(getContext())));
                                                CommonMethod.toCloseLoader();
                                                toExit();
                                            }
                                        }
                                    };
                                    profileTracker.startTracking();
                                } catch (Exception | Error e) {
                                    CommonMethod.toCloseLoader();
                                    e.printStackTrace();
                                    Crashlytics.logException(e);
                                    CommonMethod.toDisplayToast(getContext(), " Click again  to Register");
                                }
                            }

                            @Override
                            public void onCancel() {
                                Log.d("TAG", " fb is cancel");
                                CommonMethod.toCloseLoader();
                                CommonMethod.toDisplayToast(getContext(), " Click again  to Register");
                            }

                            @Override
                            public void onError(FacebookException e) {
                                // here write code when get error
                                Log.d("TAG", "fb onError " + e.getMessage());
                                CommonMethod.toCloseLoader();
                                CommonMethod.toDisplayToast(getContext(), " Click again  to Register");
                            }
                        });
                    }
                }
            });

            accessTokenTracker.startTracking();
            profileTracker.startTracking();
            //Facebook

        } catch (Exception | Error e) {
            CommonMethod.toCloseLoader();
            e.printStackTrace();
            Crashlytics.logException(e);
            CommonMethod.toDisplayToast(getContext(), " Click again  to Register");
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
            CommonMethod.toCallLoader(getContext(), "Login successful from Google");
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
                user.setLoginFrom(1);
                toExit();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    // Called when successfully logged in
    private void toExit() {
        try {
            CommonMethod.toCloseLoader();
            CommonMethod.toDisplayToast(getContext(), "Login Successful");
            CommonMethod.toCallLoader(getContext(), "Logging....");
            new PrefManager(getContext()).setUserInfo();
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
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

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
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
