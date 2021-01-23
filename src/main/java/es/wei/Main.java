package es.wei;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            executorService.execute(new Work(cliente, file));
        }
    }

    private static class Work implements Runnable {

        private Socket client;
        private File file;

        public Work(Socket client, File file) {
            this.client = client;
            this.file = file;
        }

        public String getCorreo(String s) {
            return s.contains(";") ? s.substring(s.indexOf(';')).trim() : "";
        }

        public void run() {
            System.out.println(" conectado cliente por puerto:" + client.getPort());
            OutputStream outputStream;
            InputStream input;
            try {
                outputStream = client.getOutputStream();
                input = client.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(input));
                PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream), true);
                FileWriter writer = new FileWriter(FILE_NAME);
                PrintWriter printerFile = new PrintWriter(writer, true);
                FileReader readerFile = new FileReader(FILE_NAME);
                BufferedReader bReaderFile = new BufferedReader(readerFile);
                int line;
                String nombreUsuario;
                String apellidoUsuario;
                String correoUsuario;
                String usuario;
                Date fecha = new Date();
                SimpleDateFormat formatear = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                int numeroEliminar;
                String compraVenta;
                String informacionBolsa;
                int idAccion;
                String nombreAccion;
                while (true) {
                    printer.println("Introduce siguiente numeros:");
                    printer.println("\n1:Crear nuevo usuario");
                    printer.println("\n2:Eliminar usuario");
                    printer.println("\n3:Notificar usuarios");
                    printer.println("\n4:Bloquear servidor");
                    printer.println("\n5:Desbloquear servidor");
                    printer.println("\n0:Salir");

                    line = Integer.parseInt(bReader.readLine());
                    if (line == 0) {
                        bReader.close();
                        printerFile.close();
                        readerFile.close();
                        printer.close();
                        client.close();
                    }
                    if (line == 1) {
                        printer.println("Introduce el nombre :");
                        nombreUsuario = bReader.readLine();
                        printer.println("Introduce el apellido :");
                        apellidoUsuario = bReader.readLine();
                        printer.println("Introduce el email(....@gmail.com) :");
                        correoUsuario = bReader.readLine();
                        printerFile.println(nombreUsuario + ";" + apellidoUsuario + ";" + correoUsuario + "@gmial.com");
                        printer.println(formatear.format(fecha) + "--Se crea nuevo usuario " + correoUsuario + "@gmial.com");
                    }
                    if (line == 2) {
                        ArrayList<String> usuarioLista = new ArrayList<String>();
                        while ((usuario = bReaderFile.readLine()) != null) {
                            usuarioLista.add(usuario);
                            printer.println(usuarioLista.size() + ":" + usuario);
                        }
                        file.delete();
                        file.createNewFile();
                        printer.println("Introduce el ID para eliminar el usario( 0 para salir)");
                        numeroEliminar = Integer.parseInt(bReader.readLine());
                        usuarioLista.remove(numeroEliminar - 1);
                        for (int i = 0; i < usuarioLista.size(); i++) {
                            printerFile.println(usuarioLista.get(i));
                            printerFile.flush();
                        }
                    }
                    if (line == 3) {
                        printer.println("Escribe un lo que quieres informar( BUY o SELL ): ");
                        compraVenta = bReader.readLine();
                        printer.println("1: SAN: Banco Santander");
                        printer.println("2: BBVA: Banco BBVA");
                        printer.println("3: SAB: Banco Sabadell");
                        printer.println("4: EURUSD: Divisa de EURO contra DÓLAR estadounidense.");
                        printer.println("5: USDJPY: Divisa de DÓLAR contra Yen japonés.");
                        printer.println("6: BTCUSD: Criptodivisa de BITCOIN contra DÓLAR estadounidense.\n");
                        printer.println("Introduce el ID de la accion que quieres informar :");
                        idAccion = Integer.parseInt(bReader.readLine());
                        switch (idAccion) {
                            case 1:
                                nombreAccion = "SAN";
                                break;
                            case 2:
                                nombreAccion = "BBVA";
                                break;
                            case 3:
                                nombreAccion = "SAB";
                                break;
                            case 4:
                                nombreAccion = "EURUSD";
                                break;
                            case 5:
                                nombreAccion = "USDJPY";
                                break;
                            case 6:
                                nombreAccion = "BTCUSD";
                                break;
                        }
                        while ((usuario = bReaderFile.readLine()) != null) {
                            correoUsuario = getCorreo(usuario);
                            printer.println("Se ha enviado a usuario: " + correoUsuario);
                        }
                    }
                    if (line == 4) {

                    }
                    if (line == 5) {

                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
