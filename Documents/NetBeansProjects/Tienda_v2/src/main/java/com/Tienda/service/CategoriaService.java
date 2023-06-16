package com.Tienda.service;

import com.Tienda.domain.Categoria;
import java.util.List;

public interface CategoriaService {
    //se declara un metodo para obtener un arraylist de objetos categoria
    //los objetos vienes de la tabla categoria
    //son todos los registros o solo los activos
    public List<Categoria> getCategorias(boolean activos);

    //abajo se colocaran los metodos para un Crud de categorias pero en semana 6
}
