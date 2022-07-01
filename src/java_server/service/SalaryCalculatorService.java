package java_server.service;
import api.salary_calculator.GrossSalary;
import api.salary_calculator.NetSalary;
import api.salary_calculator.SalaryInformationResponse;
import api.salary_calculator.TotalExpense;
import java_server.Request;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;

import static java_server.JavaServer.getHtmlResponse;
import static java_server.JavaServer.handleResponseToBrowser;

public class SalaryCalculatorService {

    public static void calculateSalaryAndTaxes(Request request, Socket client) throws IOException {
        SalaryInformationResponse response = new SalaryInformationResponse();
        String salaryType = request.getRequestBody().get("salary-type");
        String salary = request.getRequestBody().get("salary");
        switch (salaryType) {
            case "gross": {
                response = new GrossSalary(new BigDecimal(salary)).getSalaryInformation();
                break;
            }
            case "net": {
                response = new NetSalary(new BigDecimal(salary)).getSalaryInformation();
                break;
            }
            case "total-expense": {
                response = new TotalExpense(new BigDecimal(salary)).getSalaryInformation();
                break;
            }
        }
        handleSalaryCalculatorResponse(response, client);
    }

    private static void handleSalaryCalculatorResponse(SalaryInformationResponse response, Socket client) throws IOException {
        String content = "<br>Total Cost for Employer (Wage Fund): " + response.getEmployerExpense() + "<br>" +
                "Social Tax: " + response.getSocialTax() + "<br>" +
                "Unemployment insurance (employer): " + response.getUnemploymentInsuranceByEmployer() + "<br>" +
                "Gross Salary/Wage: " + response.getGrossSalary() + "<br>" +
                "Funded pension (II pillar): " + response.getPension() + "<br>" +
                "Unemployment insurance (employee): " + response.getUnemploymentInsuranceByEmployee() + "<br>" +
                "Income Tax: " + response.getIncomeTax() + "<br>" +
                "Net Salary/Wage: " + response.getNetSalary() + "<br>";
        String htmlResponse = getHtmlResponse(content);
        handleResponseToBrowser(client, "200 OK", "text/html", htmlResponse.getBytes());
    }
}
