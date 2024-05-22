package gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private PatientsPanel patientsPanel;
    private DoctorsPanel doctorsPanel;
    private AppointmentsPanel appointmentsPanel;
    private ConsultationsPanel consultationsPanel;

    public MainWindow() {
        tabbedPane = new JTabbedPane();

        patientsPanel = new PatientsPanel();
        doctorsPanel = new DoctorsPanel();
        appointmentsPanel = new AppointmentsPanel(tabbedPane, patientsPanel, doctorsPanel);
        consultationsPanel = new ConsultationsPanel(tabbedPane, patientsPanel, doctorsPanel);

        tabbedPane.addTab("Patients", patientsPanel);
        tabbedPane.addTab("Doctors", doctorsPanel);
        tabbedPane.addTab("Appointments", appointmentsPanel);
        tabbedPane.addTab("Consultations", consultationsPanel);
        tabbedPane.addTab("Medical Records", new MedicalRecordsPanel());
        add(tabbedPane);

        setTitle("Medical Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
