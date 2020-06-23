package encapsulations;

import java.util.ArrayList;

public class Controladora {

    private ArrayList<Usuario> listaUsarios = new ArrayList<>();
    private ArrayList<Producto> listaProductos = new ArrayList<>();
    private ArrayList<VentaProductos> historialVentas = new ArrayList<>();
    private static Controladora instancia;

    private Controladora() {
        //DEFAULT ADMIN USER
        listaUsarios.add(new Usuario("admin","admin","admin"));
        listaProductos.add(new Producto(1,"Disco De Estado Solido, 1TB",(float)8000.00));
    }

    public static Controladora getInstance(){
        if(instancia==null){
            instancia = new Controladora();
        }
        return instancia;
    }

    public void agregarUsuario(Usuario u){
        listaUsarios.add(u);
    }

    public void agregarProducto(Producto p){ listaProductos.add(p); }

    public void agregarVenta(VentaProductos vp){ historialVentas.add(vp); }

    public ArrayList<VentaProductos> getHistorialVentas() {
        return historialVentas;
    }

    public Usuario buscarUsuario(String user, String password){
        Usuario usuario = null;
        for (Usuario u: listaUsarios) {
            if(u.getUser().equals(user) && u.getPassw().equals(password)){
                usuario = u;
                break;
            }
        }
        return usuario;
    }

    public ArrayList<Producto> getListaProductos(){
        return this.listaProductos;
    }

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

    public void eliminarProducto(int id){
        int i;
        for(i=0;i<listaProductos.size();i++){
            if(listaProductos.get(i).getId() == id){
                listaProductos.remove(i);
                break;
            }
        }
    }

    public void editarProducto(int id, int newId, String nombre, float precio){
        int i;
        for(i=0;i<listaProductos.size();i++){
            if(listaProductos.get(i).getId() == id){
                listaProductos.get(i).setId(newId);
                listaProductos.get(i).setNombre(nombre);
                listaProductos.get(i).setPrecio(precio);
                break;
            }
        }
    }
}
