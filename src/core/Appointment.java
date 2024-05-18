package core;

import java.time.LocalDateTime;

public class Appointment {
	private Patient patient;
	private Doctor doctor;
	private LocalDateTime dateTime;

	public Appointment(Patient patient, Doctor doctor, LocalDateTime dateTime) {
		this.patient = patient;
		this.doctor = doctor;
		this.dateTime = dateTime;
	}

	public void rescheduleAppointment(LocalDateTime newDateTime) {
		this.dateTime = newDateTime;
		System.out.println("Appointment rescheduled to: " + newDateTime.toString());
	}

	public Patient getPatient() {
		return patient;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}
}
