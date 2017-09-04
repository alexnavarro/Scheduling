package br.alexandrenavarro.scheduling;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.alexandrenavarro.scheduling.holder.ProfessionalHolder;
import br.alexandrenavarro.scheduling.model.Company;
import br.alexandrenavarro.scheduling.model.Professional;
import br.alexandrenavarro.scheduling.model.Scheduling;
import br.alexandrenavarro.scheduling.util.DateUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexandrenavarro on 24/08/17.
 */

public class CompanyActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    public static final String EXTRA_COMPANY = "COMPANY";

    @BindView(R.id.txt_phone) TextView mPhone;
    @BindView(R.id.txt_address) TextView mAddress;
    @BindView(R.id.recycler_view_professionals) RecyclerView mRecyclerView;

    protected DatabaseReference mProfessionalRef;
    protected DatabaseReference mSchedulingRef;
    private LinearLayoutManager mManager;
    private DividerItemDecoration mDividerItemDecoration;
    private FirebaseRecyclerAdapter<Professional, ProfessionalHolder> mAdapter;

    private Company mCompany;
    private Map<Long, String> map = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_detail_activity);
        ButterKnife.bind(this);

        if(savedInstanceState == null){
            mCompany = getIntent().getParcelableExtra(EXTRA_COMPANY);
            mProfessionalRef = FirebaseDatabase.getInstance().getReference().
                    child("professional").child(String.valueOf(mCompany.getId()));
            mSchedulingRef = FirebaseDatabase.getInstance().getReference().
                    child("professionalSchedule");
//            loadDataBase();
        }

        bind();

        mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mDividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                mManager.getOrientation()
        );
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        attachRecyclerViewAdapter();
        loadScheduling();


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
        if(mCompany != null){
            mPhone.setText(mCompany.getPhone());
            mAddress.setText(mCompany.getAddress());
            setTitle(mCompany.getName());
        }

        ActionBar supportActionBar = getSupportActionBar();
        if(supportActionBar != null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
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
        String dayOfWeek = new SimpleDateFormat("EEEE").format(DateUtil.getNextBusinessDay().getTime());

        return new FirebaseRecyclerAdapter<Professional, ProfessionalHolder>(Professional.class,
                R.layout.company_details_item, ProfessionalHolder.class, mProfessionalRef,this) {
            @Override
            public void populateViewHolder(ProfessionalHolder holder, Professional professional, int position) {
                holder.bind(professional, map.get(professional.getId()), dayOfWeek);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
//                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    private void loadScheduling(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormatRequest = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = DateUtil.getNextBusinessDay().getTime();

        String dateFormatted = dateFormat.format(date);
        mSchedulingRef.orderByChild("idCompany_day").equalTo(mCompany.getId()+ "_" +dateFormatted).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        map.clear();
                        for (DataSnapshot schedulingSnapshot: dataSnapshot.getChildren()){
                            Scheduling scheduling = schedulingSnapshot.getValue(Scheduling.class);
                            Date date = null;
                            try {
                                date = dateFormatRequest.parse(scheduling.getDate());
                            } catch (ParseException e) {
                                Log.d(CompanyActivity.class.getSimpleName(), e.getMessage());
                            }

                            if(date != null){
                                Calendar calendar = DateUtil.getNextBusinessDay();
                                calendar.setTime(date);
                                String formattedDate = "";

                                if(map.containsKey(scheduling.getIdProfessional())){
                                    formattedDate = map.get(scheduling.getIdProfessional()) + " | ";
                                }

                                formattedDate += calendar.get(Calendar.HOUR_OF_DAY) + ":00";

                                map.put(scheduling.getIdProfessional(), formattedDate);

                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(CompanyActivity.class.getSimpleName(), databaseError.getMessage());
                    }
                });
    }
}