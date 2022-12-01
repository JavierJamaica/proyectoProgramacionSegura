package Clases;

/**
 * @author Javier Jamaica
 * 01/12/2022 - 1:10
 */
public class Cuenta {
    private int saldo;
    private Cliente cliente;

    public Cuenta(int saldo, Cliente cliente) {
        this.saldo = saldo;
        this.cliente = cliente;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


    @Override
    public String toString() {
        return "Cuenta{" +
                "saldo=" + saldo +
                ", cliente=" + cliente +
                '}';
    }
}
