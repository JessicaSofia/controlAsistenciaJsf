package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import javax.resource.spi.ValidatingManagedConnectionFactory;

import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import ec.edu.ec.controlAsistencia.ejb.servicios.impl.DetallePuestoServicioImpl;
import ec.edu.ec.controlAsistencia.ejb.servicios.impl.SancionesServicioImpl;
import ec.edu.ec.controlAsistencia.ejb.servicios.impl.VacacionServicioImpl;
import ec.edu.uce.controlAsistencia.ejb.datos.Estados;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.SancionesServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;
import net.sf.jasperreports.crosstabs.CrosstabDeepVisitor;

class TestVacaiones {

    VacacionServicio srvVacacion;
    DetallePuestoServicio srvdet;
    Vacacion vac;
    DetallePuesto det;
    
    @Before
    public void Start() {
    	srvVacacion= new VacacionServicioImpl();
    	srvdet=new DetallePuestoServicioImpl();
    	det=srvdet.DetallePuestoBuscarPorId(166);
    	vac=new Vacacion();
    	vac.setDetallePuesto(det);
    	vac.setVccEstado(Estados.Activo.getId());
    	vac.setVccNumAutorizacion(12);
    	vac.setVccNumDias(5);
    	vac.setVccFechaFin(Calendar.getInstance().getTime());
    	vac.setVccFechaInicio(Calendar.getInstance().getTime());
    	
    }
    
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

//	@Test
//	void testInsercionVacacion() {
//		srvVacacion= new VacacionServicioImpl();
//    	srvdet=new DetallePuestoServicioImpl();
//    	det=srvdet.DetallePuestoBuscarPorId(166); // usuario Existente
//    	vac=new Vacacion();
//    	vac.setDetallePuesto(det);
//    	vac.setVccEstado(Estados.Activo.getId());
//    	vac.setVccNumAutorizacion(12);
//    	vac.setVccNumDias(5);
//    	vac.setVccFechaFin(Calendar.getInstance().getTime());
//    	vac.setVccFechaInicio(Calendar.getInstance().getTime());
//		boolean retorno =srvVacacion.VacionInsertar(vac);
//		assertEquals(true, retorno);
//		
//	}
	
	@Test
	void testNoInsercionVacacion() {
		srvVacacion= new VacacionServicioImpl();
    	srvdet=new DetallePuestoServicioImpl();
    	det=srvdet.DetallePuestoBuscarPorId(0); // usuario No existente
    	vac=new Vacacion();
    	vac.setDetallePuesto(det);
    	vac.setVccEstado(Estados.Activo.getId());
    	vac.setVccNumAutorizacion(12);
    	vac.setVccNumDias(5);
    	vac.setVccFechaFin(Calendar.getInstance().getTime());
    	vac.setVccFechaInicio(Calendar.getInstance().getTime());
		boolean retorno =srvVacacion.VacionInsertar(vac);
		assertEquals(false, retorno);
		
	}
	

}
