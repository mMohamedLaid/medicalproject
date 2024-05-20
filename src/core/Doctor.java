package core;

public class Doctor extends Person {
    private int doctorId;
    private String email;
    private String specialty;

    public Doctor(int doctorId, String firstName, String lastName, String phoneNumber, String email, String specialty) {
        super(firstName, lastName, phoneNumber);
        this.doctorId = doctorId;
        this.email = email;
        this.specialty = specialty;
    }

    @Override
    public void displayDetails() {
        System.out.println("Doctor: " + firstName + " " + lastName + " | Phone: " + phoneNumber + " | Email: " + email + " | Specialty: " + specialty);
    }

    public void manageAppointment(Appointment appointment) {
        System.out.println("Managing appointment for: " + appointment.getPatient().getFirstName());
    }

    public void consultPatient(Patient patient) {
        System.out.println("Consulting patient: " + patient.getFirstName() + " " + patient.getLastName());
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getEmail() {
        return email;
    }

    public String getSpecialty() {
        return specialty;
    }
}
