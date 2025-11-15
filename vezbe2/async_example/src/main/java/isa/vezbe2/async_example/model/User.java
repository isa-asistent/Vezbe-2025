package isa.vezbe2.async_example.model;

public class User {
    private String firstName;

    private String lastName;

    private String emailAddress;

    public User() {

    }

    public User(String firstname, String lastname, String emailAddress) {
        super();
        this.firstName = firstname;
        this.lastName = lastname;
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return "User [firstname=" + firstName + ", lastname=" + lastName + ", emailAddress=" + emailAddress + "]";
    }
}
