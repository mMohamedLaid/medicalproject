package gui;

import core.Patient;
import database.PatientDAO;
import java.util.List;

public class PatientService {
    private PatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = new PatientDAO();
    }

    public void addPatient(String firstName, String lastName, String phoneNumber, String email) {
        patientDAO.insertPatient(firstName, lastName, phoneNumber, email);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.selectAllPatients();
    }

    public Patient getPatientById(int patientId) {
        return patientDAO.selectPatientById(patientId);
    }

    public void updatePatient(int patientId, String firstName, String lastName, String phoneNumber, String email) {
        patientDAO.updatePatient(patientId, firstName, lastName, phoneNumber, email);
    }

    public void deletePatient(int patientId) {
        patientDAO.deletePatient(patientId);
    }
}
