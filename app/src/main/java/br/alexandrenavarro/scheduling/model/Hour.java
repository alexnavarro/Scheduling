package br.alexandrenavarro.scheduling.model;

/**
 * Created by alexandrenavarro on 02/09/17.
 */

public class Hour {

    private int hour;
    private boolean available;

    public Hour(int hour, boolean available){
        this.hour = hour;
        this.available = available;
    }

    public int getHour() {
        return hour;
    }

    public String getFormattedHour(){
        return String.format("%d:00", hour);
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hour)) return false;

        Hour hour1 = (Hour) o;

        return getHour() == hour1.getHour();
    }

    @Override
    public int hashCode() {
        return getHour();
    }
}