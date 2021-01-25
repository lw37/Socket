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

import static es.wei.Servidor.FILE_NAME;

public class Trabajador implements Runnable {
    public static ExecutorService executorServiceWork = Executors.newFixedThreadPool(2);
    private final Socket cliente;

    public Trabajador(Socket cliente) {
        this.cliente = cliente;
    }

    public String getCorreo(String s) {
        return s.substring(s.lastIndexOf(";") + 1);
    }

    public String fechaActual() {
        Date fecha = new Date();
        SimpleDateFormat formatear = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatear.format(fecha);
    }

    public void run() {
        try {
            System.out.println("Cliente conectado por "+cliente.getPort());
            OutputStream outputStream;
            InputStream input;
            outputStream = cliente.getOutputStream();
            input = cliente.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(input));
            PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream), true);
            int line;
            String nombreUsuario, apellidoUsuario, correoUsuario, usuario;
            int numeroEliminar;
            String compraVenta, informacionBolsa, nombreAccion;
            int idAccion;
            PasswordEncryptor encryptor = new BasicPasswordEncryptor();
            String secreto;
            String encriptado = null;

            while (true) {
                boolean bloqueado = true;
                FileWriter writer = new FileWriter(FILE_NAME, true);
                PrintWriter printerFile = new PrintWriter(writer, true);
                FileReader readerFile = new FileReader(FILE_NAME);
                BufferedReader bReaderFile = new BufferedReader(readerFile);
                printer.println("Introduce siguiente numeros:");
                printer.println("1:Crear nuevo usuario");
                printer.println("2:Eliminar usuario");
                printer.println("3:Notificar usuarios");
                printer.println("4:Bloquear servidor");
                printer.println("5:Desbloquear servidor");
                printer.println("0:Salir");
                FileWriter fwSecreto = new FileWriter("secreto.txt",true);
                FileReader frSecreto = new FileReader("secreto.txt");
                BufferedReader bReaderSecreto = new BufferedReader(frSecreto);
                if (bReaderSecreto.readLine()==null){
                    bloqueado=false;
                }
                line = Integer.parseInt(bReader.readLine());
                if (line == 0) {
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
                    printer.println(fechaActual() + "--Se crea nuevo usuario " + correoUsuario + "@gmial.com");
                }
                if (line == 2) {
                    ArrayList<String> usuarioLista = new ArrayList<String>();
                    while ((usuario = bReaderFile.readLine()) != null) {
                        usuarioLista.add(usuario);
                        printer.println(usuarioLista.size() + ":" + usuario);
                    }
                    printer.println("Introduce el ID para eliminar el usario");
                    numeroEliminar = Integer.parseInt(bReader.readLine());
                    if (numeroEliminar == 0) {
                        break;
                    }
                    printer.println(fechaActual() + "Has eliminado el usuario: " + usuarioLista.get(numeroEliminar - 1));
                    usuarioLista.remove(numeroEliminar - 1);
                    FileWriter writer1 = new FileWriter(FILE_NAME, false);
                    writer1.write("");
                    writer1.close();
                    for (int i = 0; i < usuarioLista.size(); i++) {
                        printerFile.println(usuarioLista.get(i));
                    }
                }
                if (line == 3 && !bloqueado) {
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
                        printer.println(fechaActual() + "--Se ha enviado a usuario: " + correoUsuario);
                        executorServiceWork.execute(new Envio(informacionBolsa, correoUsuario));
                    }
                } else if (line == 3) {
                    printer.println(fechaActual() + "--El servidor esta bloqueado");
                }
                if (line == 4 && !bloqueado) {
                    printer.println("Estas en la pagina de bloquear servidor.");
                    printer.println("Crear una contraseña con 6 caracteres('exit' para salir ) :");
                    while (true) {
                        secreto = bReader.readLine();
                        if (secreto.equals("exit")) {
                            break;
                        } else {
                            if (secreto.length() > 5) {
                                encriptado=encryptor.encryptPassword(secreto);
                                fwSecreto.write(encriptado);
                                fwSecreto.close();
                                System.out.println("Encriptado" + encriptado);
                                printer.println(fechaActual() + "--Has bloqueado servidor");
                                break;
                            } else {
                                printer.println("Por favor introduce 6 caracteres o exit");
                            }
                        }
                    }
                } else if (line == 4) {
                    printer.println(fechaActual() + "--El servido ha sido bloqueado.");
                }
                if (line == 5 && bloqueado) {
                    printer.println("Por favor introduce la contraseña para desbloquear('exit' para salir').");
                    while (true) {

                        secreto = bReader.readLine();
                        if (secreto.equals("exit")) {
                            break;
                        } else {
                            FileReader frSecreto1 = new FileReader("secreto.txt");
                            BufferedReader bReaderSecreto1 = new BufferedReader(frSecreto1);
                            encriptado=bReaderSecreto1.readLine();
                            frSecreto1.close();
                            bReaderSecreto1.close();
                            if (encryptor.checkPassword(secreto, encriptado)) {
                                FileWriter fwSecreto1 = new FileWriter("secreto.txt",false);
                                fwSecreto1.write("");
                                writer.close();
                                printer.println(fechaActual() + "--Has desbloqueado servidor");
                                break;
                            } else {
                                printer.println("La contraseña es incorrecta.");
                                printer.println("Por favor vuelve a introducir la contraseña o 'exit' para salir");
                            }
                        }
                    }
                } else if (line == 5) {
                    printer.println(fechaActual() + "--El servidor no esta bloqueado.");
                }
                fwSecreto.close();
                frSecreto.close();
                bReaderSecreto.close();
                writer.close();
                readerFile.close();
                bReaderFile.close();
                printerFile.close();
            }
        } catch (IOException e) {
            System.out.println("Usuario desconectado");
        }
    }
}

