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
        int respuesta = 0;
        try {
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            DatagramSocket cliente = new DatagramSocket();

            do {
                try {
                    System.out.println("""
                            Bienvenido a JamaicaBank que desea hacer?.
                            1. Darse de alta
                            2. Entrar a su portal
                            3. Salir""");
                    System.out.print("Res: ");
                    respuesta = Integer.parseInt(br.readLine());
                    switch (respuesta) {
                        case 1:
                            int resIntroducirDatos = 0;
                            do {
                                System.out.println("""
                                        Que desea hacer?
                                        1. Introducir datos
                                        2. Salir""");
                                System.out.print("Res: ");
                                resIntroducirDatos = Integer.parseInt(br.readLine());
                            } while (resIntroducirDatos != 2);
                            break;
                        case 2:

                            break;
                        case 3:
                            System.out.println("Adios! 😁");
                            break;
                        default:
                            System.out.println("Tiene que ser una opcion valida");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Tiene que ser un numero!");
                }

            } while (respuesta != 3);

            System.out.println("Introduce mensaje: ");
            String mensaje = br.readLine();
            buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccionServidor, puerto);
            System.out.println("Se envio el mensaje");
            cliente.send(paquete);


            DatagramPacket respuestaDa = new DatagramPacket(buffer2, buffer2.length);
            cliente.receive(respuestaDa);
            System.out.println("Hora recibida");
            mensaje = new String(respuestaDa.getData());
            System.out.println(mensaje.trim());
            cliente.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
