package Clases;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Javier Jamaica
 * 29/11/2022 - 20:37
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = -5402905524421154662L;
    private String nombre;
    private String apellido;
    private int edad;
    private String email;
    private String usuario;
    private byte[] contra;

    public Cliente(String nombre, String apellido, int edad, String email, String usuario, byte[] contra) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        this.contra = contra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public byte[] getContra() {
        return contra;
    }

    public void setContra(byte[] contra) {
        this.contra = contra;
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + "\n" + "Apellidos: " + apellido + "\n" + "Edad: " + edad + "\n" + "Email: " + email + "\n"
                + "Usuario: " + usuario + "\n" + "Contraseña: " + Arrays.toString(contra);

    }
}
