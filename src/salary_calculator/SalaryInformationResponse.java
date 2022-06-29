package salary_calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class SalaryInformationResponse {
    private BigDecimal employerExpense;
    private BigDecimal socialTax;
    private BigDecimal unemploymentInsuranceByEmployer;
    private BigDecimal grossSalary;
    private BigDecimal pension;
    private BigDecimal unemploymentInsuranceByEmployee;
    private BigDecimal taxExemption;
    private BigDecimal incomeTax;
    private BigDecimal netSalary;

    public BigDecimal getEmployerExpense() {
        return employerExpense;
    }

    public void setEmployerExpense(BigDecimal employerExpense) {
        this.employerExpense = employerExpense.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSocialTax() {
        return socialTax;
    }

    public void setSocialTax(BigDecimal socialTax) {
        this.socialTax = socialTax.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getUnemploymentInsuranceByEmployer() {
        return unemploymentInsuranceByEmployer;
    }

    public void setUnemploymentInsuranceByEmployer(BigDecimal unemploymentInsuranceByEmployer) {
        this.unemploymentInsuranceByEmployer = unemploymentInsuranceByEmployer.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPension() {
        return pension;
    }

    public void setPension(BigDecimal pension) {
        this.pension = pension.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getUnemploymentInsuranceByEmployee() {
        return unemploymentInsuranceByEmployee;
    }

    public void setUnemploymentInsuranceByEmployee(BigDecimal unemploymentInsuranceByEmployee) {
        this.unemploymentInsuranceByEmployee = unemploymentInsuranceByEmployee.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(BigDecimal incomeTax) {
        this.incomeTax = incomeTax.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(BigDecimal taxExemption) {
        this.taxExemption = taxExemption.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryInformationResponse that = (SalaryInformationResponse) o;
        return Objects.equals(employerExpense, that.employerExpense) && Objects.equals(socialTax, that.socialTax) && Objects.equals(unemploymentInsuranceByEmployer, that.unemploymentInsuranceByEmployer) && Objects.equals(grossSalary, that.grossSalary) && Objects.equals(pension, that.pension) && Objects.equals(unemploymentInsuranceByEmployee, that.unemploymentInsuranceByEmployee) && Objects.equals(taxExemption, that.taxExemption) && Objects.equals(incomeTax, that.incomeTax) && Objects.equals(netSalary, that.netSalary);
    }

    @Override
    public String toString() {
        return "SalaryInformationResponse{" +
                "employerExpense=" + employerExpense +
                ", socialTax=" + socialTax +
                ", unemploymentInsuranceByEmployer=" + unemploymentInsuranceByEmployer +
                ", grossSalary=" + grossSalary +
                ", pension=" + pension +
                ", unemploymentInsuranceByEmployee=" + unemploymentInsuranceByEmployee +
                ", taxFreeIncome=" + taxExemption +
                ", incomeTax=" + incomeTax +
                ", netSalary=" + netSalary +
                '}';
    }

    public SalaryInformationResponse(BigDecimal employerExpense, BigDecimal socialTax, BigDecimal unemploymentInsuranceByEmployer, BigDecimal grossSalary, BigDecimal pension, BigDecimal unemploymentInsuranceByEmployee, BigDecimal taxExemption, BigDecimal incomeTax, BigDecimal netSalary) {
        this.employerExpense = employerExpense;
        this.socialTax = socialTax;
        this.unemploymentInsuranceByEmployer = unemploymentInsuranceByEmployer;
        this.grossSalary = grossSalary;
        this.pension = pension;
        this.unemploymentInsuranceByEmployee = unemploymentInsuranceByEmployee;
        this.taxExemption = taxExemption;
        this.incomeTax = incomeTax;
        this.netSalary = netSalary;
    }

    public SalaryInformationResponse() {
    }
}
