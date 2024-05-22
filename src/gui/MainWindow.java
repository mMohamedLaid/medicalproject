package gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTabbedPane tabbedPane;
    private PatientsPanel patientsPanel;
    private DoctorsPanel doctorsPanel;
    private AppointmentsPanel appointmentsPanel;
    private ConsultationsPanel consultationsPanel;
    private MedicalRecordsPanel medicalRecordsPanel;

    public MainWindow() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Title Screen with Background Image
        JPanel titleScreen = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("images/BLUE_HEAL.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Panel for title text and button
        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false); // Make the panel transparent

        JLabel titleLabel = new JLabel("Welcome to the Medical Management System", JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleTextPanel.add(titleLabel);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(new Color(0, 123, 255));
        startButton.setForeground(Color.WHITE);
        startButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleTextPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between title and button
        titleTextPanel.add(startButton);

        // Center the titleTextPanel vertically and align to left edge
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Align to the left edge (X-axis)
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align components to the top left corner
        centerPanel.add(titleTextPanel, gbc);

        titleScreen.add(centerPanel, BorderLayout.CENTER);

        startButton.addActionListener(e -> cardLayout.show(cardPanel, "Main"));

        cardPanel.add(titleScreen, "Title");

        // Main Tabbed Interface
        tabbedPane = new JTabbedPane();
        patientsPanel = new PatientsPanel();
        doctorsPanel = new DoctorsPanel();
        appointmentsPanel = new AppointmentsPanel(tabbedPane, patientsPanel, doctorsPanel);
        consultationsPanel = new ConsultationsPanel(tabbedPane, patientsPanel, doctorsPanel);
        medicalRecordsPanel = new MedicalRecordsPanel(tabbedPane, patientsPanel, consultationsPanel);

        tabbedPane.addTab("Patients", patientsPanel);
        tabbedPane.addTab("Doctors", doctorsPanel);
        tabbedPane.addTab("Appointments", appointmentsPanel);
        tabbedPane.addTab("Consultations", consultationsPanel);
        tabbedPane.addTab("Medical Records", medicalRecordsPanel);

        cardPanel.add(tabbedPane, "Main");

        add(cardPanel);

        setTitle("Medical Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}



