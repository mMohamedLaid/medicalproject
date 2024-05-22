package database;

import core.MedicalRecord;
import core.Patient;
import core.Consultation;
import gui.PatientService;
import gui.ConsultationService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {
    static {
        DatabaseConnection.executeSQLFile("setup.sql");
    }

    public void insertMedicalRecord(MedicalRecord medicalRecord) {
        String sql = "INSERT INTO medical_records (patient_id, consultation_id, prescriptions, medical_certificate) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicalRecord.getPatient().getPatientId());
            pstmt.setInt(2, medicalRecord.getConsultation().getConsultationId());
            pstmt.setString(3, medicalRecord.getPrescriptions());
            pstmt.setString(4, medicalRecord.getMedicalCertificate());
            pstmt.executeUpdate();
            System.out.println("A new medical record has been inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MedicalRecord> selectAllMedicalRecords() {
        String sql = "SELECT * FROM medical_records";
        List<MedicalRecord> medicalRecords = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int recordId = rs.getInt("record_id");
                int patientId = rs.getInt("patient_id");
                int consultationId = rs.getInt("consultation_id");
                String prescriptions = rs.getString("prescriptions");
                String medicalCertificate = rs.getString("medical_certificate");

                Patient patient = new PatientService().getPatientById(patientId);
                Consultation consultation = new ConsultationService().getConsultationById(consultationId);

                MedicalRecord medicalRecord = new MedicalRecord(recordId, patient, consultation, prescriptions, medicalCertificate);
                medicalRecords.add(medicalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicalRecords;
    }

    public void updateMedicalRecord(MedicalRecord medicalRecord) {
        String sql = "UPDATE medical_records SET patient_id = ?, consultation_id = ?, prescriptions = ?, medical_certificate = ? WHERE record_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, medicalRecord.getPatient().getPatientId());
            pstmt.setInt(2, medicalRecord.getConsultation().getConsultationId());
            pstmt.setString(3, medicalRecord.getPrescriptions());
            pstmt.setString(4, medicalRecord.getMedicalCertificate());
            pstmt.setInt(5, medicalRecord.getRecordId());
            pstmt.executeUpdate();
            System.out.println("Medical record updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMedicalRecord(int recordId) {
        String sql = "DELETE FROM medical_records WHERE record_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            pstmt.executeUpdate();
            System.out.println("Medical record deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

