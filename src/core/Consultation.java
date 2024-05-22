package core;

import java.time.LocalDate;

public class Consultation {
    private int consultationId;
    private Patient patient;
    private Doctor doctor;
    private LocalDate date;
    private String diagnosis;
    private String treatment;

    public Consultation(int consultationId, Patient patient, Doctor doctor, LocalDate date, String diagnosis, String treatment) {
        this.consultationId = consultationId;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    public int getConsultationId() {
        return consultationId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }
}
