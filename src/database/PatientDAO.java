package database;

import core.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    static {
        DatabaseConnection.executeSQLFile("setup.sql");
    }

    public void insertPatient(String firstName, String lastName, String phoneNumber, String email) {
        String personSql = "INSERT INTO person (first_name, last_name, phone_number, email) VALUES (?, ?, ?, ?)";
        String patientSql = "INSERT INTO patients (person_id) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement personPstmt = connection.prepareStatement(personSql, Statement.RETURN_GENERATED_KEYS)) {
            personPstmt.setString(1, firstName);
            personPstmt.setString(2, lastName);
            personPstmt.setString(3, phoneNumber);
            personPstmt.setString(4, email);
            personPstmt.executeUpdate();

            ResultSet rs = personPstmt.getGeneratedKeys();
            if (rs.next()) {
                int personId = rs.getInt(1);
                try (PreparedStatement patientPstmt = connection.prepareStatement(patientSql)) {
                    patientPstmt.setInt(1, personId);
                    patientPstmt.executeUpdate();
                }
            }
            System.out.println("A new patient has been inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Patient> selectAllPatients() {
        String sql = "SELECT p.patient_id, pe.first_name, pe.last_name, pe.phone_number, pe.email " +
                     "FROM patients p JOIN person pe ON p.person_id = pe.person_id";
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int patientId = rs.getInt("patient_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                patients.add(new Patient(patientId, firstName, lastName, phoneNumber, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public Patient selectPatientById(int patientId) {
        String sql = "SELECT p.patient_id, pe.first_name, pe.last_name, pe.phone_number, pe.email " +
                     "FROM patients p JOIN person pe ON p.person_id = pe.person_id WHERE p.patient_id = ?";
        Patient patient = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                patient = new Patient(patientId, firstName, lastName, phoneNumber, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patient;
    }

    public void updatePatient(int patientId, String firstName, String lastName, String phoneNumber, String email) {
        String personSql = "UPDATE person SET first_name = ?, last_name = ?, phone_number = ?, email = ? " +
                           "WHERE person_id = (SELECT person_id FROM patients WHERE patient_id = ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement personPstmt = connection.prepareStatement(personSql)) {
            personPstmt.setString(1, firstName);
            personPstmt.setString(2, lastName);
            personPstmt.setString(3, phoneNumber);
            personPstmt.setString(4, email);
            personPstmt.setInt(5, patientId);
            personPstmt.executeUpdate();
            System.out.println("Patient information has been updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
    public boolean exists(String firstName, String lastName, String phoneNumber, String email) {
        String sql = "SELECT COUNT(*) FROM patients WHERE first_name = ? AND last_name = ? AND phone_number = ? AND email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
