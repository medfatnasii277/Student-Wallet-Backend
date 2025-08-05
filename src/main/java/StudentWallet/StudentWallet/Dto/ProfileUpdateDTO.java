package StudentWallet.StudentWallet.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileUpdateDTO {
    
    @Size(max = 100, message = "School name must be less than 100 characters")
    private String school;
    
    @Size(max = 100, message = "Specialty must be less than 100 characters")
    private String specialty;
    
    @Size(max = 500, message = "Interests must be less than 500 characters")
    private String interests;
    
    @Size(max = 1000, message = "Bio must be less than 1000 characters")
    private String bio;
    
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;
    
    @Pattern(regexp = "^[\\+]?[1-9][\\d]{0,15}$|^$", message = "Please provide a valid phone number")
    private String phoneNumber;
    
    @Size(max = 20, message = "Year of study must be less than 20 characters")
    private String yearOfStudy;
    
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    // Constructors
    public ProfileUpdateDTO() {}

    public ProfileUpdateDTO(String school, String specialty, String interests, String bio, 
                           String email, String phoneNumber, String yearOfStudy, String city) {
        this.school = school;
        this.specialty = specialty;
        this.interests = interests;
        this.bio = bio;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.yearOfStudy = yearOfStudy;
        this.city = city;
    }

    // Getters and Setters
    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
