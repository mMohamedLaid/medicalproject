package database;

import core.Doctor;
import java.util.List;

public interface DoctorDAOInterface {
    void insertDoctor(String firstName, String lastName, String phoneNumber, String specialty);
    List<Doctor> selectAllDoctors();
    Doctor selectDoctorById(int doctorId);
    void updateDoctor(int doctorId, String firstName, String lastName, String phoneNumber, String specialty);
    void deleteDoctor(int doctorId);
}

