package com.Tienda.controller;

import com.Tienda.service.ReporteService;
import com.tienda.domain.Usuario;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/reportes")
public class ReporteController {
    @Autowired
    ReporteService reporteService;
    
    @GetMapping("/principal")
    public String principal(Model model) {
        return "/reportes/principal";
    }
    @GetMapping("/usuarios")
    public ResponseEntity<Resource> usuarios(@RequestParam String tipo) throws IOException {
        return reporteService.generaReporte(
                "usuarios",
                null, 
                tipo);
    }
    @GetMapping("/ventas")
    public ResponseEntity<Resource> ventas(@RequestParam String tipo) throws IOException {
        return reporteService.generaReporte(
                "ventas",
                null, 
                tipo);
    }
    @GetMapping("/Producto")
    public ResponseEntity<Resource> Producto(@RequestParam String tipo) throws IOException {
        return reporteService.generaReporte(
                "Producto",
                null, 
                tipo);
    }
    @GetMapping("/categoria")
    public ResponseEntity<Resource> categoria(@RequestParam String tipo) throws IOException {
        return reporteService.generaReporte(
                "categoria",
                null, 
                tipo);
    }
}
