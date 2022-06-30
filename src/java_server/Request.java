package java_server;

import java.util.List;
import java.util.Map;

public class Request {

    private String method;
    private String path;
    private Map<String, String> params;
    private List<Headers> headers;

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public List<Headers> getHeaders() {
        return headers;
    }

    public Request(String method, String path, Map<String, String> params, List<Headers> headers) {
        this.method = method;
        this.path = path;
        this.params = params;
        this.headers = headers;
    }


    public Request() {
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", params=" + params +
                ", headers=" + headers +
                '}';
    }
}
