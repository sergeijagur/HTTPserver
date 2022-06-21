package personalCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static personalCode.Gender.FEMALE;
import static personalCode.Gender.MALE;

public class SwedishPersonalCode extends PersonalCode {

    String personalCode;
    PersonalCodeInfoRequest request;

    public SwedishPersonalCode(String personalCode) {
        if (personalCode.length() == 11) {
            int year = Integer.parseInt(personalCode.substring(0, 2));
            if (year <= 22) {
                personalCode = 20 + personalCode;
            } else {
                personalCode = 19 + personalCode;
            }
        }
        this.personalCode = personalCode;
    }

    public SwedishPersonalCode(PersonalCodeInfoRequest request) {
        this.request = request;
    }

    @Override
    public boolean isValid() {

        try {
            LocalDate.parse(getBirthDate());
        } catch (DateTimeParseException a) {
            return false;
        }

        if ('-' != personalCode.charAt(8) || personalCode.length() != 13) return false;

        return checkSum(personalCode) == Integer.parseInt(String.valueOf(personalCode.charAt(personalCode.length() - 1)));
    }

    @Override
    public String getBirthDate() {

        String year = personalCode.substring(0, 4);
        String month = personalCode.substring(4, 6);
        String day = personalCode.substring(6, 8);

        return year + "-" + month + "-" + day;
    }

    @Override
    public String generatePersonalCode() {
        String generatedPersonalCode = "";

        generatedPersonalCode += request.getBirthOfDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        generatedPersonalCode += "-";
        generatedPersonalCode = findCorrectCheckSum(generatedPersonalCode);

        return generatedPersonalCode;
    }

    private String findCorrectCheckSum(String generatedPersonalCode) {
        String personalCodeWithoutCheckSum = generatedPersonalCode + generateRandomSerialNumber();
        int generatedCheckSum = checkSum(personalCodeWithoutCheckSum);
        generatedPersonalCode = personalCodeWithoutCheckSum + generatedCheckSum;
        return generatedPersonalCode;
    }

    private static int checkSum(String personalCode) {
        char[] chars = personalCode.substring(2, 12).toCharArray();
        int sum = getSumOfMultiplications(chars);
        int correctCheckSum = 10 - (sum % 10);

        if (correctCheckSum == 10) {
            return 0;
        }
        return correctCheckSum;
    }

    private static int getSumOfMultiplications(char[] chars) {
        List<Integer> multipliers = new ArrayList<>(List.of(2, 1, 2, 1, 2, 1, 0, 2, 1, 2));

        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '-') {
                continue;
            }
            int multiplication = Integer.parseInt(String.valueOf(chars[i])) * multipliers.get(i);
            if (multiplication > 9) {
                int result = (multiplication / 10) + (multiplication % 10);
                sum += result;
            } else {
                sum += multiplication;
            }
        }
        return sum;
    }

    private String generateRandomSerialNumber() {
        Random rand = new Random();

        String serialNumber = String.valueOf(rand.nextInt(999));

        if (request.getGender() == FEMALE) {
            while (Integer.parseInt(serialNumber) % 2 != 0) {
                serialNumber = String.valueOf(rand.nextInt(999));
            }
        } else if (request.getGender() == MALE) {
            while (Integer.parseInt(serialNumber) % 2 == 0) {
                serialNumber = String.valueOf(rand.nextInt(999));
            }
        }

        if (serialNumber.length() == 1) {
            serialNumber = "00" + serialNumber;
        } else if (serialNumber.length() == 2) {
            serialNumber = "0" + serialNumber;
        }

        return serialNumber;
    }
}
