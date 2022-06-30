package java_server;

public class Headers {

    private String name;
    private String value;

    public Headers(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Headers{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public Headers() {
    }
}
