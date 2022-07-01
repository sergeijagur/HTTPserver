package api.personal_code;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static api.personal_code.Gender.MALE;

public class EstonianPersonalCode extends PersonalCode {

    String personalCode;
    PersonalCodeInfoRequest request;

    public EstonianPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    public EstonianPersonalCode(PersonalCodeInfoRequest request) {
        this.request = request;
    }

    @Override
    public boolean isValid() {
        char genderIndex = personalCode.charAt(0);

        try {
            LocalDate.parse(getBirthDate());
        } catch (DateTimeParseException a) {
            return false;
        }

        if (personalCode.length() == 11 && (genderIndex == '3' || genderIndex == '4' || genderIndex == '5' || genderIndex == '6')) {
            if (LocalDate.now().isBefore(LocalDate.parse(getBirthDate()))) {
                return false;
            }

            return checkSum(personalCode) == Integer.parseInt(String.valueOf(personalCode.charAt(personalCode.length() - 1)));
        }
        return false;
    }

    @Override
    public String getBirthDate() {
        String year = personalCode.substring(1, 3);
        String month = personalCode.substring(3, 5);
        String day = personalCode.substring(5, 7);

        if (personalCode.charAt(0) == '3' || personalCode.charAt(0) == '4') {
            year = "19" + year;
        } else {
            year = "20" + year;
        }
        return year + "-" + month + "-" + day;
    }

    @Override
    public String generatePersonalCode() {
        String generatedPersonalCode = "";
        generatedPersonalCode += generateCenturyIndex();
        generatedPersonalCode += request.getBirthOfDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
        generatedPersonalCode += getHospitalIndex();
        generatedPersonalCode += checkSum(generatedPersonalCode);

        return generatedPersonalCode;
    }

    private String getHospitalIndex() {
        int[] ranges = Arrays.stream(request.getHospital().label.split("-")).mapToInt(Integer::parseInt).toArray();

        Random rand = new Random();
        String hospitalIndex = String.valueOf(rand.nextInt((ranges[1] - ranges[0]) + 1) + ranges[0]);
        if (hospitalIndex.length() == 1) {
            hospitalIndex = "00" + hospitalIndex;
        } else if (hospitalIndex.length() == 2) {
            hospitalIndex = "0" + hospitalIndex;
        }
        return hospitalIndex;
    }

    private String generateCenturyIndex() {
        String index = "";
        if (request.getBirthOfDate().getYear() < 2000) {
            if (request.getGender() == MALE) {
                index += "3";
            } else {
                index += "4";
            }
        } else {
            if (request.getGender() == MALE) {
                index += "5";
            } else {
                index += "6";
            }
        }
        return index;
    }

    private static int checkSum(String personalCode) {

        char[] chars = personalCode.substring(0, 10).toCharArray();

        int correctCheckSum;

        int sum = calculateFirstStageScale(chars);

        if (sum % 11 < 10) {
            correctCheckSum = sum % 11;
        } else {
            correctCheckSum = calculateSecondStageScale(chars);
        }

        return correctCheckSum;
    }

    private static int calculateSecondStageScale(char[] chars) {
        List<Integer> secondStageScale = new ArrayList<>(List.of(3, 4, 5, 6, 7, 8, 9, 1, 2, 3));

        int result = 0;

        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            sum += Integer.parseInt(String.valueOf(chars[i])) * secondStageScale.get(i);
        }

        if (sum % 11 < 10) {
            result = sum % 11;
        }

        return result;
    }

    private static int calculateFirstStageScale(char[] chars) {
        List<Integer> firstStageScale = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 1));

        int result = 0;

        for (int i = 0; i < chars.length; i++) {
            int a;
            try {
                a = Integer.parseInt(String.valueOf(chars[i]));
            } catch (NumberFormatException e) {
                return -1;
            }
            result += a * firstStageScale.get(i);
        }
        return result;
    }

}
