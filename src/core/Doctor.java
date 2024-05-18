package core;


public class Doctor extends Person {
	private String specialty;
	private boolean isAvailable;

	public Doctor(String firstName, String lastName, String phoneNumber, String specialty, boolean isAvailable) {
		super(firstName, lastName, phoneNumber);
		this.specialty = specialty;
		this.isAvailable = isAvailable;
	}

	@Override
	public void displayDetails() {
		System.out.println("Doctor: " + firstName + " " + lastName + " | Specialty: " + specialty + " | Available: "
				+ isAvailable);
	}

	public void manageAppointment(Appointment appointment) {
		System.out.println("Managing appointment for: " + appointment.getPatient().firstName);
	}

	public void consultPatient(Patient patient) {
		System.out.println("Consulting patient: " + patient.firstName + " " + patient.lastName);
	}

	public String getSpecialty() {
		return specialty;
	}

	public boolean isAvailable() {
		return isAvailable;
	}
}
