package database;

import core.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    static {
        DatabaseConnection.executeSQLFile("setup.sql");
    }

    public void insertDoctor(String firstName, String lastName, String phoneNumber, String email, String specialty) {
        String personSql = "INSERT INTO person (first_name, last_name, phone_number, email) VALUES (?, ?, ?, ?)";
        String doctorSql = "INSERT INTO doctors (person_id, specialty) VALUES (?, ?)";

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
                try (PreparedStatement doctorPstmt = connection.prepareStatement(doctorSql)) {
                    doctorPstmt.setInt(1, personId);
                    doctorPstmt.setString(2, specialty);
                    doctorPstmt.executeUpdate();
                }
            }
            System.out.println("A new doctor has been inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Doctor> selectAllDoctors() {
        String sql = "SELECT d.doctor_id, p.first_name, p.last_name, p.phone_number, p.email, d.specialty " +
                     "FROM doctors d JOIN person p ON d.person_id = p.person_id";
        List<Doctor> doctors = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int doctorId = rs.getInt("doctor_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String specialty = rs.getString("specialty");
                doctors.add(new Doctor(doctorId, firstName, lastName, phoneNumber, email, specialty));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public Doctor selectDoctorById(int doctorId) {
        String sql = "SELECT d.doctor_id, p.first_name, p.last_name, p.phone_number, p.email, d.specialty " +
                     "FROM doctors d JOIN person p ON d.person_id = p.person_id WHERE d.doctor_id = ?";
        Doctor doctor = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String specialty = rs.getString("specialty");
                doctor = new Doctor(doctorId, firstName, lastName, phoneNumber, email, specialty);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctor;
    }

    public void updateDoctor(int doctorId, String firstName, String lastName, String phoneNumber, String email, String specialty) {
        String personSql = "UPDATE person SET first_name = ?, last_name = ?, phone_number = ?, email = ? " +
                           "WHERE person_id = (SELECT person_id FROM doctors WHERE doctor_id = ?)";
        String doctorSql = "UPDATE doctors SET specialty = ? WHERE doctor_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement personPstmt = connection.prepareStatement(personSql);
             PreparedStatement doctorPstmt = connection.prepareStatement(doctorSql)) {
            personPstmt.setString(1, firstName);
            personPstmt.setString(2, lastName);
            personPstmt.setString(3, phoneNumber);
            personPstmt.setString(4, email);
            personPstmt.setInt(5, doctorId);
            personPstmt.executeUpdate();

            doctorPstmt.setString(1, specialty);
            doctorPstmt.setInt(2, doctorId);
            doctorPstmt.executeUpdate();
            System.out.println("Doctor information has been updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDoctor(int doctorId) {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            pstmt.executeUpdate();
            System.out.println("Doctor has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
