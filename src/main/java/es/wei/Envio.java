package es.wei;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.io.*;
import java.text.MessageFormat;

public class Envio implements Runnable {
    private String infoBolsa, usuarioCorreo;

    public Envio(String infoBolsa, String usuarioCorreo) {
        this.infoBolsa = infoBolsa;
        this.usuarioCorreo = usuarioCorreo;
    }

    @Override
    public void run() {
        String local = "no-reply@servidor.com";
        Email email = new SimpleEmail();
        email.setHostName("localhost");
        email.setSmtpPort(1025);
        try {
            email.setFrom(local);
            email.setSubject("Informacion de BOLSA");
            email.setMsg(MessageFormat.format("Estimado cliente {0} : Nuevo informacion del mercado ---  {1} ", usuarioCorreo, infoBolsa));
            email.addTo(usuarioCorreo);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }


    }

}
