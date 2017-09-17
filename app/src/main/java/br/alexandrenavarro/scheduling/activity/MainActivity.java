package br.alexandrenavarro.scheduling.activity;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.holder.CompanyHolder;
import br.alexandrenavarro.scheduling.model.Company;
import br.alexandrenavarro.scheduling.widget.ScheduleWidgetProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexandrenavarro on 09/08/17.
 */

public class MainActivity extends BaseActivity implements LifecycleRegistryOwner {

    private static final String TAG = "MainActivity";

    public static final int RC_SIGN_IN = 9001;

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    private FirebaseRecyclerAdapter<Company, CompanyHolder> mAdapter;

    @BindView(R.id.recycler_view) RecyclerView mCompanies;
    @BindView(R.id.txt_greeting) TextView mTxtGreeting;
    @BindView(R.id.sign_in_button) SignInButton mBtnGoogleSignIn;
    @BindView(R.id.logout_button) Button mBtnLogout;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;

    private DatabaseReference mDatabase;
    private LinearLayoutManager mManager;
    private DividerItemDecoration mDividerItemDecoration;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mManager = new LinearLayoutManager(this);
        mCompanies.setLayoutManager(mManager);
        mDividerItemDecoration = new DividerItemDecoration(
                mCompanies.getContext(),
                mManager.getOrientation()
        );
        mCompanies.addItemDecoration(mDividerItemDecoration);
        attachRecyclerViewAdapter();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    protected FirebaseRecyclerAdapter<Company, CompanyHolder> getAdapter() {
        DatabaseReference mCompaniesRef = mDatabase.child("companies");
        Query lastFifty = mCompaniesRef.limitToLast(50);

        return new FirebaseRecyclerAdapter<Company, CompanyHolder>(Company.class,
                R.layout.company_list_item, CompanyHolder.class, lastFifty, this) {
            @Override
            public void populateViewHolder(CompanyHolder holder, Company chat, int position) {
                holder.bind(chat);
            }

            @Override
            public void onDataChanged() {
                mProgressBar.setVisibility(View.INVISIBLE);
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
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            mAuth.getCurrentUser().reload();
            updateUI(mAuth.getCurrentUser());
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @OnClick(R.id.logout_button)
    public void onLogOutClicked(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && !currentUser.isAnonymous()){
            mAuth.signOut();
            updateUI(mAuth.getCurrentUser());
            Intent dataUpdatedIntent = new Intent(ScheduleWidgetProvider.ACTION_DATA_UPDATED);
            getApplication().sendBroadcast(dataUpdatedIntent);
        }
    }


    private void updateUI(FirebaseUser user) {
        if(user != null && !user.isAnonymous()) {
            mTxtGreeting.setText(getString(R.string.user_logged, user.getDisplayName()));
            mBtnGoogleSignIn.setVisibility(View.GONE);
            mBtnLogout.setVisibility(View.VISIBLE);
        }else{
            mTxtGreeting.setText(R.string.user_greeting);
            mBtnGoogleSignIn.setVisibility(View.VISIBLE);
            mBtnLogout.setVisibility(View.GONE);
        }
    }
}