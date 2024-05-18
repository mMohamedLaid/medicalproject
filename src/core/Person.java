package core;

public abstract class Person {
	protected String firstName;
	protected String lastName;
	protected String phoneNumber;

	public Person(String firstName, String lastName, String phoneNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
	}

	public abstract void displayDetails();

	// Add getters if needed for both Patient and Doctor
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
