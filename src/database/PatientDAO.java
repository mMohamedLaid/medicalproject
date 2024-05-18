package database;

import core.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO implements PatientDAOInterface {

    static {
        DatabaseConnection.executeSQLFile("setup.sql");
    }

    @Override
    public void insertPatient(String firstName, String lastName, String phoneNumber, String email) {
        String sql = "INSERT INTO patients (first_name, last_name, phone_number, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            pstmt.executeUpdate();
            System.out.println("A new patient has been inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Patient> selectAllPatients() {
        String sql = "SELECT * FROM patients";
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("patient_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                patients.add(new Patient(id, firstName, lastName, phoneNumber, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public Patient selectPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        Patient patient = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("patient_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                patient = new Patient(id, firstName, lastName, phoneNumber, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patient;
    }

    @Override
    public void updatePatient(int patientId, String firstName, String lastName, String phoneNumber, String email) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, phone_number = ?, email = ? WHERE patient_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            pstmt.setInt(5, patientId);
            pstmt.executeUpdate();
            System.out.println("Patient information has been updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            pstmt.executeUpdate();
            System.out.println("Patient has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

