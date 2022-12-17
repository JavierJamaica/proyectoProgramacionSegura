package Clases;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServidorTcpHilo extends Thread {

    static Socket socket = new Socket();

    public ServidorTcpHilo(Socket socket) {
        this.socket = socket;
    }

    static char[] characters = {'A', 'B', 'C', 'D', 'F', 'G', 'H', 'I', 'J', 'K'};

    @Override
    public void run() {

        try {
            KeyPairGenerator keygen;
            keygen = KeyPairGenerator.getInstance("RSA");
            System.out.println("Generando par de claves");
            KeyPair par = keygen.generateKeyPair();
            PrivateKey privada = par.getPrivate();
            PublicKey publica = par.getPublic();

            //Creamos los flujos
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //mandamos la clave publica

            oos.writeObject(publica);
            System.out.println("Enviamos la clave publica");
            byte[] opRecibido = (byte[]) ois.readObject();
            int opDecifrado = Integer.parseInt(decifrarDatos(opRecibido, privada));
            System.out.println("OPPP " + opDecifrado);
            switch (opDecifrado) {
                case 1:
                    System.out.println("Esperando datos del nuevo cliente!");
                    byte[] nombreRecibidoCifrado = (byte[]) ois.readObject();
                    String nombreDecifrado = decifrarDatos(nombreRecibidoCifrado, privada);
                    byte[] apellidoRecibidoCifrado = (byte[]) ois.readObject();
                    String apellidoDecifrado = decifrarDatos(apellidoRecibidoCifrado, privada);
                    byte[] edadRecibidaCifrada = (byte[]) ois.readObject();
                    int edadDecifrada = Integer.parseInt(decifrarDatos(edadRecibidaCifrada, privada));
                    byte[] emailRecibidoCifrado = (byte[]) ois.readObject();
                    String emailDecifrado = decifrarDatos(emailRecibidoCifrado, privada);
                    byte[] usuarioRecibidoCifrado = (byte[]) ois.readObject();
                    String usuarioDecifrado = decifrarDatos(usuarioRecibidoCifrado, privada);
                    byte[] contraRecibida = (byte[]) ois.readObject();

                    darAltaUsuario(nombreDecifrado, apellidoDecifrado, edadDecifrada, emailDecifrado, usuarioDecifrado, contraRecibida);
                case 2:
                    byte[] idIngresarRetirarRecibido = (byte[]) ois.readObject();
                    String idDinero = decifrarDatos(idIngresarRetirarRecibido, privada);
                    if (idDinero.equals("I")) {
                        byte[] usuRecibido = (byte[]) ois.readObject();
                        String usuDecifrado = decifrarDatos(usuRecibido, privada);
                        byte[] dineroIngresar = (byte[]) ois.readObject();
                        int dineroDecifrado = Integer.parseInt(decifrarDatos(dineroIngresar, privada));
                        ingresarDinero(usuDecifrado, dineroDecifrado);
                    } else if (idDinero.equals("R")) {

                    }

            }


            //recibimos texto encriptado del cliente

            oos.close();
            ois.close();

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException |
                 BadPaddingException | InvalidKeyException | ClassNotFoundException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }

    }

    public static String decifrarDatos(byte[] datos, PrivateKey privada) {
        try {
            Cipher descipher = Cipher.getInstance("RSA");
            descipher.init(Cipher.DECRYPT_MODE, privada);

            return new String(descipher.doFinal(datos));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    public static void darAltaUsuario(String nombre, String apellido, int edad, String email, String usuario, byte[] contra) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        File fichero = new File(".//src/Ficheros/Clientes.dat");
        FileOutputStream fileOutputStream = new FileOutputStream(fichero, true);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        Cliente cliente = new Cliente(nombre, apellido, edad, email, usuario, contra);
        objectOutputStream.writeObject(cliente);
        objectOutputStream.close();
        fileOutputStream.close();

        Random r = new Random();
        String numCuenta = String.valueOf(characters[r.nextInt(0, characters.length)]) + r.nextInt(1, 1000) + "BBVA";

        File ficheroCuenta = new File(".//src/Ficheros/Cuentas.dat");
        FileOutputStream fileOutputStreamC = new FileOutputStream(ficheroCuenta, true);
        ObjectOutputStream objectOutputStreamC = new ObjectOutputStream(fileOutputStreamC);
        Cuenta cuenta = new Cuenta(0, numCuenta, cliente);
        objectOutputStreamC.writeObject(cuenta);
        objectOutputStreamC.close();
        fileOutputStreamC.close();
        System.out.println("Cliente dado de alta");
        System.out.println("Se asocio el cliente con la cuenta: \n" + cuenta);
    }

    public static boolean comprobrarUsuario(String usu) throws IOException, ClassNotFoundException {

        List<Cliente> clientes = new ArrayList<>();
        File fichero = new File(".//src/Ficheros/Clientes.dat");
        if (fichero.exists()) {
            FileInputStream fileInputStream = new FileInputStream(fichero);
            ObjectInputStream objectInputStream = null;

            while (fileInputStream.available() != 0) {
                objectInputStream = new ObjectInputStream(fileInputStream);
                Cliente cliente = (Cliente) objectInputStream.readObject();
                clientes.add(cliente);
            }
            for (Cliente c : clientes) {
                if (c.getUsuario().equals(usu)) {
                    return true;
                }
            }
            assert objectInputStream != null;
            objectInputStream.close();
            fileInputStream.close();
        }

        return false;
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

    public static void leerCuenta() throws IOException, ClassNotFoundException {
        File fichero = new File(".//src/Ficheros/Cuentas.dat");
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
        byte[] resumen;
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

    public static void verSaldo(String usu) throws IOException, ClassNotFoundException {
        List<Cuenta> cuentas = new ArrayList<>();
        File fichero = new File(".//src/Ficheros/Cuentas.dat");
        FileInputStream fileInputStream = new FileInputStream(fichero);
        ObjectInputStream objectInputStream = null;
        while (fileInputStream.available() != 0) {
            objectInputStream = new ObjectInputStream(fileInputStream);
            Cuenta cuentaList = (Cuenta) objectInputStream.readObject();
            cuentas.add(cuentaList);
        }
        for (Cuenta c : cuentas) {
            if (c.getCliente().getUsuario().equals(usu)) {
                System.out.println("Cuenta con el numero: " + c.getNumeroCuenta() + ", tiene un saldo de: " + c.getSaldo());
            }
        }
        assert objectInputStream != null;
        objectInputStream.close();
        fileInputStream.close();
    }

    public static void ingresarDinero(String usu, int dineroIngresar) throws IOException, ClassNotFoundException {
        Cuenta cuentaMod;
        File fichero = new File(".//src/Ficheros/Cuentas.dat");
        FileInputStream fileIn = new FileInputStream(fichero);
        ObjectInputStream dataIn = null;
        int cuentaExiste = 0;

        File ficheroAux = new File(".//src/Ficheros/CuentasAux.dat");
        FileOutputStream fileOutAux = new FileOutputStream(ficheroAux);
        ObjectOutputStream dataOsAux = null;

        while (fileIn.available() != 0) {
            dataIn = new ObjectInputStream((fileIn));
            dataOsAux = new ObjectOutputStream(fileOutAux);
            try {
                cuentaMod = (Cuenta) dataIn.readObject();
            } catch (EOFException e) {
                System.out.println("No hay datos");
                break;
            }
            if (cuentaMod.getCliente().getUsuario().equals(usu)) {
                cuentaMod.setSaldo(cuentaMod.getSaldo() + dineroIngresar);
                cuentaExiste = 1;
            }
            Cuenta cuentaMod2 = new Cuenta(cuentaMod.getSaldo(), cuentaMod.getNumeroCuenta(), cuentaMod.getCliente());
            dataOsAux.writeObject(cuentaMod2);

        }
        if (cuentaExiste > 0) {
            crearNuevaCuentaMod();
            System.out.println("Se ha ingresado el dinero correctamente");
        }
        dataIn.close();
        dataOsAux.close();
    }

    public static void crearNuevaCuentaMod() throws IOException {
        Cuenta cuentaMod;

        File ficheroAux = new File(".//src/Ficheros/CuentasAux.dat");
        FileInputStream fileInAux = new FileInputStream(ficheroAux);

        File fichero = new File(".//src/Ficheros/Cuentas.dat");
        FileOutputStream fileOut = new FileOutputStream(fichero);

        try {
            while (fileInAux.available() != 0) {
                ObjectOutputStream dataOut = new ObjectOutputStream(fileOut);
                ObjectInputStream dataInAux = new ObjectInputStream(fileInAux);
                cuentaMod = (Cuenta) dataInAux.readObject();
                Cuenta cuentaMod2 = new Cuenta(cuentaMod.getSaldo(), cuentaMod.getNumeroCuenta(), cuentaMod.getCliente());
                dataOut.writeObject(cuentaMod2);

            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        fileOut.close();
        fileInAux.close();

    }


}
