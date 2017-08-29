package br.alexandrenavarro.scheduling.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexandrenavarro on 14/08/17.
 */

public class Company implements Parcelable {

    private String addOn;
    private String address;
    private String name;
    private String phone;
    private long id;

    public String getAddOn() {
        return addOn;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.addOn);
        dest.writeString(this.address);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeLong(this.id);
    }

    public Company() {
    }

    protected Company(Parcel in) {
        this.addOn = in.readString();
        this.address = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel source) {
            return new Company(source);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };
}
