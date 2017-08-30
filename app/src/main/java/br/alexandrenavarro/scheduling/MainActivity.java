package br.alexandrenavarro.scheduling;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.alexandrenavarro.scheduling.database.CompanyHolder;
import br.alexandrenavarro.scheduling.model.Company;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexandrenavarro on 09/08/17.
 */

public class MainActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    // TODO remove once arch components are merged into support lib
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    private FirebaseRecyclerAdapter<Company, CompanyHolder> mAdapter;
    @BindView(R.id.recycler_view) RecyclerView mCompanies;
    protected DatabaseReference mCompaniesRef;
    private LinearLayoutManager mManager;
    private DividerItemDecoration mDividerItemDecoration;

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
}