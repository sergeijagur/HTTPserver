package main.service;

import main.domain.CalculatorInput;
import main.domain.GrossSalary;
import main.domain.NetSalary;
import main.domain.TotalExpense;

import java.io.*;
import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

public class SalaryCalculatorService {

    public static final BigDecimal SOCIAL_WELFARE_TAX = d(33).movePointLeft(2);
    public static final BigDecimal EMPLOYER_UNEMPLOYMENT_TAX = d(0.8).movePointLeft(2);
    public static final BigDecimal EMPLOYEE_UNEMPLOYMENT_TAX = d(1.6).movePointLeft(2);
    public static final BigDecimal INCOME_TAX_FROM_GROSS_SALARY = d(20).movePointLeft(2);
    public static final BigDecimal SECOND_PILLAR_PENSION_PAYMENT = d(2.0).movePointLeft(2);
    public static final BigDecimal EMPLOYER_EXPENSE_TO_GROSS = EMPLOYER_UNEMPLOYMENT_TAX.add(SOCIAL_WELFARE_TAX).add(d(1));
    public static final BigDecimal MAX_TAX_EXEMPTION = d(500.00);
    public static final BigDecimal MAX_GROSS_SALARY_FOR_BASIC_TAX_EXEMPTION = d(2100);
    public static final BigDecimal MIN_GROSS_SALARY_FOR_BASIC_TAX_EXEMPTION = d(1200);
    public static final BigDecimal MAX_NET_SALARY_FOR_BASIC_TAX_EXEMPTION = d(1620);
    public static final BigDecimal MIN_NET_SALARY_FOR_BASIC_TAX_EXEMPTION = d(1025.44);
    public static final BigDecimal INCOME_TAX_FROM_NET_SALARY = d(0.25);
    public static final BigDecimal NET_SALARY_PLUS_INCOME_TAX = d(0.964);


    public static void main(String[] args) {
        printResponse(getSalaryInformation(new GrossSalary(BigDecimal.valueOf(5000))));
    }
    public static SalaryInformationResponse getSalaryInformation(CalculatorInput calculatorInput) {
        return calculatorInput.getSalaryInformation();
    }


    public static BigDecimal getTaxExemptionFromGrossSalary(BigDecimal grossSalary) {
        if (grossSalary.compareTo(MIN_GROSS_SALARY_FOR_BASIC_TAX_EXEMPTION) < 0 && grossSalary.compareTo(d(0)) > 0) {
            return MAX_TAX_EXEMPTION;
        } else if (grossSalary.compareTo(MIN_GROSS_SALARY_FOR_BASIC_TAX_EXEMPTION) > 0 && grossSalary.compareTo(MAX_GROSS_SALARY_FOR_BASIC_TAX_EXEMPTION) < 0) {
            BigDecimal remainderOfMinSalaryForBasicTaxExemption = grossSalary.subtract(MIN_GROSS_SALARY_FOR_BASIC_TAX_EXEMPTION);
            BigDecimal remainderOfTaxExemption = remainderOfMinSalaryForBasicTaxExemption.multiply(MAX_TAX_EXEMPTION
                    .divide(d(900), 10, HALF_UP));
            return MAX_TAX_EXEMPTION.subtract(remainderOfTaxExemption);
        } else {
            return d(0);
        }
    }


    private static void printResponse(SalaryInformationResponse response) {
        System.out.println(" ");
        System.out.println("\u001B[31m" + "SALARY INFORMATION:" + "\u001B[0m");
        System.out.println("Employer expense: " + response.getEmployerExpense() + " ?");
        System.out.println("Social tax: " + response.getSocialTax() + " ?");
        System.out.println("Unemployment insurance by employer: " + response.getUnemploymentInsuranceByEmployer() + " ?");
        System.out.println("Gross salary: " + response.getGrossSalary() + " ?");
        System.out.println("Pension fund: " + response.getPension() + " ?");
        System.out.println("Unemployment insurance by employee: " + response.getUnemploymentInsuranceByEmployee() + " ?");
        System.out.println("Tax exemption: " + response.getTaxExemption() + " ?");
        System.out.println("Income tax: " + response.getIncomeTax() + " ?");
        System.out.println("Net salary: " + response.getNetSalary() + " ?");
        System.out.println(" ");


    }

    private static BigDecimal d(double value) {
        return BigDecimal.valueOf(value);
    }
}
