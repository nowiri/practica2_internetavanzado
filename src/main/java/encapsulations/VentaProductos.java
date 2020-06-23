package encapsulations;

import java.util.ArrayList;
import java.util.Date;

public class VentaProductos {

    private String id;
    private String fechaCompra;
    private String nombreCliente;
    private ArrayList<Producto> listaProductos;
    private float total;

    public VentaProductos(){

    }

    public VentaProductos(String id, String fechaCompra, String nombreCliente, ArrayList<Producto> listaProductos) {
        this.id = id;
        this.fechaCompra = fechaCompra;
        this.nombreCliente = nombreCliente;
        this.listaProductos = listaProductos;
        this.total = calcularTotal();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    private float calcularTotal(){
        float sum=0;
        for (Producto p: listaProductos) {
            sum += p.getPrecio();
        }
        return sum;
    }
}
