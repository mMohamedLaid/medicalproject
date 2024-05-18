package core;

import java.util.List;
import java.util.ArrayList;

public class MedicalRecord {
	private Patient patient;
	private List<Consultation> consultations;
	private List<String> prescriptions;
	private List<String> medicalCertificates;

	public MedicalRecord(Patient patient) {
		this.patient = patient;
		this.consultations = new ArrayList<>();
		this.prescriptions = new ArrayList<>();
		this.medicalCertificates = new ArrayList<>();
	}

	public void addConsultation(Consultation consultation) {
		consultations.add(consultation);
	}

	public void addPrescription(String prescription) {
		prescriptions.add(prescription);
	}

	public void addMedicalCertificate(String certificate) {
		medicalCertificates.add(certificate);
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