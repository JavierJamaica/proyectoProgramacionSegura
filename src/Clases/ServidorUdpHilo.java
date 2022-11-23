package Clases;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;

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
}
