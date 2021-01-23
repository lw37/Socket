package es.wei;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static es.wei.Main.FILE_NAME;

public class Work implements Runnable {
    public static ExecutorService executorServiceWork = Executors.newFixedThreadPool(2);
    private Socket cliente;
    private File fichero;

    public Work(Socket cliente, File fichero) {
        this.cliente = cliente;
        this.fichero = fichero;
    }

    public String getCorreo(String s) {
        return s.substring(s.lastIndexOf(";") + 1);
    }

    public void run() {
        System.out.println(" conectado cliente por puerto:" + cliente.getPort());
        OutputStream outputStream;
        InputStream input;
        try {
            outputStream = cliente.getOutputStream();
            input = cliente.getInputStream();
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
            PasswordEncryptor encryptor = new BasicPasswordEncryptor();
            String secreto = null;
            String encriptado = null;
            boolean bloqueado = false;
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
                    cliente.close();
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
                    fichero.delete();
                    fichero.createNewFile();
                    printer.println("Introduce el ID para eliminar el usario( 0 para salir)");
                    numeroEliminar = Integer.parseInt(bReader.readLine());
                    usuarioLista.remove(numeroEliminar - 1);
                    for (int i = 0; i < usuarioLista.size(); i++) {
                        printerFile.println(usuarioLista.get(i));
                        printerFile.flush();
                    }
                }
                if (line == 3 && bloqueado == false) {
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
                        default:
                            throw new IllegalStateException("Unexpected value: " + idAccion);
                    }
                    informacionBolsa = compraVenta + "-" + nombreAccion;
                    while ((usuario = bReaderFile.readLine()) != null) {
                        correoUsuario = getCorreo(usuario);
                        printer.println(formatear.format(fecha) + "--Se ha enviado a usuario: " + correoUsuario);
                        executorServiceWork.execute(new Envio(informacionBolsa, correoUsuario));
                    }
                }else if(line==3){
                    printer.println("El servidor esta bloqueado");
                }
                if (line == 4 && bloqueado == false) {
                    printer.println("Estas en la pagina de bloquear servidor.");
                    printer.println("Crear una contraseña con 6 caracteres('exit' para salir ) :");
                    while (true) {
                        secreto = bReader.readLine();
                        if (secreto.equals("exit")) {
                            break;
                        } else {
                            if (secreto.length() > 5) {
                                encriptado = encryptor.encryptPassword(secreto);
                                System.out.println("Encriptado" + encriptado);
                                bloqueado = true;
                                break;
                            } else {
                                printer.println("Por favor introduce 6 caracteres o exit");
                            }
                        }
                    }
                } else if (line == 4) {
                    printer.println("El servido ha sido bloqueado.");
                }
                if (line == 5 && bloqueado == true) {
                    printer.println("Por favor introduce la contraseña para desbloquear('exit' para salir').");
                    while (true) {
                        secreto = bReader.readLine();
                        if (secreto.equals("exit")) {
                            break;
                        } else {
                            if (encryptor.checkPassword(secreto, encriptado)) {
                                bloqueado = false;
                                break;
                            } else {
                                printer.println("La contraseña es incorrecta.");
                                printer.println("Por favor vuelve a introducir la contraseña o 'exit' para salir");
                            }
                        }
                    }
                } else if (line == 5) {
                    printer.println("El servidor no esta bloqueado.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

