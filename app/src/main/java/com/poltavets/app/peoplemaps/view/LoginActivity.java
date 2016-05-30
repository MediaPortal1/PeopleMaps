package com.poltavets.app.peoplemaps.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.poltavets.app.peoplemaps.R;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private SignInButton loginbtn;
    private GoogleSignInAccount account;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private final static String SCOPE =
            "oauth2:https://docs.google.com/feeds/ https://docs.googleusercontent.com/ https://spreadsheets.google.com/feeds/";
    private TextView txtView;
    private String name;
    private String id;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_login));
        //

        //INIT GOOGLE+
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_oauth))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //INIT LOGIN BUTTON & TextView
        loginbtn=(SignInButton)findViewById(R.id.sign_in_button);
        loginbtn.setSize(SignInButton.SIZE_STANDARD);
        loginbtn.setScopes(gso.getScopeArray());
        loginbtn.setOnClickListener(this);
        txtView=(TextView)findViewById(R.id.textView_login);
        txtView.setVisibility(View.INVISIBLE);
        txtView.setEnabled(false);
        txtView.setOnClickListener(this);
        //
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Login failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.textView_login:
                Intent intent=new Intent(this,MainActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("id",id);
                intent.putExtra("token",token);
                startActivity(intent);
                break;
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            account= result.getSignInAccount();
            name=account.getDisplayName();
            id=account.getId();
            token=account.getIdToken();
            txtView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
            txtView.setEnabled(true);
            txtView.setVisibility(View.VISIBLE);
            Toast.makeText(this,"Login success",Toast.LENGTH_SHORT).show();
        } else {
            // Signed out, show unauthenticated UI.
        }
    }
}
