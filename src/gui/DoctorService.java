package gui;

import core.Doctor;
import database.DoctorDAO;
import java.util.List;

public class DoctorService {
    private DoctorDAO doctorDAO;

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    public void addDoctor(String firstName, String lastName, String phoneNumber,String email, String specialty) {
        doctorDAO.insertDoctor(firstName, lastName, phoneNumber, email , specialty);
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.selectAllDoctors();
    }

    public Doctor getDoctorById(int doctorId) {
        return doctorDAO.selectDoctorById(doctorId);
    }

    public void updateDoctor(int doctorId, String firstName, String lastName, String phoneNumber, String email, String specialty) {
        doctorDAO.updateDoctor(doctorId, firstName, lastName, phoneNumber, email, specialty);
    }

    public void deleteDoctor(int doctorId) {
        doctorDAO.deleteDoctor(doctorId);
    }
}
