package Clases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClienteUDP {
    public static void main(String[] args) throws SocketException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int puerto = 4444;
        byte[] buffer;
        byte[] buffer2 = new byte[1024];
        try {
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            DatagramSocket cliente = new DatagramSocket();
            System.out.println("Introduce mensaje: ");
            String mensaje = br.readLine();
            buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer,buffer.length,direccionServidor,puerto);
            System.out.println("Se envio el mensaje");
            cliente.send(paquete);



            DatagramPacket respuesta = new DatagramPacket(buffer2,buffer2.length);
            cliente.receive(respuesta);
            System.out.println("Hora recibida");
            mensaje = new String(respuesta.getData());
            System.out.println(mensaje.trim());
            cliente.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
