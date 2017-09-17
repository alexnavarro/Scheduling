package br.alexandrenavarro.scheduling.model;

/**
 * Created by alexandrenavarro on 26/08/17.
 */

public class Scheduling {

    private String date;
    private long idCompany;
    private String uid;
    private String idCompany_day;
    private String name;
    private long idProfessional;
    private String idUserProfessionalDay;
    private String idProfessionalDay;
    private String userPhone;
    private String userEmail;
    private String professionalName;
    private String specialization;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
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

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getIdUserProfessionalDay() {
        return idUserProfessionalDay;
    }

    public void setIdUserProfessionalDay(String idUserProfessionalDay) {
        this.idUserProfessionalDay = idUserProfessionalDay;
    }

    public String getIdProfessionalDay() {
        return idProfessionalDay;
    }

    public void setIdProfessionalDay(String idProfessionalDay) {
        this.idProfessionalDay = idProfessionalDay;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getIdUserDay() {
        return idUserDay;
    }

    public void setIdUserDay(String idUserDay) {
        this.idUserDay = idUserDay;
    }
}