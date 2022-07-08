package java_server;

import com.google.gson.Gson;
import java_server.service.FilesToServerService;
import java_server.service.PersonalCodeControllerAndGeneratorService;
import java_server.service.SalaryCalculatorService;
import org.json.JSONObject;
import personal_code_custom.PersonalInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JavaServer {



    public static void main(String[] args)  {
        handleRun();
    }

    static void handleRun() {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void run() throws IOException {
        boolean running = true;
        int port = 8080;
        ServerSocket server = new ServerSocket(port);
        Socket client = new Socket();
        System.out.println("Listening for connection on port " + port);
        while (running) {
            try {
                client = server.accept();
            } catch (Exception e) {
                e.printStackTrace();
                client.close();
            }
            ClientHandler clientHandler = new ClientHandler(client);
            new Thread(clientHandler).start();
//            handleRequest(client);
        }
    }
    }


