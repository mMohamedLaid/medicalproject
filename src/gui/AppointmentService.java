package gui;

import core.Appointment;
import database.AppointmentDAO;

import java.util.List;

public class AppointmentService {
    private AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public void addAppointment(Appointment appointment) {
        appointmentDAO.insertAppointment(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.selectAllAppointments();
    }

    public void updateAppointment( Appointment appointment) {
        appointmentDAO.updateAppointment( appointment);
    }

    public void deleteAppointment(int appointmentId) {
        appointmentDAO.deleteAppointment(appointmentId);
    }
}
