package ec.edu.uce.contolAsistencia.jsf.session.vacacion;


import java.io.Serializable;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
	
	
	
	private boolean esActualizacion;
	
	
	@ManagedProperty(value="#{registrosVacacion.seleccionVacacion}")
	private Vacacion seleccionVacacion;
	@ManagedProperty(value="#{busquedaEmpleado.seleccionDetallePuesto}")
	private DetallePuesto seleccionDetallePuesto;
	@ManagedProperty(value="#{registrosVacacion.saldoVacacion}")
	private List<SaldoVacacion> saldoVacacion;
	@ManagedProperty(value="#{registrosVacacion.seleccionVacacion}")
	private SaldoVacacion saldoVacacion1;
	@ManagedProperty(value="#{registrosVacacion.seleccionVacacion}")
	private SaldoVacacion saldoVacacion2;  
	
	 
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
	
	public SaldoVacacion getSaldoVacacion1() {
		return saldoVacacion1;
	}
	public void setSaldoVacacion1(SaldoVacacion saldoVacacion1) {
		this.saldoVacacion1 = saldoVacacion1;
	}
	public SaldoVacacion getSaldoVacacion2() {
		return saldoVacacion2;
	}
	public void setSaldoVacacion2(SaldoVacacion saldoVacacion2) {
		this.saldoVacacion2 = saldoVacacion2;
	}
	/**
	 * 
	 * ============= Métodos==============
	 */
	 
	public void CalcularVacaciones(){
	vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio(), vacacion.getVccNumDias()));
	generarNumAutorizacion();
	CalcularSaldoVacacion(vacacion.getVccNumDias());
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
	
	public void CalcularSaldoVacacion(int num){
		int saldoDias1=saldoVacacion1.getSlvcTotalDias();
		int saldoDias2=saldoVacacion2.getSlvcTotalDias();
		Date saldoHoras1=saldoVacacion1.getSlvcTotalHoras();
		Date saldoHoras2=saldoVacacion2.getSlvcTotalHoras();
		
		 SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	
		
		Calendar horaInicio = Calendar.getInstance();
		 horaInicio.set(Calendar.HOUR, 0);
		 horaInicio.set(Calendar.MINUTE, 0);
		 horaInicio.set(Calendar.SECOND, 0);
		 Date horaDefault=horaInicio.getTime();
		int saldoTotaldias1= saldoDias1-num;
		if(saldoTotaldias1<0){
			saldoVacacion1.setSlvcTotalDias(0);
		if(saldoHoras1==horaDefault){  
			saldoTotaldias1=  saldoTotaldias1+saldoDias2;
			saldoVacacion2.setSlvcTotalDias(saldoTotaldias1);
		}else{
			
			saldoVacacion1.setSlvcTotalDias(0);
			Calendar hora1 = Calendar.getInstance();
			hora1.setTime(saldoHoras1);
			int hora=hora1.get(Calendar.HOUR);
			int minutos=hora1.get(Calendar.MINUTE);
			
			System.out.println("IMPLEMENTCION INCOMPLETA  RESTAR HORAS CUANDO TIENE HORAS RESTANTES");
			if(minutos==0){
				
			}		
			
		}
		
		}else{
			saldoVacacion1.setSlvcTotalDias(saldoTotaldias1);
		}
						
	}
            
}