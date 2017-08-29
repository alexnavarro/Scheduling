package br.alexandrenavarro.scheduling.model;

/**
 * Created by alexandrenavarro on 26/08/17.
 */

public class Professional {

    private String phone;
    private String name;

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
}
