import encapsulations.*;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;

import java.util.*;
import java.text.SimpleDateFormat;

import static io.javalin.apibuilder.ApiBuilder.before;

public class Main {
    public static void main(String[] args) {

        //JAVALIN INIT
        Javalin app = Javalin.create(config ->{
            config.addStaticFiles("/public"); //STATIC FILES -> /resources/public
        }).start(7000);

        //CONTROLLER CLASS
        Controladora controladora = Controladora.getInstance();

        //REGISTER THYMELEAF IN JAVALIN
        JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");

        /**
         * Login filter
         */

        /***
         * default endpoint
         */
        app.get("/", ctx -> ctx.redirect("/productos"));

        /**
         * Login endpoints and logic
         */

        app.before("/confirmarLog", ctx -> {
            String user = ctx.formParam("Username");
            String pass = ctx.formParam("Password");
            if(controladora.buscarUsuario(user,pass) == null){
                ctx.redirect("/login.html");
            }
        });

        app.post("/confirmarLog", ctx -> {
            String user = ctx.formParam("Username");
            String pass = ctx.formParam("Password");
            String id = ctx.req.getSession().getId();
            ctx.req.getSession().invalidate();
            ctx.sessionAttribute("user", user);
            ctx.redirect("/");
        });

        app.post("/crearUsuario", ctx -> {
            String user = ctx.formParam("Username");
            String pass = ctx.formParam("Password");
            String name = ctx.formParam("Name");
            System.out.println(user+" "+pass+" "+name);
            Usuario tmp = new Usuario(user,name,pass);
            controladora.agregarUsuario(tmp);
            ctx.redirect("/login.html");
        });

        /**
         * Products list logic using Thymeleaf
         */

        app.get("/productos", ctx -> {
            //get product list
            List<Producto> lista = controladora.getListaProductos();
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("lista",lista);
            if(ctx.sessionAttribute("user")== null){
                modelo.put("size",0);
            }else{
                if(ctx.sessionAttribute("carrito")==null){
                    CarroCompra carrito = new CarroCompra(ctx.req.getSession().getId());
                    ctx.sessionAttribute("carrito",carrito);
                    modelo.put("size",0);
                }else{
                    modelo.put("size",((CarroCompra)ctx.sessionAttribute("carrito")).getListaProductos().size());
                }
            }
            ctx.render("/templates/lista_productos.html",modelo);
        });

        app.before("/gestionProd", ctx -> {
            System.out.println("verifying user");
            if(ctx.sessionAttribute("user") == null){
                System.out.println("user not found");
                ctx.redirect("/login.html");
            }
            //continue req if user is admin
        });

        app.get("/gestionProd", ctx -> {

           if(ctx.sessionAttribute("user").equals("admin")){
               System.out.println("user is admin");
               List<Producto> lista = controladora.getListaProductos();
               Map<String, Object> modelo = new HashMap<>();
               modelo.put("lista",lista);
               modelo.put("size",((CarroCompra)ctx.sessionAttribute("carrito")).getListaProductos().size());
               ctx.render("/templates/gestionar_producto.html",modelo);
           }else{
               System.out.println("user is not admin");
               ctx.redirect("/error_permiso.html");
           }
        });

        /**
         * CRUD Productos
         */
        //CREATE
        app.post("/nuevoProd", ctx ->{
            int id = ctx.formParam("id", Integer.class).get();
            String nombre = ctx.formParam("nombre");
            float precio = ctx.formParam("precio", Float.class).get();

            Producto p = new Producto(id,nombre,precio);
            controladora.agregarProducto(p);

            ctx.redirect("/gestionProd");
        });

        //DELETE
        app.get("/eliminarProd/:id", ctx ->{
            int id = ctx.pathParam("id",Integer.class).get();
            controladora.eliminarProducto(id);
            ctx.redirect("/gestionProd");
        });

        //UPDATE
        app.get("/editarProd/:id", ctx ->{
            int id = ctx.pathParam("id",Integer.class).get();
            Map<String, Object> modelo = new HashMap<>();

            Producto p = controladora.buscarProducto(id);

            modelo.put("id",id);
            modelo.put("nombre",p.getNombre());
            modelo.put("precio",p.getPrecio());

            ctx.render("/templates/editarProducto.html",modelo);
        });

        app.post("/editarProd/:id", ctx -> {
            int id = ctx.pathParam("id",Integer.class).get();
            int NewId = ctx.formParam("id", Integer.class).get();
            String nombre = ctx.formParam("nombre", String.class).get();
            float precio = ctx.formParam("precio", Float.class).get();

            controladora.editarProducto(id,NewId,nombre,precio);

            ctx.redirect("/gestionProd");
        });

        /**
         * Añadir al carro
         */

        app.before("/anadirAlCarrito", ctx -> {
            if(ctx.sessionAttribute("carrito") == null){
                ctx.redirect("login.html");
            }
        });

        app.post("/anadirAlCarrito", ctx ->{

            int id = ctx.formParam("id", Integer.class).get();
            int cantidad = ctx.formParam("cantidad", Integer.class).get();

            Producto tmp = controladora.buscarProducto(id);
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            for(int i=0;i<cantidad;i++){
                carrito.addProducto(tmp);
            }

            System.out.println("Se agregaron "+cantidad+" Productos con el id "+id);
            ctx.redirect("/productos");
        });

        app.before("/carrito", ctx -> {
            if(ctx.sessionAttribute("carrito") == null){
                ctx.redirect("login.html");
            }
        });

        app.get("/carrito", ctx -> {
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("lista",carrito.getListaProductos());
            modelo.put("size",((CarroCompra)ctx.sessionAttribute("carrito")).getListaProductos().size());
            modelo.put("user",ctx.sessionAttribute("user"));
            ctx.render("/templates/micarrito.html",modelo);
        });

        app.get("/eliminarProdCarrito/:id",ctx ->{

            int id = ctx.pathParam("id",Integer.class).get();
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            carrito.eliminarProducto(carrito.buscarProducto(id));

            ctx.redirect("/carrito");
        });

        app.before("/procesar", ctx ->{
            CarroCompra carrito = ctx.sessionAttribute("carrito");
            if(carrito.getListaProductos().size()==0){
                ctx.redirect("/carrito");
            }
            //si no esta vacio, continua con el request
        });

        app.get("/procesar",ctx -> {

            CarroCompra carrito = ctx.sessionAttribute("carrito");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();

            ArrayList<Producto> ps = new ArrayList<>();
            for (Producto p:carrito.getListaProductos()) {
                ps.add(p);
            }

            VentaProductos venta = new VentaProductos(carrito.getId(),formatter.format(date),ctx.sessionAttribute("user"),ps);

            controladora.agregarVenta(venta);

            carrito.getListaProductos().clear();

            ctx.redirect("/success.html");

        });

        app.before("/ventasRealizadas",ctx ->{
           if(ctx.sessionAttribute("user")==null){
               ctx.redirect("/login.html");
           }else{
               if(!ctx.sessionAttribute("user").equals("admin")){
                   ctx.redirect("error_permiso.html");
               }
           }
        });

        app.get("/ventasRealizadas", ctx ->{
            ArrayList<VentaProductos> lista = controladora.getHistorialVentas();
            Map<String, Object> modelo = new HashMap<>();
            
            modelo.put("lista",lista);
            modelo.put("size",((CarroCompra)ctx.sessionAttribute("carrito")).getListaProductos().size());

            ctx.render("/templates/ver_ventas.html",modelo);
        });

        }
}