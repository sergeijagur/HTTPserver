package salary_calculator;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static salary_calculator.SalaryCalculatorService.*;

public class NetSalary extends CalculatorInput {

    private final BigDecimal netSalary;

    public NetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    @Override
    public SalaryInformationResponse getSalaryInformation() {

        BigDecimal grossSalary = getGrossSalaryByNetSalary(netSalary);
        SalaryInformationResponse response = new SalaryInformationResponse();
        BigDecimal insuranceByEmployer = GrossSalary.getUnemploymentInsuranceByEmployer(grossSalary);
        BigDecimal socialTax = GrossSalary.getSocialTax(grossSalary);
        BigDecimal insuranceByEmployee = GrossSalary.getUnemploymentInsuranceByEmployee(grossSalary);
        BigDecimal pension = GrossSalary.getPension(grossSalary);
        BigDecimal incomeTax = grossSalary.subtract(pension).subtract(insuranceByEmployee)
                .subtract(getTaxExemptionFromGrossSalary(grossSalary)).multiply(INCOME_TAX_FROM_GROSS_SALARY);
        BigDecimal netSalary = grossSalary.subtract(pension).subtract(incomeTax).subtract(insuranceByEmployee);
        BigDecimal employerExpense = grossSalary.add(insuranceByEmployer.add(socialTax));
        response.setNetSalary(netSalary);
        response.setGrossSalary(grossSalary);
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


    public BigDecimal getGrossSalaryByNetSalary(BigDecimal netSalary) {
        if (netSalary.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else if (netSalary.compareTo(MAX_NET_SALARY_FOR_BASIC_TAX_EXEMPTION) > 0) {
            BigDecimal incomeTax = netSalary.multiply(INCOME_TAX_FROM_NET_SALARY);
            return netSalary.add(incomeTax).divide(NET_SALARY_PLUS_INCOME_TAX, 2, HALF_UP);
        } else if (netSalary.compareTo(MAX_NET_SALARY_FOR_BASIC_TAX_EXEMPTION) < 0 && netSalary.compareTo(MIN_NET_SALARY_FOR_BASIC_TAX_EXEMPTION) > 0) {
            return netSalary.multiply(d(1.514947)).subtract(d(353.487746));
        } else if (netSalary.compareTo(MIN_NET_SALARY_FOR_BASIC_TAX_EXEMPTION) < 0) {
            BigDecimal incomeTax = netSalary.subtract(MAX_TAX_EXEMPTION).multiply(INCOME_TAX_FROM_NET_SALARY);
            return netSalary.add(incomeTax).divide(NET_SALARY_PLUS_INCOME_TAX, 10, HALF_UP).setScale(2, HALF_UP);
        }
        return null;
    }


    private static BigDecimal d(double value) {
        return BigDecimal.valueOf(value);
    }


}
