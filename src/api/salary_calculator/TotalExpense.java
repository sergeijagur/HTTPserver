package api.salary_calculator;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static api.salary_calculator.SalaryCalculatorService.*;


public class TotalExpense extends CalculatorInput {


    private final BigDecimal totalExpense;


    @Override
    public SalaryInformationResponse getSalaryInformation() {
        BigDecimal grossSalary = getGrossSalaryByTotalExpense();
        SalaryInformationResponse response = new SalaryInformationResponse();
        BigDecimal insuranceByEmployer = GrossSalary.getUnemploymentInsuranceByEmployer(grossSalary);
        BigDecimal socialTax = GrossSalary.getSocialTax(grossSalary);
        BigDecimal insuranceByEmployee = GrossSalary.getUnemploymentInsuranceByEmployee(grossSalary);
        BigDecimal pension = GrossSalary.getPension(grossSalary);
        BigDecimal incomeTax = grossSalary.subtract(pension).subtract(insuranceByEmployee)
                .subtract(getTaxExemptionFromGrossSalary(grossSalary)).multiply(INCOME_TAX_FROM_GROSS_SALARY);
        BigDecimal netSalary = grossSalary.subtract(pension).subtract(incomeTax).subtract(insuranceByEmployee);
        response.setEmployerExpense(totalExpense);
        response.setGrossSalary(grossSalary);
        response.setNetSalary(netSalary);
        response.setTaxExemption(getTaxExemptionFromGrossSalary(grossSalary));
        response.setIncomeTax(incomeTax);
        response.setUnemploymentInsuranceByEmployer(insuranceByEmployer);
        response.setSocialTax(socialTax);
        response.setIncomeTax(incomeTax);
        response.setPension(pension);
        response.setUnemploymentInsuranceByEmployee(insuranceByEmployee);
        return response;

    }

    private BigDecimal getGrossSalaryByTotalExpense() {
        return totalExpense.divide(EMPLOYER_EXPENSE_TO_GROSS, 2, HALF_UP);
    }


    public TotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    private static BigDecimal d(double value) {
        return BigDecimal.valueOf(value);
    }

}
