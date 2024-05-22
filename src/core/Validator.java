package core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import database.PatientDAO;
import database.DoctorDAO;

public class Validator {

	public static boolean isValidPersonInput(String firstName, String lastName, String phoneNumber, String email) {
        return !(firstName.isEmpty() || lastName.isEmpty() || !phoneNumber.matches("\\d+") || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$"));
    }

    public static boolean isUniquePatientInput(String firstName, String lastName, String phoneNumber, String email) {
        PatientDAO patientDAO = new PatientDAO();
        return !patientDAO.exists(firstName, lastName, phoneNumber, email);
    }

    public static boolean isUniqueDoctorInput(String firstName, String lastName, String phoneNumber, String email) {
        DoctorDAO doctorDAO = new DoctorDAO();
        return !doctorDAO.exists(firstName, lastName, phoneNumber, email);
    }

    public static boolean isValidAppointmentInput(int patientId, int doctorId, LocalDateTime dateTime) {
        // Add any additional validation logic if necessary
        return patientId > 0 && doctorId > 0 && isValidDateTime(dateTime);
    }

    public static boolean isValidDateTime(LocalDateTime dateTime) {
        try {
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(dateTime);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public static boolean isValidConsultationInput(int patientId, int doctorId, LocalDate date) {
        return patientId > 0 && doctorId > 0 && isValidDate(date);
    }

    public static boolean isValidDate(LocalDate date) {
        try {
            DateTimeFormatter.ofPattern("dd-MM-yyyy").format(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public static boolean isValidMedicalRecordInput(int patientId, int consultationId) {
        return patientId > 0 && consultationId > 0;
    }
}


