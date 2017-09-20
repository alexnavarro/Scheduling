package br.alexandrenavarro.scheduling.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.holder.ProfessionalHolder;
import br.alexandrenavarro.scheduling.model.Company;
import br.alexandrenavarro.scheduling.model.Professional;
import br.alexandrenavarro.scheduling.util.DateUtil;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by alexandrenavarro on 24/08/17.
 */

public class CompanyActivity extends LifecycleActivity implements LifecycleRegistryOwner, AppCompatCallback {

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    public static final String EXTRA_COMPANY = "COMPANY";

    @BindView(R.id.txt_phone) TextView mPhone;
    @BindView(R.id.txt_address) TextView mAddress;
    @BindView(R.id.recycler_view_professionals) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    protected DatabaseReference mProfessionalRef;
    protected DatabaseReference mSchedulingRef;
    private LinearLayoutManager mManager;
    private DividerItemDecoration mDividerItemDecoration;
    private FirebaseRecyclerAdapter<Professional, ProfessionalHolder> mAdapter;
    private AppCompatDelegate delegate;

    private Company mCompany;
    private Map<Long, Set<Integer>> map = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        delegate = AppCompatDelegate.create(this, this);
        delegate.installViewFactory();
        super.onCreate(savedInstanceState);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.company_detail_activity);
        ButterKnife.bind(this);
        delegate.setSupportActionBar(mToolbar);

        mCompany = getIntent().getParcelableExtra(EXTRA_COMPANY);
        mProfessionalRef = FirebaseDatabase.getInstance().getReference().
                child("professional").child(String.valueOf(mCompany.getId()));
        mSchedulingRef = FirebaseDatabase.getInstance().getReference().
                child("professionalSchedule");

        bind();

        mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mDividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                mManager.getOrientation()
        );
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        attachRecyclerViewAdapter();

        ViewModelProviders.of(this).get(CompanyActivityModel.class).
                loadScheduling(mCompany.getId()).observe(this, new Observer<Map<Long, Set<Integer>>>() {
            @Override
            public void onChanged(@Nullable Map<Long, Set<Integer>> longSetMap) {
                map.clear();
                map.putAll(longSetMap);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }

    private void bind() {
        ActionBar supportActionBar = delegate.getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }

        if(mCompany != null){
            mPhone.setText(mCompany.getPhone());
            mAddress.setText(mCompany.getAddress());
            setTitle(mCompany.getName());
        }
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    private void attachRecyclerViewAdapter() {
        mAdapter = getAdapter();

        // Scroll to bottom on new messages
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mRecyclerView, null, mAdapter.getItemCount());
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    protected FirebaseRecyclerAdapter<Professional, ProfessionalHolder> getAdapter() {
        final String dayOfWeek = new SimpleDateFormat("EEEE").format(DateUtil.getNextBusinessDay().getTime());

        return new FirebaseRecyclerAdapter<Professional, ProfessionalHolder>(Professional.class,
                R.layout.company_details_item, ProfessionalHolder.class, mProfessionalRef,this) {
            @Override
            public void populateViewHolder(ProfessionalHolder holder, Professional professional, int position) {
                holder.bind(professional, map.get(professional.getId()), dayOfWeek);
            }

            @Override
            public void onDataChanged() {}
        };
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}