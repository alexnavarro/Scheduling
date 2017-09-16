package br.alexandrenavarro.scheduling.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.alexandrenavarro.scheduling.activity.CompanyActivity;
import br.alexandrenavarro.scheduling.R;
import br.alexandrenavarro.scheduling.model.Company;

/**
 * Created by alexandrenavarro on 14/08/17.
 */

public class CompanyHolder extends RecyclerView.ViewHolder{

    private final TextView mNameField;
    private final TextView mAddressField;
    private final TextView mPhoneField;
    private Company mCompany;

    public CompanyHolder(View itemView) {
        super(itemView);
        mNameField = itemView.findViewById(R.id.txt_name);
        mAddressField = itemView.findViewById(R.id.txt_address);
        mPhoneField = itemView.findViewById(R.id.txt_txt_phone);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CompanyActivity.class);
            intent.putExtra(CompanyActivity.EXTRA_COMPANY, mCompany);
            view.getContext().startActivity(intent);
        });
    }

    public void bind(Company company) {
        mNameField.setText(company.getName());
        mAddressField.setText(company.getAddress());
        mPhoneField.setText(company.getPhone());
        mCompany = company;
    }
}