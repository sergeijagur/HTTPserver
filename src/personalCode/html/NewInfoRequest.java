package personalCode.html;

import personalCode.Gender;
import personalCode.HospitalOfBirth;

import java.time.LocalDate;

public class NewInfoRequest {

    private String birthOfDate;
    private String gender;

    public NewInfoRequest(String birthOfDate, String gender) {
        this.birthOfDate = birthOfDate;
        this.gender = gender;
    }


    public String getBirthOfDate() {
        return birthOfDate.replace("-", "");
    }

    public String getGender() {
        return gender;
    }

}
