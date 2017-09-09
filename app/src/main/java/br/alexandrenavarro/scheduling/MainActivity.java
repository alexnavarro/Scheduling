package br.alexandrenavarro.scheduling;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.alexandrenavarro.scheduling.activity.BaseActivity;
import br.alexandrenavarro.scheduling.holder.CompanyHolder;
import br.alexandrenavarro.scheduling.model.Company;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexandrenavarro on 09/08/17.
 */

public class MainActivity extends BaseActivity implements LifecycleRegistryOwner,  GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    private FirebaseRecyclerAdapter<Company, CompanyHolder> mAdapter
            ;
    @BindView(R.id.recycler_view) RecyclerView mCompanies;
    @BindView(R.id.txt_greeting) TextView mTxtGreeting;
    @BindView(R.id.sign_in_button) SignInButton mBtnGoogleSignIn;
    @BindView(R.id.logout_button) Button mBtnLogout;

    protected DatabaseReference mCompaniesRef;
    private LinearLayoutManager mManager;
    private DividerItemDecoration mDividerItemDecoration;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        mCompaniesRef = FirebaseDatabase.getInstance().getReference().child("companies");
        mManager = new LinearLayoutManager(this);
        mCompanies.setLayoutManager(mManager);
        mDividerItemDecoration = new DividerItemDecoration(
                mCompanies.getContext(),
                mManager.getOrientation()
        );
        mCompanies.addItemDecoration(mDividerItemDecoration);
        attachRecyclerViewAdapter();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    protected FirebaseRecyclerAdapter<Company, CompanyHolder> getAdapter() {
        Query lastFifty = mCompaniesRef.limitToLast(50);
        return new FirebaseRecyclerAdapter<Company, CompanyHolder>(Company.class,
                R.layout.company_list_item, CompanyHolder.class, lastFifty, this) {
            @Override
            public void populateViewHolder(CompanyHolder holder, Company chat, int position) {
                holder.bind(chat);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
//                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void attachRecyclerViewAdapter() {
        mAdapter = getAdapter();

        // Scroll to bottom on new messages
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mCompanies, null, mAdapter.getItemCount());
            }
        });

        mCompanies.setAdapter(mAdapter);
    }

    @OnClick(R.id.sign_in_button)
    public void onGoogleSignInClicked(View view) {
        signIn();
    }

    @OnClick(R.id.logout_button)
    public void onLogOutClicked(View view) {
    }


    private void updateUI(FirebaseUser user) {
        if(user != null && !user.isAnonymous()) {
            mTxtGreeting.setText(getString(R.string.user_logged, user.getDisplayName()));
            mBtnGoogleSignIn.setVisibility(View.GONE);
            mBtnLogout.setVisibility(View.VISIBLE);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    hideProgressDialog();
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, connectionResult.toString());
    }
}