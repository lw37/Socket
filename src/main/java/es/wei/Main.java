package es.wei;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT_HTTP = 8989;
    public static final String FILE_NAME = "usuarios.txt";

    public static void main(String[] args) throws IOException {
        File file = new File(FILE_NAME);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ServerSocket server = new ServerSocket(PORT_HTTP);
        Socket cliente;
        while (true) {
            cliente = server.accept();
            executorService.execute(new Work(cliente));
        }
    }

    private static class Work implements Runnable {
        private Socket client;

        public Work(Socket client) {
            this.client = client;
        }

        public void run() {
            System.out.println(" conectado cliente por puerto:" + client.getPort());
            OutputStream outputStream;
            InputStream input;
            try {
                outputStream = client.getOutputStream();
                input = client.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream), true);
                FileWriter writer =new FileWriter(FILE_NAME);
                PrintWriter printerFile=new PrintWriter(writer);
                int line;
                String nombre;
                String apellido;
                String correo;
                while (true) {
                    printer.println("Introduce siguiente numeros:");
                    printer.println("\n1:Crear nuevo usuario");
                    printer.println("\n2:Eliminar usuario");
                    printer.println("\n3:Notificar usuarios");
                    printer.println("\n4:Bloquear servidor");
                    printer.println("\n5:Desbloquear servidor");
                    printer.println("\n7:Salir");
                    line = Integer.parseInt(reader.readLine());
                    if (line == 1) {
                        printer.println("Introduce el nombre :");
                        nombre=reader.readLine();
                        printer.println("Introduce el apellido :");
                        apellido=reader.readLine();
                        printer.println("Introduce el email :");
                        correo=reader.readLine();
                        printerFile.println(nombre+";"+apellido+";"+correo+"@gmial.com");
                        printerFile.flush();
                    }
                    if(line==2){
                        
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
