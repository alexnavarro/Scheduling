package br.alexandrenavarro.scheduling.model;

/**
 * Created by alexandrenavarro on 26/08/17.
 */

public class Scheduling {

    private String date;
    private long idCompany;
    private long uid;
    private String idCompany_day;
    private String name;
    private long idProfessional;
    private String idUserDay;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(long idCompany) {
        this.idCompany = idCompany;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getIdCompany_day() {
        return idCompany_day;
    }

    public void setIdCompany_day(String idCompany_day) {
        this.idCompany_day = idCompany_day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getIdProfessional() {
        return idProfessional;
    }

    public void setIdProfessional(long idProfessional) {
        this.idProfessional = idProfessional;
    }

    public void setIdUserDay(String idUserDay) {
        this.idUserDay = idUserDay;
    }

    public String getIdUserDay() {
        return idUserDay;
    }
}