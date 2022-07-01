package api.salary_calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static api.salary_calculator.SalaryCalculatorService.*;


public class GrossSalary extends CalculatorInput {

    private final BigDecimal grossSalary;

    public static BigDecimal getSocialTax(BigDecimal grossSalary) {
        return grossSalary.multiply(SOCIAL_WELFARE_TAX).setScale(10, RoundingMode.HALF_UP);
    }

    public static BigDecimal getUnemploymentInsuranceByEmployer(BigDecimal grossSalary) {
        return grossSalary.multiply(EMPLOYER_UNEMPLOYMENT_TAX).setScale(10, RoundingMode.HALF_UP);
    }

    public static BigDecimal getPension(BigDecimal grossSalary) {
        return grossSalary.multiply(SECOND_PILLAR_PENSION_PAYMENT).setScale(10, RoundingMode.HALF_UP);
    }

    public static BigDecimal getUnemploymentInsuranceByEmployee(BigDecimal grossSalary) {
        return grossSalary.multiply(EMPLOYEE_UNEMPLOYMENT_TAX).setScale(10, RoundingMode.HALF_UP);
    }

    private static BigDecimal d(double value) {
        return BigDecimal.valueOf(value);
    }

    @Override
    public SalaryInformationResponse getSalaryInformation() {
        SalaryInformationResponse response = new SalaryInformationResponse();
        BigDecimal insuranceByEmployer = GrossSalary.getUnemploymentInsuranceByEmployer(grossSalary);
        BigDecimal socialTax = GrossSalary.getSocialTax(grossSalary);
        BigDecimal insuranceByEmployee = GrossSalary.getUnemploymentInsuranceByEmployee(grossSalary);
        BigDecimal pension = GrossSalary.getPension(grossSalary);
        BigDecimal incomeTax = grossSalary.subtract(pension).subtract(insuranceByEmployee)
                .subtract(getTaxExemptionFromGrossSalary(grossSalary)).multiply(INCOME_TAX_FROM_GROSS_SALARY);
        BigDecimal netSalary = grossSalary.subtract(pension).subtract(incomeTax).subtract(insuranceByEmployee);
        BigDecimal employerExpense = grossSalary.add(insuranceByEmployer.add(socialTax));
        response.setGrossSalary(grossSalary);
        response.setNetSalary(netSalary);
        response.setEmployerExpense(employerExpense);
        response.setTaxExemption(getTaxExemptionFromGrossSalary(grossSalary));
        response.setIncomeTax(incomeTax);
        response.setUnemploymentInsuranceByEmployer(insuranceByEmployer);
        response.setSocialTax(socialTax);
        response.setIncomeTax(incomeTax);
        response.setPension(pension);
        response.setUnemploymentInsuranceByEmployee(insuranceByEmployee);
        return response;
    }

    public GrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }


//

}
