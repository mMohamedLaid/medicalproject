package database;

import core.Consultation;
import core.Patient;
import core.Doctor;
import gui.PatientService;
import gui.DoctorService;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {
    static {
        DatabaseConnection.executeSQLFile("setup.sql");
    }

    public void insertConsultation(Consultation consultation) {
        String sql = "INSERT INTO consultations (patient_id, doctor_id, consultation_date, diagnosis, treatment) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, consultation.getPatient().getPatientId());
            pstmt.setInt(2, consultation.getDoctor().getDoctorId());
            pstmt.setDate(3, Date.valueOf(consultation.getDate()));
            pstmt.setString(4, consultation.getDiagnosis());
            pstmt.setString(5, consultation.getTreatment());
            pstmt.executeUpdate();
            System.out.println("A new consultation has been inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Consultation> selectAllConsultations() {
        String sql = "SELECT * FROM consultations";
        List<Consultation> consultations = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int consultationId = rs.getInt("consultation_id");
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");
                LocalDate date = rs.getDate("consultation_date").toLocalDate();
                String diagnosis = rs.getString("diagnosis");
                String treatment = rs.getString("treatment");
                Patient patient = new PatientService().getPatientById(patientId);
                Doctor doctor = new DoctorService().getDoctorById(doctorId);
                consultations.add(new Consultation(consultationId, patient, doctor, date, diagnosis, treatment));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultations;
    }

    public Consultation selectConsultationById(int consultationId) {
        String sql = "SELECT * FROM consultations WHERE consultation_id = ?";
        Consultation consultation = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, consultationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");
                LocalDate date = rs.getDate("consultation_date").toLocalDate();
                String diagnosis = rs.getString("diagnosis");
                String treatment = rs.getString("treatment");
                Patient patient = new PatientService().getPatientById(patientId);
                Doctor doctor = new DoctorService().getDoctorById(doctorId);
                consultation = new Consultation(consultationId, patient, doctor, date, diagnosis, treatment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultation;
    }

    public void updateConsultation(Consultation consultation) {
        String sql = "UPDATE consultations SET patient_id = ?, doctor_id = ?, consultation_date = ?, diagnosis = ?, treatment = ? WHERE consultation_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, consultation.getPatient().getPatientId());
            pstmt.setInt(2, consultation.getDoctor().getDoctorId());
            pstmt.setDate(3, Date.valueOf(consultation.getDate()));
            pstmt.setString(4, consultation.getDiagnosis());
            pstmt.setString(5, consultation.getTreatment());
            pstmt.setInt(6, consultation.getConsultationId());
            pstmt.executeUpdate();
            System.out.println("Consultation information has been updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteConsultation(int consultationId) {
        String sql = "DELETE FROM consultations WHERE consultation_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, consultationId);
            pstmt.executeUpdate();
            System.out.println("Consultation has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

