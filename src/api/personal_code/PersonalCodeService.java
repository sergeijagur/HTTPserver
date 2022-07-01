package api.personal_code;

public class PersonalCodeService {

    PersonalCode personalCode;


    public PersonalCodeService(PersonalCode personalCode) {
        this.personalCode = personalCode;
    }

    public boolean isValidPersonalCode() {
        return personalCode.isValid();
    }

    public int getAge() {
        return personalCode.getAge();
    }

    public String getBirthDate() {
        return personalCode.getBirthDate();
    }

    public String generatePersonalCode() {
        return personalCode.generatePersonalCode();
    }

}
