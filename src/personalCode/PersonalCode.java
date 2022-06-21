package personalCode;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class PersonalCode {

    public abstract boolean isValid();

    public abstract String getBirthDate();

    public abstract String generatePersonalCode();

    protected int getAge() {
        LocalDate localDate = LocalDate.now();
        LocalDate birthDate = LocalDate.parse(getBirthDate());

        return (int) birthDate.until(localDate, ChronoUnit.YEARS);
    }

}
