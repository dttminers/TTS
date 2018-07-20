package in.tts.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
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

public class LoginFragment extends Fragment {

    private TextView mTvLogin;
    private EditText mEdtEmail, mEdtPassword;
    private Button mBtnLogin;

    // Google
    private GoogleSignInClient mGoogleSignInClient;
    private RelativeLayout relativeLayoutGoogle;

    //Facebook
    private RelativeLayout relativeLayoutFb;
    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LoginManager mFbLoginManager;

    private FirebaseAuth auth;

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
            //Get Firebase auth instance
            auth = FirebaseAuth.getInstance();

//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            updateUI(currentUser);

            mTvLogin = getActivity().findViewById(R.id.txtLogin);

            SpannableString ss1 = new SpannableString(getString(R.string.str_login_data));
            ss1.setSpan(new RelativeSizeSpan(1.5f), 38, 47, 0); // set size
//            ss1.setSpan(new ForegroundColorSpan(Color.WHITE), 38, 49, 0);// set color
            mTvLogin.setText(ss1);

            // E-mail & Password Validation...........

            mEdtEmail = getActivity().findViewById(R.id.edtEmailIdLogin);
            mEdtPassword = getActivity().findViewById(R.id.edtPasswordLogin);
            mBtnLogin = getActivity().findViewById(R.id.btnLogin);

            mEdtEmail.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (validateEmail()) {
                            mEdtPassword.requestFocus();
                            return true;
                        } else {
                            mEdtEmail.requestFocus();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            });

            mEdtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (validatePassword()) {
                            mBtnLogin.requestFocus();
                            return true;
                        } else {
                            mEdtPassword.requestFocus();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            });

            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Login...");
                    if (validateEmail() && validatePassword()) {
//                        startActivity(new Intent(getContext(), MainActivity.class));
//                        CommonMethod.toDisplayToast(getContext(), "Login Successfully ");
                        //authenticate user
                        auth.signInWithEmailAndPassword(mEdtEmail.getText().toString(), mEdtPassword.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
//                            progressBar.setVisibility(View.GONE);
                                        if (!task.isSuccessful()) {
                                            // there was an error
                                            if (mEdtPassword.getText().toString().length() < 8) {
                                                mEdtPassword.setError(getString(R.string.str_error_minimum_8));
                                            } else {
                                                Toast.makeText(getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                            }
                                        } else {
//
                                            User user = User.getUser(getContext());
                                            user.setLoginFrom(3);
                                            user.setEmail(mEdtEmail.getText().toString());
//                                        user.setPicPath(currentProfile.getProfilePictureUri(1000, 1000).toString());
                                            Log.d("TAG", "Userinfo email  " + new Gson().toJson(User.getUser(getContext())));
                                            CommonMethod.toCloseLoader();
                                            toExit();
                                        }
                                    }
                                });
                    } else {
                        CommonMethod.toDisplayToast(getContext(), "Please try again, Login failed");
                    }
                }
            });

            mEdtEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validateEmail();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validateEmail();
                }
            });

            mEdtPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validatePassword();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validatePassword();
                }
            });

            // View
            getActivity().findViewById(R.id.llSignUp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Loading....");
                        getContext().startActivity(new Intent(getContext(), LoginActivity.class).putExtra("LOGIN", "register"));
                        getActivity().finish();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });
            getActivity().findViewById(R.id.txtSkipLogin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Loading....");
                        getContext().startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
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
                        CommonMethod.toCallLoader(getContext(), "Login with Facebook ");
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
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
                        CommonMethod.toDisplayToast(getContext(), " Click again  to login");
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
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                    CommonMethod.toDisplayToast(getContext(), " Click again  to login");
                                }
                            }

                            @Override
                            public void onCancel() {
                                Log.d("TAG", " fb is cancel");
                                CommonMethod.toCloseLoader();
                                CommonMethod.toDisplayToast(getContext(), " Click again  to login");
                            }

                            @Override
                            public void onError(FacebookException e) {
                                // here write code when get error
                                Log.d("TAG", "fb onError " + e.getMessage());
                                CommonMethod.toCloseLoader();
                                CommonMethod.toDisplayToast(getContext(), " Click again  to login");
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toDisplayToast(getContext(), " Click again  to login");
        }
    }

    public boolean validateEmail() {
        if (mEdtEmail.getText().toString().trim().length() == 0) {
            mEdtEmail.setError(getContext().getResources().getString(R.string.str_field_cant_be_empty));
            return false;
        } else if (CommonMethod.isValidEmail(mEdtEmail.getText().toString().trim())) {
            mEdtEmail.setError(null);
            return true;
        } else {
            mEdtEmail.setError(getString(R.string.str_error_valid_email));
            return false;
        }
    }

    public boolean validatePassword() {
        if (mEdtPassword.getText().toString().trim().length() == 0) {
            mEdtPassword.setError(getContext().getResources().getString(R.string.str_field_cant_be_empty));
            return false;
        } else if (mEdtPassword.getText().toString().trim().length() < 8) {
            mEdtPassword.setError(getString(R.string.str_error_minimum_8));
            return false;
        } else if (mEdtPassword.getText().toString().trim().length() > 15) {
            mEdtPassword.setError(getString(R.string.str_error_maximum_15));
            return false;
        } else if (CommonMethod.isValidPassword(mEdtPassword.getText().toString().trim())) {
            mEdtPassword.setError(getString(R.string.str_error_pswd));
            return false;
        } else {
            mEdtPassword.setError(null);
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("TAG", " fb  result " + resultCode + ":" + requestCode + " :");
            // Google
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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
        CommonMethod.toCloseLoader();
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
        try {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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