package es.wei;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    private static final int PORT_HTTP = 9876;
    public static final String FILE_NAME = "usuarios.txt";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT_HTTP);
        Socket cliente;
        while (true) {
            cliente = server.accept();
            executorService.execute(new Trabajador(cliente));
        }
    }


    }


