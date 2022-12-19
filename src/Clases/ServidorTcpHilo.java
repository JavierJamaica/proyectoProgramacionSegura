package Clases;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ServidorTcpHilo extends Thread {

    static Socket socket = new Socket();

    public ServidorTcpHilo(Socket socket) {
        this.socket = socket;
    }

    static char[] characters = {'A', 'B', 'C', 'D', 'F', 'G', 'H', 'I', 'J', 'K'};

    /**
     * Funcion del Hilo ServidorTcpHilo en el cual toda la logica del servidor se ejecuta
     */
    @Override
    public void run() {
        int opDecifrado;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            KeyPairGenerator keygen;
            keygen = KeyPairGenerator.getInstance("RSA");
            System.out.println("Generando par de claves");
            KeyPair par = keygen.generateKeyPair();
            PrivateKey privada = par.getPrivate();
            PublicKey publica = par.getPublic();

            //Creamos los flujos

            //mandamos la clave publica

            oos.writeObject(publica);
            oos.writeObject(privada);
            System.out.println("Enviamos la clave publica y privada");

            do {

                System.out.println("Esperando opcion");
                byte[] opRecibido = (byte[]) ois.readObject();
                opDecifrado = Integer.parseInt(decifrarDatos(opRecibido, privada));
                System.out.println(opDecifrado);
                switch (opDecifrado) {
                    case 1:
                        String normasDes;


                        String mensaje = "Aceptas los terminos del banco?";
                        oos.writeObject(mensaje);
                        System.out.println("Recibimos mensaje normas: ");
                        byte[] normasRecibidas = (byte[]) ois.readObject();
                        normasDes = decifrarDatos(normasRecibidas, privada);
                        if (normasDes.equalsIgnoreCase("si")) {
                            System.out.println("Normas aceptadas");
                            Signature dsa = Signature.getInstance("SHA1WITHRSA");
                            dsa.initSign(privada);
                            dsa.update(mensaje.getBytes());
                            byte[] firma = dsa.sign(); //MENSAJE FIRMADO
                            oos.writeObject(firma);
                            System.out.println("Esperando datos del nuevo cliente!");
                            System.out.println("Esperando nombre");
                            byte[] nombreRecibidoCifrado = (byte[]) ois.readObject();
                            String nombreDecifrado = decifrarDatos(nombreRecibidoCifrado, privada);
                            System.out.println("Esperando apellido");
                            byte[] apellidoRecibidoCifrado = (byte[]) ois.readObject();
                            String apellidoDecifrado = decifrarDatos(apellidoRecibidoCifrado, privada);
                            System.out.println("Esperando edad");
                            byte[] edadRecibidaCifrada = (byte[]) ois.readObject();
                            int edadDecifrada = Integer.parseInt(decifrarDatos(edadRecibidaCifrada, privada));
                            System.out.println("Esperando email");
                            byte[] emailRecibidoCifrado = (byte[]) ois.readObject();
                            String emailDecifrado = decifrarDatos(emailRecibidoCifrado, privada);
                            System.out.println("Esperando usuario");
                            byte[] usuarioRecibidoCifrado = (byte[]) ois.readObject();
                            String usuarioDecifrado = decifrarDatos(usuarioRecibidoCifrado, privada);
                            System.out.println("Esperando contraseña");
                            byte[] contraRecibida = (byte[]) ois.readObject();

                            darAltaUsuario(nombreDecifrado, apellidoDecifrado, edadDecifrada, emailDecifrado, usuarioDecifrado, contraRecibida);
                        } else {
                            System.out.println("No acepto las normas");
                            break;
                        }


                    case 2:

                        int opAccesoPort;
                        do {
                            System.out.println("AccesoPortal");
                            byte[] accsesoCif = (byte[]) ois.readObject();
                            opAccesoPort = Integer.parseInt(decifrarDatos(accsesoCif, privada));
                            switch (opAccesoPort) {
                                case 1:
                                    int opDecifrado2;
                                    int rTransfer;
                                    do {
                                        System.out.println("esperando que hacer: ");
                                        byte[] opRecibido2 = (byte[]) ois.readObject();
                                        opDecifrado2 = Integer.parseInt(decifrarDatos(opRecibido2, privada));
                                        switch (opDecifrado2) {
                                            case 1:
                                                System.out.println("El cliente esta viendo su saldo");
                                                break;
                                            case 2:

                                                do {
                                                    System.out.println("Esperando opcion transfer");
                                                    byte[] transferDec = (byte[]) ois.readObject();
                                                    rTransfer = Integer.parseInt(decifrarDatos(transferDec, privada));

                                                    switch (rTransfer) {
                                                        case 1:
                                                            int dF = dobleFactor();
                                                            byte[] dFcif = ClienteTCP.cifrarDatosAlta(String.valueOf(dF), publica);
                                                            oos.writeObject(dFcif);
                                                            System.out.println("Esperando codF");
                                                            byte[] codEsCif = (byte[]) ois.readObject();
                                                            int codDescDf = Integer.parseInt(decifrarDatos(codEsCif, privada));
                                                            if (codDescDf == dF) {
                                                                String ok = "true";
                                                                byte[] okDec = ClienteTCP.cifrarDatosAlta(ok,publica);
                                                                oos.writeObject(okDec);
                                                                System.out.println("Autenticacion correcta");
                                                                System.out.println("Esperando cuentaN");
                                                                byte[] cuentaCif = (byte[]) ois.readObject();
                                                                String cuentaDec = decifrarDatos(cuentaCif, privada);
                                                                if (comprobrarCuenta(cuentaDec)) {
                                                                    String noExist = "true";
                                                                    oos.writeObject(noExist);
                                                                    System.out.println("Esperando dinero");
                                                                    byte[] dineroCif = (byte[]) ois.readObject();
                                                                    int dineroDec = Integer.parseInt(decifrarDatos(dineroCif, privada));
                                                                    System.out.println("llamamos funcion");
                                                                    transferencia(cuentaDec, dineroDec);
                                                                    System.out.println("Esperando usu");
                                                                    byte[] usuCif = (byte[]) ois.readObject();
                                                                    String usuDecif = decifrarDatos(usuCif, privada);
                                                                    retirarDineroTran(usuDecif, dineroDec);
                                                                    break;
                                                                } else {
                                                                    System.out.println("Cuenta no existe");
                                                                    String noExist = "false";
                                                                    oos.writeObject(noExist);
                                                                }
                                                            } else {
                                                                System.out.println("Autenticacion fallida");
                                                                String ok = "false";
                                                                byte[] okDec = ClienteTCP.cifrarDatosAlta(ok,publica);
                                                                oos.writeObject(okDec);

                                                            }

                                                            break;
                                                        case 2:
                                                            System.out.println("Leyendo cuentas");
                                                            leerCuenta();
                                                            break;
                                                        case 3:
                                                            System.out.println("El cliente cancelo transferencia");
                                                            break;
                                                        default:
                                                    }
                                                } while (rTransfer != 3);
                                                break;
                                            case 3:
                                                int rIngresar;
                                                do {
                                                    System.out.println("Esperando opcion");
                                                    byte[] idIngresarRetirarRecibido = (byte[]) ois.readObject();
                                                    rIngresar = Integer.parseInt(decifrarDatos(idIngresarRetirarRecibido, privada));
                                                    switch (rIngresar) {
                                                        case 1:
                                                            System.out.println("Esperando dinero a ingresar");
                                                            byte[] usuRecibido = (byte[]) ois.readObject();
                                                            String usuDecifrado = decifrarDatos(usuRecibido, privada);
                                                            byte[] dineroIngresar = (byte[]) ois.readObject();
                                                            int dineroDecifrado = Integer.parseInt(decifrarDatos(dineroIngresar, privada));
                                                            ingresarDinero(usuDecifrado, dineroDecifrado);

                                                            break;
                                                        case 2:
                                                            System.out.println("Esperando dinero a retirar");
                                                            byte[] usuRecibido2 = (byte[]) ois.readObject();
                                                            String usuDecifrado2 = decifrarDatos(usuRecibido2, privada);
                                                            byte[] dineroRetirar2 = (byte[]) ois.readObject();
                                                            int dineroDecifrado2 = Integer.parseInt(decifrarDatos(dineroRetirar2, privada));
                                                            retirarDinero(usuDecifrado2, dineroDecifrado2);
                                                            break;
                                                        case 3:
                                                            break;
                                                    }
                                                } while (rIngresar != 3);

                                                break;
                                            case 4:
                                                break;
                                            default:
                                                System.out.println("Tiene que ser una opcion valida");

                                        }
                                    } while (opDecifrado2 != 4);
                                    System.out.println("El cliente cerro sesion del portal");
                                    break;
                                case 2:
                                    System.out.println("El cliente esta en menu credenciales");
                                    break;
                                default:
                            }

                        } while (opAccesoPort != 2);


                    case 3:
                        break;
                }
            } while (opDecifrado != 3);
            System.out.println("El cliente cerro la conexion");


            //recibimos texto encriptado del cliente
            socket.close();
            oos.close();
            ois.close();

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException |
                 BadPaddingException | InvalidKeyException | ClassNotFoundException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Funcion que decifra los datos recibidos desde el cliente
     *
     * @param datos   recibe un String de datos apra decifrar
     * @param privada recibe la clave privada que se genera al principio
     * @return devuelve un String que serian los datos decifrados
     */
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

    /**
     * Funcion que usa los datos recibidos para guardalos en un fichero.dat
     *
     * @param nombre   que se guardara en el fichero
     * @param apellido que se guardara en el fichero
     * @param edad     que se guardara en el fichero
     * @param email    que se guardara en el fichero
     * @param usuario  que se guardara en el fichero
     * @param contra   que se guardara en el fichero
     */
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

    /**
     * Funcion que se usa para comprobar si el ususario existe en el fichero.dat
     *
     * @param usu recibe el usuario con el cual se buscara si existe
     * @return devuelve true si existe o false si no
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Funcion que lee el fichero Clientes.dat y los muestra por consola
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Funcionque lee el fichero Cuentas.dat y los muestra por consola
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Funcion qye se usa para hashear la contraseña que se recibe desde el cliente
     *
     * @param mensaje recibe la contraseña en bytes
     * @return devuevlve un array de bytes que estan ya hasheados
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getDigest(byte[] mensaje) throws NoSuchAlgorithmException {
        byte[] resumen;
        MessageDigest algoritmo = MessageDigest.getInstance(ALGORITMO);
        algoritmo.reset();
        algoritmo.update(mensaje);
        resumen = algoritmo.digest();
        return resumen;

    }

    /**
     * Funcion para comparar arrays de bytes hasheados y ver si son iguales
     *
     * @param resumen1 array de bytes a comparar
     * @param resumen2 array de bytes a comparar
     * @return devuelve true si son iguales o false si no lo son
     */
    public static boolean compararResumenes(byte[] resumen1, byte[] resumen2) throws NoSuchAlgorithmException {
        boolean sonIguales;
        MessageDigest algoritmo = MessageDigest.getInstance(ALGORITMO);
        algoritmo.reset();
        sonIguales = algoritmo.isEqual(resumen1, resumen2);
        return sonIguales;
    }

    /**
     * Funcion que nos permite ver el saldo de una cuenta
     *
     * @param usu recibe un nombre de usuario para ver el saldo que tiene en su cuenta
     */

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

    /**
     * Funcion que se usa para ingresar dinero y guardar los cambios en el fichero.dat
     *
     * @param usu            recibe el usuario del ingreso para identificarlo
     * @param dineroIngresar recibe cuanto se sumara al saldo por el ingreso
     */
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
        assert dataIn != null;
        dataIn.close();
        dataOsAux.close();
    }

    /**
     * Funcion que se usa para comprobar si existe una cuenta por medio del numero de cuenta
     *
     * @param cuentaN numero de cuenta que se usa para filtrar los datos
     * @return devuelve true si existe, false si no
     */
    public static boolean comprobrarCuenta(String cuentaN) throws IOException, ClassNotFoundException {

        List<Cuenta> cuentas = new ArrayList<>();
        File fichero = new File(".//src/Ficheros/Cuentas.dat");
        if (fichero.exists()) {
            FileInputStream fileInputStream = new FileInputStream(fichero);
            ObjectInputStream objectInputStream = null;

            while (fileInputStream.available() != 0) {
                objectInputStream = new ObjectInputStream(fileInputStream);
                Cuenta cuenta = (Cuenta) objectInputStream.readObject();
                cuentas.add(cuenta);
            }
            for (Cuenta c : cuentas) {
                if (c.getNumeroCuenta().equals(cuentaN)) {
                    return true;
                }
            }
            assert objectInputStream != null;
            objectInputStream.close();
            fileInputStream.close();
        }

        return false;
    }

    /**
     * Funcion que se usa para realizar transeferencias a diferentes cuentas
     *
     * @param cuenta         recibe el numero de cuenta al que ingresar el dinero
     * @param dineroIngresar recibe cuanto dinero se ingresara a esa cuenta
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void transferencia(String cuenta, int dineroIngresar) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());


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
            if (cuentaMod.getNumeroCuenta().equals(cuenta)) {
                cuentaMod.setSaldo(cuentaMod.getSaldo() + dineroIngresar);
                cuentaExiste = 1;
            }
            Cuenta cuentaMod2 = new Cuenta(cuentaMod.getSaldo(), cuentaMod.getNumeroCuenta(), cuentaMod.getCliente());
            dataOsAux.writeObject(cuentaMod2);

        }
        if (cuentaExiste > 0) {
            crearNuevaCuentaMod();
            System.out.println("Se ha hecho la transferencia correctamente");
        }
        assert dataIn != null;
        dataIn.close();
        dataOsAux.close();


    }

    /**
     * Funcion que retira dinero de la cuenta
     *
     * @param usu           recibe el usuario el cual retira dinero
     * @param dineroRetirar la cantidad de dinero a restar del saldo
     */

    public static void retirarDinero(String usu, int dineroRetirar) throws IOException, ClassNotFoundException {
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
                cuentaMod.setSaldo(cuentaMod.getSaldo() - dineroRetirar);
                cuentaExiste = 1;
            }
            Cuenta cuentaMod2 = new Cuenta(cuentaMod.getSaldo(), cuentaMod.getNumeroCuenta(), cuentaMod.getCliente());
            dataOsAux.writeObject(cuentaMod2);

        }
        if (cuentaExiste > 0) {
            crearNuevaCuentaMod();
            System.out.println("Se ha retirado el dinero correctamente");
        }
        assert dataIn != null;
        dataIn.close();
        dataOsAux.close();
    }

    /**
     * Funcion que resta dinero al que hace la transferencia
     *
     * @param usu           recibe el usuario que hace la transferencia
     * @param dineroRetirar y el dinero que se le descontara por la misma
     */
    public static void retirarDineroTran(String usu, int dineroRetirar) throws IOException, ClassNotFoundException {
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
                cuentaMod.setSaldo(cuentaMod.getSaldo() - dineroRetirar);
                cuentaExiste = 1;
            }
            Cuenta cuentaMod2 = new Cuenta(cuentaMod.getSaldo(), cuentaMod.getNumeroCuenta(), cuentaMod.getCliente());
            dataOsAux.writeObject(cuentaMod2);

        }
        if (cuentaExiste > 0) {
            crearNuevaCuentaMod();
            System.out.println("Se ha retirado el dinero correctamente");
        }
        assert dataIn != null;
        dataIn.close();
        dataOsAux.close();
    }

    /**
     * Funcion que sirve de apoyo para modificar los datos en el fichero.dat
     */
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

    /**
     * Funcion que se usa para la doble autenticacion de las transferencias
     *
     * @return devuelve un numero aleatoria que despues sera cifrado y enviado al cliente
     */
    public int dobleFactor() {
        Random r = new Random();
        return r.nextInt(0, 100);
    }


}
