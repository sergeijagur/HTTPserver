package api.personal_code;

public enum HospitalOfBirth {

    KURESAARE_HAIGLA("001-010"),
    TARTU_ULIKOOLI_NAISTEKLIINIK("011-019"),
    IDA_TALLINNA_KESKHAIGLA_PELGULINNA_SUNNITUSMAJA("021-150"),
    KEILA_HAIGLA("151-160"),
    RAPLA_LOKSA_HIIUMAA_HAIGLA("161-220"),
    IDA_VIRU_KESKHAIGLA("221-270"),
    MAARJAMOISA_KLIINIKUM_JOGEVA_HAIGLA("271-370"),
    NARVA_HAIGLA("371-420"),
    PARNU_HAIGLA("421-470"),
    HAAPSALU_HAIGLA("471-490"),
    JARVAMAA_HAIGLA("491-520"),
    RAKVERE_TAPA_HAIGLA("521-570"),
    VALGA_HAIGLA("571-600"),
    VILJANDI_HAIGLA("601-650"),
    VORU_POLVA_HAIGLA("651-700"),
    VALISMAALANE("701-999");

    public final String label;

    HospitalOfBirth(String label) {
        this.label = label;
    }

}
