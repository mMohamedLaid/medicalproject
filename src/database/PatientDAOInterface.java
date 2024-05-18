package database;

import core.Patient;
import java.util.List;
import java.sql.SQLException;

public interface PatientDAOInterface {
    void insertPatient(String firstName, String lastName, String phoneNumber, String email);
    List<Patient> selectAllPatients();
    Patient selectPatientById(int patientId);
    void updatePatient(int patientId, String firstName, String lastName, String phoneNumber, String email) throws SQLException;
    void deletePatient(int patientId);
}
