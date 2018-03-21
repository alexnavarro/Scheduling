package br.alexandrenavarro.scheduling.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.activity.SchedulingActivity;
import br.alexandrenavarro.scheduling.model.Professional;
import br.alexandrenavarro.scheduling.model.Scheduling;
import br.alexandrenavarro.scheduling.util.DateUtil;

import static br.alexandrenavarro.scheduling.util.FormatIdUtil.geIdWithFormattedDateWithoutHours;

/**
 * Created by alexandrenavarro on 16/09/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScheduleWidgetRemoteViewsService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return  new RemoteViewsFactory(){

            private List<Scheduling> scheduleList = new ArrayList<>();
            private DatabaseReference mDatabase;
            private FirebaseAuth mAuth;
            private boolean isRealTimeDataBaseInitialized = false;

            @Override
            public void onCreate() {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mAuth = FirebaseAuth.getInstance();
            }

            private void synService() {
                if(isRealTimeDataBaseInitialized){
                    return;
                }

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null && !currentUser.isAnonymous()){
                    isRealTimeDataBaseInitialized = true;
                    DatabaseReference mSchedulingRef = mDatabase.child("professionalSchedule");
                    mSchedulingRef.orderByChild("idUserDay").
                            equalTo(geIdWithFormattedDateWithoutHours(DateUtil.getNextBusinessDay(),
                                    currentUser.getUid())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            scheduleList.clear();
                            for (DataSnapshot schedulingSnapshot : dataSnapshot.getChildren()) {
                                Scheduling scheduling = schedulingSnapshot.getValue(Scheduling.class);
                                scheduleList.add(scheduling);
                            }

                            Intent dataUpdatedIntent = new Intent(ScheduleWidgetProvider.ACTION_DATA_UPDATED);
                            getApplication().sendBroadcast(dataUpdatedIntent);

                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
            }

            @Override
            public void onDataSetChanged() {
                synService();
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return scheduleList.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_schedule_item);

                Scheduling scheduling = scheduleList.get(position);
                Date date = DateUtil.parseDate(scheduling.getDate());

                views.setTextViewText(R.id.txt_name, scheduling.getProfessionalName());
                views.setTextViewText(R.id.txt_date, new SimpleDateFormat("dd/MM/yyyy").format(date));
                views.setTextViewText(R.id.txt_hour, new SimpleDateFormat("HH:mm").format(date));

                final Intent fillInIntent = new Intent();
                Professional professional = new Professional();
                professional.setId(scheduling.getIdProfessional());
                professional.setName(scheduling.getProfessionalName());
                professional.setIdCompany(professional.getIdCompany());
                professional.setSpecialization(professional.getSpecialization());
                fillInIntent.putExtra(SchedulingActivity.EXTRA_PROFESSIONAL, professional);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                 return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
