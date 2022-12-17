package Clases;

import java.io.Serializable;

/**
 * @author Javier Jamaica
 * 01/12/2022 - 1:10
 */
public class Cuenta implements Serializable {
    private int saldo;
    private String numeroCuenta;
    private Cliente cliente;

    public Cuenta(int saldo, String numeroCuenta, Cliente cliente) {
        this.saldo = saldo;
        this.numeroCuenta = numeroCuenta;
        this.cliente = cliente;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Cuenta: " + numeroCuenta + ", Saldo: " + saldo + "\n"
                + "Cliente asociado: " + cliente;
    }
}
