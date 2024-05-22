package core;

public class MedicalRecord {
    private int recordId;
    private Patient patient;
    private Consultation consultation;
    private String prescriptions;
    private String medicalCertificate;

    public MedicalRecord(int recordId, Patient patient, Consultation consultation, String prescriptions, String medicalCertificate) {
        this.recordId = recordId;
        this.patient = patient;
        this.consultation = consultation;
        this.prescriptions = prescriptions;
        this.medicalCertificate = medicalCertificate;
    }

    public int getRecordId() {
        return recordId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public String getPrescriptions() {
        return prescriptions;
    }

    public String getMedicalCertificate() {
        return medicalCertificate;
    }

    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
    }

    public void setMedicalCertificate(String medicalCertificate) {
        this.medicalCertificate = medicalCertificate;
    }
}

