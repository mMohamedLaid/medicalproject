package core;

import java.time.LocalDate;

public class Consultation {
	private Patient patient;
	private Doctor doctor;
	private LocalDate date;
	private String medicalObservations;
	private String treatment;

	public Consultation(Patient patient, Doctor doctor, LocalDate date, String medicalObservations, String treatment) {
		this.patient = patient;
		this.doctor = doctor;
		this.date = date;
		this.medicalObservations = medicalObservations;
		this.treatment = treatment;
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

	public String getMedicalObservations() {
		return medicalObservations;
	}

	public String getTreatment() {
		return treatment;
	}
}

