package br.alexandrenavarro.scheduling.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexandrenavarro on 26/08/17.
 */

public class Professional implements Parcelable {

    private String phone;
    private String name;
    private String specialization;
    private long idCompany;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Professional)) return false;

        Professional that = (Professional) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }

    private long id;

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public long getIdCompany() {
        return idCompany;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.name);
        dest.writeString(this.specialization);
        dest.writeLong(this.idCompany);
        dest.writeLong(this.id);
    }

    public Professional() {
    }

    protected Professional(Parcel in) {
        this.phone = in.readString();
        this.name = in.readString();
        this.specialization = in.readString();
        this.idCompany = in.readLong();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<Professional> CREATOR = new Parcelable.Creator<Professional>() {
        @Override
        public Professional createFromParcel(Parcel source) {
            return new Professional(source);
        }

        @Override
        public Professional[] newArray(int size) {
            return new Professional[size];
        }
    };
}