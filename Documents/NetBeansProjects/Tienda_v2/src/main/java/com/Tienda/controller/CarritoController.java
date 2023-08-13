package com.Tienda.controller;

import com.tienda.dao.FacturaDao;
import com.tienda.dao.ProductoDao;
import com.tienda.dao.VentaDao;
import com.tienda.domain.Factura;
import com.tienda.domain.Item;
import com.tienda.domain.Producto;
import com.tienda.domain.Usuario;
import com.tienda.domain.Venta;
import com.tienda.service.ItemService;
import static com.tienda.service.ItemService.listaItems;
import com.tienda.service.ProductoService;
import com.tienda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CarritoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        return "index";
    }

    @Autowired
    private ItemService itemService;

    @GetMapping("/carrito/agregar/{idProducto}")
    public ModelAndView agregarItem(Model model, Item item) {
        Item item2 = itemService.get(item);
        if (item2 == null) {
            Producto producto = productoService.getProducto(item);
            item2 = new Item(producto);
        }
        itemService.save(item2);
        var lista = itemService.gets();
        var totalCarritos = 0;
        var carritoTotalVenta = 0;
        for (Item i : lista) {
            totalCarritos += i.getCantidad();
            carritoTotalVenta += (i.getCantidad() * i.getPrecio());
        }
        model.addAttribute("listaItems", lista);
        model.addAttribute("listaTotal", totalCarritos);
        model.addAttribute("carritoTotal", carritoTotalVenta);
        return new ModelAndView("/carrito/fragmentos :: verCarrito");
    }

    //Para ver el carrito
    @GetMapping("/carrito/listado")
    public String inicio(Model model) {
        var items = itemService.gets();
        model.addAttribute("items", items);
        var carritoTotalVenta = 0;
        for (Item i : items) {
            carritoTotalVenta += (i.getCantidad() * i.getPrecio());
        }
        model.addAttribute("carritoTotal",
                carritoTotalVenta);
        return "/carrito/listado";
    }

    //Para mofificar un producto del carrito
    @GetMapping("/carrito/modificar/{idProducto}")
    public String modificarItem(Item item, Model model) {
        item = itemService.get(item);
        model.addAttribute("item", item);
        return "/carrito/modifica";
    }

    //Para eliminar un elemento del carrito
    @GetMapping("/carrito/eliminar/{idProducto}")
    public String eliminarItem(Item item) {
        itemService.delete(item);
        return "redirect:/carrito/listado";
    }

    //Para actualizar un producto del carrito (cantidad)
    @PostMapping("/carrito/guardar")
    public String guardarItem(Item item) {
        itemService.actualiza(item);
        return "redirect:/carrito/listado";
    }

    //Para facturar los productos del carrito... no implementado...
    @GetMapping("/facturar/carrito")
    public String facturarCarrito() {
        itemService.facturar();
        return "redirect:/";
    }
    
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private FacturaDao facturaDao;
    @Autowired
    private VentaDao ventaDao;
    @Autowired
    private ProductoDao productoDao;
    
    @Override
    public void facturar() {
        System.out.println("Facturando");

        //Se obtiene el usuario autenticado
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

        if (username.isBlank()) {
            return;
        }

        Usuario usuario = usuarioService.getUsuarioPorUsername(username);

        if (usuario == null) {
            return;
        }

        Factura factura = new Factura(usuario.getIdUsuario());
        factura = facturaDao.save(factura);

        double total = 0;
        for (Item i : listaItems) {
            System.out.println("Producto: " + i.getDescripcion()
                    + " Cantidad: " + i.getCantidad()
                    + " Total: " + i.getPrecio() * i.getCantidad());
            Venta venta = new Venta(factura.getIdFactura(), i.getIdProducto(), i.getPrecio(), i.getCantidad());
            ventaDao.save(venta);
            Producto producto = productoDao.getReferenceById(i.getIdProducto());
            producto.setExistencias(producto.getExistencias() - i.getCantidad());
            productoDao.save(producto);
            total += i.getPrecio() * i.getCantidad();
        }
        factura.setTotal(total);
        facturaDao.save(factura);
        listaItems.clear();
    }
}
