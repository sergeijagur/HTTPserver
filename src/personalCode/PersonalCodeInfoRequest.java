package personalCode;

import java.time.LocalDate;

public class PersonalCodeInfoRequest {

    private LocalDate birthOfDate;
    private Gender gender;
    private HospitalOfBirth hospital;

    public PersonalCodeInfoRequest(LocalDate birthOfDate, Gender gender, HospitalOfBirth hospital) {
        this.birthOfDate = birthOfDate;
        this.gender = gender;
        this.hospital = hospital;
    }

    public PersonalCodeInfoRequest(LocalDate birthOfDate, Gender gender) {
        this.birthOfDate = birthOfDate;
        this.gender = gender;
    }

    public LocalDate getBirthOfDate() {
        return birthOfDate;
    }

    public Gender getGender() {
        return gender;
    }

    public HospitalOfBirth getHospital() {
        return hospital;
    }
}
