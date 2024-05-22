package core;

import java.time.LocalDateTime;

import gui.DoctorService;
import gui.PatientService;

public class Appointment {
    private int appointmentId;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;

    // Existing constructor
    public Appointment(int appointmentId, Patient patient, Doctor doctor, LocalDateTime dateTime) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
    }

    // New constructor for simpler instantiation
    public Appointment(int appointmentId, int patientId, int doctorId, LocalDateTime dateTime) {
        this.appointmentId = appointmentId;
        this.patient = new PatientService().getPatientById(patientId);
        this.doctor = new DoctorService().getDoctorById(doctorId);
        this.dateTime = dateTime;
    }

    public int getAppointmentId() {
        return appointmentId;
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

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // New getter methods for patientId and doctorId
    public int getPatientId() {
        return patient.getPatientId();
    }

    public int getDoctorId() {
        return doctor.getDoctorId();
    }
}
