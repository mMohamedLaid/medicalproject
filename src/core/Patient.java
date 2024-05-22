package core;

import java.util.List;
import java.util.ArrayList;

public class Patient extends Person {
    private int patientId;
    private String email;
    private List<Consultation> consultations;
    private List<String> prescriptions;
    private List<String> medicalCertificates;

    public Patient(int patientId, String firstName, String lastName, String phoneNumber, String email) {
        super(firstName, lastName, phoneNumber);
        this.patientId = patientId;
        this.email = email;
        this.consultations = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.medicalCertificates = new ArrayList<>();
    }

    @Override
    public void displayDetails() {
        System.out.println("Patient: " + firstName + " " + lastName + " | Phone: " + phoneNumber + " | Email: " + email);
    }

    public void addConsultation(Consultation consultation) {
        consultations.add(consultation);
    }

    public String generatePrescription(String medication, String dose) {
        String prescription = "Medication: " + medication + ", Dose: " + dose;
        prescriptions.add(prescription);
        return prescription;
    }

    public String issueMedicalCertificate(String details) {
        String certificate = "Medical Certificate for " + firstName + " " + lastName + "\nDetails: " + details;
        medicalCertificates.add(certificate);
        return certificate;
    }

    // Getter methods to access patient details
    public int getPatientId() {
        return patientId;
    }

    public String getEmail() {
        return email;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public List<String> getPrescriptions() {
        return prescriptions;
    }

    public List<String> getMedicalCertificates() {
        return medicalCertificates;
    }
}

