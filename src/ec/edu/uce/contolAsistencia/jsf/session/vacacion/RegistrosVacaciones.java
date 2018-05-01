package ec.edu.uce.contolAsistencia.jsf.session.vacacion;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.print.attribute.standard.DateTimeAtCompleted;

import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;

@ManagedBean(name="registrosVacacion")
@SessionScoped
public  class RegistrosVacaciones  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  List<Vacacion>   listaVacacion;
	private Vacacion seleccionVacacion;
	private List<SaldoVacacion> saldoVacacion  = null;  
	
	@ManagedProperty(value="#{busquedaEmpleado.seleccionDetallePuesto}")
	private DetallePuesto seleccionDetallePuesto;
	
	@EJB
	private VacacionServicio  srvVacacion;

	public List<Vacacion> getListaVacacion() {
		if(seleccionDetallePuesto !=null ){
			listaVacacion=srvVacacion.ListaVacacionesPorDetallePuestoId(seleccionDetallePuesto.getDtpsId());			
		}
		return listaVacacion;
	}

	public void setListaVacacion(List<Vacacion> listaVacacion) {
		this.listaVacacion = listaVacacion;
	}

	public Vacacion getSeleccionVacacion() {
		
		return seleccionVacacion;
	}

	public void setSeleccionVacacion(Vacacion seleccionVacacion) {
		this.seleccionVacacion = seleccionVacacion;
	}

	

	public List<SaldoVacacion> getSaldoVacacion() {
		if(saldoVacacion==null){
			saldoVacacion=srvVacacion.listSaldoVacacionPorDetallePuestoId(seleccionDetallePuesto.getDtpsId());
		}
		return saldoVacacion;
	}

	public void setSaldoVacacion(List<SaldoVacacion> saldoVacacion) {
		this.saldoVacacion = saldoVacacion;
	}

	public DetallePuesto getSeleccionDetallePuesto() {
		return seleccionDetallePuesto;
	}

	public void setSeleccionDetallePuesto(DetallePuesto seleccionDetallePuesto) {
		this.seleccionDetallePuesto = seleccionDetallePuesto;
	}

	public VacacionServicio getSrvVacacion() {
		return srvVacacion;
	}

	public void setSrvVacacion(VacacionServicio srvVacacion) {
		this.srvVacacion = srvVacacion;
	}

	
	

	/**
	 * Region Metodos
	 */
	
	 public String saldoVacacionPorPeriodo(int periodo){
		 
		 String  TotalPeriodo="";
		 for(SaldoVacacion lsv: saldoVacacion){
			if(lsv.getSlvcPeriodo()== periodo){
				TotalPeriodo=lsv.toString();
				
			}
		 }
		return TotalPeriodo;
	 }
	 
	
	
	
	

}
