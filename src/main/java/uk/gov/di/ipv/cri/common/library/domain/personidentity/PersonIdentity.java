package uk.gov.di.ipv.cri.common.library.domain.personidentity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonIdentity {
    private String firstName;
    private String middleNames;
    private String surname;
    private List<PersonAddress> addresses;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    public PersonIdentity() {
        this.addresses = new ArrayList<>(0);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(String middleNames) {
        this.middleNames = middleNames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
    }
}