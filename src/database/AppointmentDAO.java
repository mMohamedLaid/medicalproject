package database;

import core.Appointment;
import core.Patient;
import core.Doctor;
import gui.PatientService;
import gui.DoctorService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    static {
        DatabaseConnection.executeSQLFile("setup.sql");
    }

    public void insertAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, appointment.getPatient().getPatientId());
            pstmt.setInt(2, appointment.getDoctor().getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getDateTime()));
            pstmt.executeUpdate();
            System.out.println("A new appointment has been inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Appointment> selectAllAppointments() {
        String sql = "SELECT * FROM appointments";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");
                LocalDateTime dateTime = rs.getTimestamp("appointment_date").toLocalDateTime();

                Patient patient = new PatientService().getPatientById(patientId);
                Doctor doctor = new DoctorService().getDoctorById(doctorId);
                Appointment appointment = new Appointment(appointmentId, patient, doctor, dateTime);
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public void updateAppointment(Appointment appointment) {
        String sql = "UPDATE appointments SET patient_id = ?, doctor_id = ?, appointment_date = ? WHERE appointment_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getDateTime()));
            pstmt.setInt(4, appointment.getAppointmentId());
            pstmt.executeUpdate();
            System.out.println("Appointment updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            pstmt.executeUpdate();
            System.out.println("Appointment has been deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


