package br.alexandrenavarro.scheduling;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import br.alexandrenavarro.scheduling.database.CompanyHolder;
import br.alexandrenavarro.scheduling.model.Company;

/**
 * Created by alexandrenavarro on 09/08/17.
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<Company, CompanyHolder> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
