package com.Tienda.service.impl;

import com.Tienda.dao.CategoriaDao;
import com.Tienda.domain.Categoria;
import com.Tienda.service.CategoriaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoriaServiceImpl implements CategoriaService{

    //La anotacion autowired crea un unico objeto mientras se ejecuta el app
    @Autowired
    private CategoriaDao categoriaDao;
    
    @Override
    public List<Categoria> getCategorias(boolean activos) {
        var lista=categoriaDao.findAll();
        
        if(activos){ //se deben eliminar los que no estan activos
            lista.removeIf(e -> !e.isActivo());
        }
        
        return lista;
    }
    
}
