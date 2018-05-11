package ec.edu.uce.contolAsistencia.jsf.session.vacacion;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;

@ManagedBean(name="vacacionForm")
@SessionScoped
public class VacacionForm implements   Serializable{

	/**
	 * Variables
	 */
	private static final long serialVersionUID = 1L;
	private Vacacion  vacacion;
	private List<SaldoVacacion> saldoVacacion;
	private boolean esActualizacion;
	
	
	@ManagedProperty(value="#{registrosVacacion.seleccionVacacion}")
	private Vacacion seleccionVacacion;
	@ManagedProperty(value="#{busquedaEmpleado.seleccionDetallePuesto}")
	private DetallePuesto seleccionDetallePuesto;
	
	@PostConstruct
	public void init(){
		if(seleccionVacacion==null){

			vacacion=new Vacacion();
			vacacion.setVccNumAutorizacion(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			vacacion.setVccFechaEmision(fechaEmision);
			
		}	else {
			vacacion=seleccionVacacion;
		}
	}
	
	@EJB
	private VacacionServicio  srvVacacion;
	
	
	/**
	 * 
	 * =============Getters  and Setters==============
	 */
	public Vacacion getVacacion() {
		return vacacion;
	}
	public void setVacacion(Vacacion vacacion) {
		this.vacacion = vacacion;
	}
	public List<SaldoVacacion> getSaldoVacacion() {
		return saldoVacacion;
	}
	public void setSaldoVacacion(List<SaldoVacacion> saldoVacacion) {
		this.saldoVacacion = saldoVacacion;
	}
	public Vacacion getSeleccionVacacion() {
		return seleccionVacacion;
	}
	public void setSeleccionVacacion(Vacacion seleccionVacacion) {
		this.seleccionVacacion = seleccionVacacion;
	}
	public DetallePuesto getSeleccionDetallePuesto() {
		return seleccionDetallePuesto;
	}
	public void setSeleccionDetallePuesto(DetallePuesto seleccionDetallePuesto) {
		this.seleccionDetallePuesto = seleccionDetallePuesto;
	}	
	public boolean isEsActualizacion() {
		return esActualizacion;
	}
	public void setEsActualizacion(boolean esActualizacion) {
		this.esActualizacion = esActualizacion;
	}
	/**
	 * 
	 * ============= Métodos==============
	 */
	
	public void CalcularVacaciones(){
	vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio(), vacacion.getVccNumDias()));
	
	}
	
	public void GuardarVacacion(){
			srvVacacion.VacionInsertar(vacacion);
		}
	
	public void EditarVacacion(){
		
	} 
	
	public Date calcularFechaFinal( Date fechaInicio , int numDias){
		Calendar fechaFinal = Calendar.getInstance();
		
		if(fechaInicio!=null){
			fechaFinal.setTime(fechaInicio);
			fechaFinal.add(Calendar.DAY_OF_YEAR, numDias); 	
		}
		return (Date) fechaFinal.getTime();
		
		
	
	}
	/**
	 * Metodo paara  generar el Numero De Autorizacion
	 * @return
	 */
	public int generarNumAutorizacion(){
		int numAutorizacion=0;
		if(srvVacacion.MaximaNumAutorizacion()!=0){
			numAutorizacion=srvVacacion.MaximaNumAutorizacion()+1;
		}else{
			numAutorizacion=1;
		}
		return numAutorizacion;
	}
	 
	
	
	

}
