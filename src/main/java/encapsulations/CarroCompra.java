package encapsulations;

import java.util.ArrayList;

public class CarroCompra {

    private String id;
    private ArrayList<Producto> listaProductos;

    public CarroCompra(String id) {
        this.id = id;
        this.listaProductos = new ArrayList<Producto>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public void addProducto(Producto p){listaProductos.add(p);}

    public Producto buscarProducto(int id){
        Producto tmp = null;
        for (Producto p:listaProductos) {
            if(p.getId() == id){
                System.out.println("found it");
                tmp = p;
                break;
            }
        }
        return tmp;
    }

    public void eliminarProducto(Producto p){
        listaProductos.remove(p);
    }
}
