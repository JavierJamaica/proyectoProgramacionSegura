package Clases;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class ServidorUdpHilo extends Thread {

    DatagramSocket socket = null;

    public ServidorUdpHilo(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            byte[] msg = new byte[1024];


            while (true) {
                DatagramPacket recibido = new DatagramPacket(new byte[1024], 1024);
                System.out.println("Servidor del banco activo!");
                socket.receive(recibido);
                System.out.println("Ha llegado una peticion!");
                System.out.println("Procedente de: " + recibido.getAddress());
                System.out.println("En el puerto: " + recibido.getPort());
                System.out.println("Sirviendo la peticion\n");

                String mensaje = "La hora del servidor es: " + LocalDateTime.now();
                msg = mensaje.getBytes();
                DatagramPacket paqueteConMensaje = new DatagramPacket(msg, msg.length, recibido.getAddress(), recibido.getPort());
                socket.send(paqueteConMensaje);


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void darAltaUsuario(String nombre, String apellido, int edad, String email, String usuario, byte[] contra) throws IOException {
        File fichero = new File(".//src/Ficheros/Clientes.dat");
        FileOutputStream fileOutputStream = new FileOutputStream(fichero, true);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        Cliente cliente = new Cliente(nombre, apellido, edad, email, usuario, contra);
        objectOutputStream.writeObject(cliente);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    public static void leerUsuario() throws IOException, ClassNotFoundException {
        File fichero = new File(".//src/Ficheros/Clientes.dat");
        FileInputStream fileInputStream = new FileInputStream(fichero);
        ObjectInputStream objectInputStream = null;

        while (fileInputStream.available() != 0) {
            objectInputStream = new ObjectInputStream(fileInputStream);
            System.out.println(objectInputStream.readObject());
        }
        assert objectInputStream != null;
        objectInputStream.close();
        fileInputStream.close();
    }

    private static final String ALGORITMO = "SHA-256";

    public static byte[] getDigest(byte[] mensaje) throws NoSuchAlgorithmException {
        byte[] resumen = null;
        MessageDigest algoritmo = MessageDigest.getInstance(ALGORITMO);
        algoritmo.reset();
        algoritmo.update(mensaje);
        resumen = algoritmo.digest();
        return resumen;

    }

    public static boolean compararResumenes(byte[] resumen1, byte[] resumen2) throws NoSuchAlgorithmException {
        boolean sonIguales;
        MessageDigest algoritmo = MessageDigest.getInstance(ALGORITMO);
        algoritmo.reset();
        sonIguales = algoritmo.isEqual(resumen1, resumen2);
        return sonIguales;


    }
}
