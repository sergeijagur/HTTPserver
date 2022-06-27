package java_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class java_http_server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Listening for connection on port 8080");
        while (true) {
            Socket client = server.accept();
            InputStreamReader is = new InputStreamReader(client.getInputStream());
            BufferedReader bf = new BufferedReader(is);
            String line = bf.readLine();
            while (!line.isEmpty()) {
                System.out.println(line);
                line= bf.readLine();
            }
//            try (Socket socket = server.accept()) {
//                Date today = new Date();
//                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + today + "\rHello world";
            client.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            FileInputStream in = new FileInputStream("html_files/main.html");
            client.getOutputStream().write(in.readAllBytes());
            in.close();


        }


//        }
    }
}
