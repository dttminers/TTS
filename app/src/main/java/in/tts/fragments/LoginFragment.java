package in.tts.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import in.tts.R;
import in.tts.activities.HomeActivity;
import in.tts.activities.LoginActivity;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.network.VolleySingleton;
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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private User user;

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
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    @AddTrace(name = "onCreateLoginFragment", enabled = true)
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "Login", null);

            user = User.getUser(getContext());
            FacebookSdk.sdkInitialize(getContext());
            mTvLogin = getActivity().findViewById(R.id.txtLogin);

            SpannableString ss1 = new SpannableString(getString(R.string.str_login_data));
            ss1.setSpan(new RelativeSizeSpan(1.5f), 38, 47, 0); // set size
            mTvLogin.setText(ss1);

            mEdtEmail = getActivity().findViewById(R.id.edtEmailIdLogin);
            mEdtPassword = getActivity().findViewById(R.id.edtPasswordLogin);
            mBtnLogin = getActivity().findViewById(R.id.btnLogin);


            //Get Firebase auth instance
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser account = firebaseAuth.getCurrentUser();
//                    signInButton.setVisibility(View.GONE);
//                    signOutButton.setVisibility(View.VISIBLE);
                    if (account != null) {
                        // User is signed in
                        Log.d("TAG", "onAuthStateChanged:signed_in:" + account.getUid());
                        user.setEmail(account.getEmail());
                        user.setId(account.getUid());
                        user.setName(account.getDisplayName());
                        user.setPicPath(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null);
                        user.setLoginFrom(1);
                        toExit();
                    } else {
                        // User is signed out
                        Log.d("TAG", "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };


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
                    try {
                        if (validateEmail() && validatePassword()) {
                            checkInternetConnection();
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    mAuth.signInWithEmailAndPassword(mEdtEmail.getText().toString(), mEdtPassword.getText().toString())
                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("TAG", " Login Successful");
                                                    } else {
                                                        Log.d("TAG", " Login Failed ");
                                                    }
                                                }
                                            })
                                            .addOnCanceledListener(getActivity(), new OnCanceledListener() {
                                                @Override
                                                public void onCanceled() {
                                                    Log.d("TAG", " Login Cancel ");
                                                }
                                            });
                                }
                            };

                        }

                    } catch (Exception | Error e) {
                        CommonMethod.toCloseLoader();
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                        CommonMethod.toDisplayToast(getContext(), " Click again  to login");
                    }

//                    if (validateEmail() && validatePassword()) {
//                        user = User.getUser(getContext());
//                        user.setLoginFrom(3);
//                        user.setId(String.valueOf(System.currentTimeMillis()));
//                        user.setEmail(mEdtEmail.getText().toString());
//                        toExit();
//
                    //authenticate user
//                        mAuth.signInWithEmailAndPassword(mEdtEmail.getText().toString(), mEdtPassword.getText().toString())
//                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (!task.isSuccessful()) {
//                                            // there was an error
//                                            if (mEdtPassword.getText().toString().length() < 8) {
//                                                mEdtPassword.setError(getString(R.string.str_error_minimum_8));
//                                            } else {
//                                                Toast.makeText(getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//                                            }
//                                        } else {
////
//                                            user = User.getUser(getContext());
//                                            user.setLoginFrom(3);
//                                            user.setEmail(mEdtEmail.getText().toString());
//                                            CommonMethod.toCloseLoader();
//                                            toExit();
//                                        }
//                                    }
//                                });
//                    } else {
//                        CommonMethod.toDisplayToast(getContext(), "Please try again, Login failed");
//                    }
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
//                        getContext().startActivity(new Intent(getContext(), MainActivity.class));
                        getContext().startActivity(new Intent(getContext(), HomeActivity.class));
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
                }
            };

            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

                }
            };

            relativeLayoutFb = getActivity().findViewById(R.id.rlFacebookLogin);
            relativeLayoutFb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonMethod.toCallLoader(getContext(), "Login with Facebook ");
                    if (accessTokenTracker.isTracking()) {
                        mFbLoginManager.logOut();
                        accessTokenTracker.stopTracking();
                        profileTracker.stopTracking();
                        CommonMethod.toCloseLoader();
                        CommonMethod.toDisplayToast(getContext(), " Click again  to login");
                    } else {
                        accessTokenTracker.startTracking();
                        mFbLoginManager.logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));//, "user_birthday"));
                        mFbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                try {
                                    AccessToken accessToken = loginResult.getAccessToken();
                                    ProfileTracker profileTracker = new ProfileTracker() {
                                        @Override
                                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                            if (currentProfile != null) {
                                                this.stopTracking();
                                                Profile.setCurrentProfile(currentProfile);
                                                user = User.getUser(getContext());
                                                user.setId(currentProfile.getId());
                                                user.setName1(currentProfile.getFirstName());
                                                user.setName2(currentProfile.getLastName() + currentProfile.getMiddleName());
                                                user.setName(currentProfile.getName());
                                                user.setLoginFrom(2);
                                                user.setPicPath(currentProfile.getProfilePictureUri(1000, 1000).toString());
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
                                CommonMethod.toCloseLoader();
                                CommonMethod.toDisplayToast(getContext(), " Click again  to login");
                            }

                            @Override
                            public void onError(FacebookException e) {
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

    private void checkInternetConnection() {
        try {
            if (getContext() != null) {
                if (CommonMethod.isOnline(getContext())) {
                    new toLogin().execute();
                } else {
                    CommonMethod.toDisplayToast(getContext(), getResources().getString(R.string.lbl_no_check_internet));
                }
            } else {
                CommonMethod.toDisplayToast(getContext(), getResources().getString(R.string.lbl_no_check_internet));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
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
                firebaseAuthWithGoogle(account);
                user = User.getUser(getContext());
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
//        } catch (ApiException e) {
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            CommonMethod.toCloseLoader();
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
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
//            startActivity(new Intent(getContext(), MainActivity.class));
            startActivity(new Intent(getContext(), HomeActivity.class));
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    private class toLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                VolleySingleton.getInstance(getContext())
                        .addToRequestQueue(
                                new StringRequest(Request.Method.POST,
                                        "http://vnoi.in/ttsApi/register_login.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    Log.d("TAG", "login Response " + response);
                                                    if (response != null) {
                                                        JSONObject obj = new JSONObject(response.trim());
                                                        if (obj != null) {
                                                            if (!obj.isNull("status")) {
                                                                if (obj.getString("status").trim().equals("1")) {
                                                                    Log.d("TAG", " login success  ");
                                                                    user = User.getUser(getContext());
                                                                    user.setLoginFrom(3);
                                                                    if (!obj.isNull("id")) {
                                                                        user.setId(obj.getString("id"));
                                                                    }
//                                                                    user.setId(String.valueOf(System.currentTimeMillis()));
                                                                    user.setEmail(mEdtEmail.getText().toString());
                                                                    toExit();
                                                                } else {
                                                                    Log.d("TAG", " login failed ");
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
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("TAG", " login error " + error.getMessage());
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("action", "login");
                                        params.put("email", mEdtEmail.getText().toString());
                                        params.put("password", mEdtPassword.getText().toString());
                                        return params;
                                    }
                                }
                                , "LOGIN");
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CommonMethod.toCloseLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonMethod.toCallLoader(getContext(), "Authenticating user.....");
        }
    }
}