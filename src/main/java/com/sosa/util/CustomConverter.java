package com.sosa.util;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;

import com.sosa.model.CatalogoMovimiento;
import com.sosa.model.CatalogoOperacion;
import com.sosa.model.Movimiento;
import com.sosa.model.Prestamo;
import com.sosa.model.dto.MovimientoDTO;

public class CustomConverter {
	
	public static Converter<Long, CatalogoMovimiento> catalogoMovimientoConverter = new Converter<Long, CatalogoMovimiento>() {
        public CatalogoMovimiento convert(MappingContext<Long, CatalogoMovimiento> context) {
        	CatalogoMovimiento catalogoMovimiento = new CatalogoMovimiento();
        	catalogoMovimiento.setIdCatalogoMovimiento(context.getSource());
            return catalogoMovimiento;
        }
    };
    
    public static Converter<Long, CatalogoOperacion> catalogoOperacionConverter = new Converter<Long, CatalogoOperacion>() {
        public CatalogoOperacion convert(MappingContext<Long, CatalogoOperacion> context) {
        	CatalogoOperacion catalogoOperacion = new CatalogoOperacion();
        	catalogoOperacion.setIdCatalogoOperacion(context.getSource());
            return catalogoOperacion;
        }
    };

    public static Converter<String, Prestamo> prestamoConverter = new Converter<String, Prestamo>() {
        public Prestamo convert(MappingContext<String, Prestamo> context) {
        	Prestamo prestamo = new Prestamo();
        	prestamo.setIdPrestamo(context.getSource());
            return prestamo;
        }
    };
    
    public static PropertyMap<MovimientoDTO, Movimiento> movimientoMap = new PropertyMap<MovimientoDTO, Movimiento>() {
	    protected void configure() {
	        //using(catalogoMovimientoConverter).map(source.getIdCatMovimiento()).setCatalogoMovimiento(null);
	        //using(catalogoOperacionConverter).map(source.getIdCatOperacion()).setCatalogoOperacion(null);
	        using(prestamoConverter).map(source.getIdPrestamo()).setPrestamo(null);
	    }
    };
    
    public static Converter<CatalogoMovimiento, Long> catalogoMovimientoToLongConverter = new Converter<CatalogoMovimiento, Long>() {
        public Long convert(MappingContext<CatalogoMovimiento, Long> context) {
            return context.getSource().getIdCatalogoMovimiento();
        }
    };
    
    public static Converter<CatalogoOperacion, Long> catalogoOperacionToLongConverter = new Converter<CatalogoOperacion, Long>() {
        public Long convert(MappingContext<CatalogoOperacion, Long> context) {
        	return context.getSource().getIdCatalogoOperacion();
        }
    };

    public static Converter<Prestamo, String> prestamoToStringConverter = new Converter<Prestamo, String>() {
        public String convert(MappingContext<Prestamo, String> context) {
        	return context.getSource().getIdPrestamo();
        }
    };
    
    public static PropertyMap<Movimiento, MovimientoDTO> movimientoDTOMap = new PropertyMap<Movimiento, MovimientoDTO>() {
	    protected void configure() {
	        //using(catalogoMovimientoToLongConverter).map(source.getCatalogoMovimiento()).setIdCatMovimiento(null);
	        //using(catalogoOperacionToLongConverter).map(source.getCatalogoOperacion()).setIdCatOperacion(null);
	        using(prestamoToStringConverter).map(source.getPrestamo()).setIdPrestamo(null);
	    }
    };
}