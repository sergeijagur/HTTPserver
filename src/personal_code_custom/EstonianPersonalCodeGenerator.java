package personal_code_custom;



import api.personal_code.PersonalCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class EstonianPersonalCodeGenerator extends PersonalCode {

    NewInfoRequest request;


    public EstonianPersonalCodeGenerator(NewInfoRequest request) {
        this.request = request;
    }


    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String getBirthDate() {
        return null;
    }

    @Override
    public String generatePersonalCode() {
        String generatedPersonalCode = "";
        generatedPersonalCode += generateCenturyIndex();
        generatedPersonalCode += request.getBirthOfDate().substring(2);
        generatedPersonalCode += getHospitalIndex();
        generatedPersonalCode += checkSum(generatedPersonalCode);

        return generatedPersonalCode;
    }

    private String getHospitalIndex() {

        Random rand = new Random();
        String hospitalIndex = String.valueOf(rand.nextInt(1000));
        if (hospitalIndex.length() == 1) {
            hospitalIndex = "00" + hospitalIndex;
        } else if (hospitalIndex.length() == 2) {
            hospitalIndex = "0" + hospitalIndex;
        }
        return hospitalIndex;
    }

    private String generateCenturyIndex() {
        String index = "";
        if (Integer.parseInt(request.getBirthOfDate().substring(0, 4)) < 2000) {
            if (request.getGender().equals("male")) {
                index += "3";
            } else if (request.getGender().equals("female")) {
                index += "4";
            }
        } else if (Integer.parseInt(request.getBirthOfDate().substring(0, 4)) > 2000) {
            if (request.getGender().equals("male")) {
                index += "5";
            } else if (request.getGender().equals("female")) {
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
