package java_server.service;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import static java_server.JavaServer.handleResponseToBrowser;

public class FilesToServerService {

    public static void saveFile(Socket client, String requestBody) throws IOException {
        String fileName = requestBody.split("\r\n")[1].split(" ")[3].split("=")[1];
        String fn = fileName.substring(1, fileName.length() - 1);
        System.out.println(fn);
        String data = requestBody.split("\r\n")[4];
        byte[] bytes = data.getBytes(StandardCharsets.ISO_8859_1);
        FileOutputStream file = new FileOutputStream(fn);
        file.write(bytes);
        file.close();
        handleResponseToBrowser(client, "200 OK", "text/html", "File is saved".getBytes());
    }
}
