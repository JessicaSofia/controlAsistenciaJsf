package ec.edu.uce.contolAsistencia.jsf.session.vacacion;


import java.io.Serializable;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.taglibs.standard.tag.common.fmt.SetTimeZoneSupport;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;

import ec.edu.uce.controlAsistencia.ejb.datos.Estados;
import ec.edu.uce.controlAsistencia.ejb.datos.ParametroVacacionesDto;
import ec.edu.uce.controlAsistencia.ejb.datos.ParametrosVacacion;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ContratoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ParametroVacacionesServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Contrato;
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
	private DetallePuesto detallePuesto;
	private Estados estado;
	
	
	private boolean esActualizacion=false;
	
	

	@ManagedProperty(value="#{busquedaEmpleado.seleccionPersona}")
	private PersonaDto seleccionPersona;
	private SaldoVacacion saldoVacacion1;
	private SaldoVacacion saldoVacacion2;  
	
	 
	@PostConstruct
	public void init(){
		
	}
	
	@EJB
	private VacacionServicio  srvVacacion; 
	
	@EJB
	private DetallePuestoServicio srvDetallePuesto;
	
	@EJB
	private ContratoServicio srvContrato;
	
	
	@EJB 
	private ParametroVacacionesServicio srvParamVacaciones;
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
	
	

	
	public Estados getEstado() {
		return estado;
	}
	public void setEstado(Estados estado) {
		this.estado = estado;
	}
	public DetallePuesto getDetallePuesto() {
		return detallePuesto;
	}
	public void setDetallePuesto(DetallePuesto detallePuesto) {
		this.detallePuesto = detallePuesto;
	}

	
	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}
	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
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
	 * @throws ParseException 
	 */
	 
	public void CalcularVacaciones(){
		if(vacacion.getVccNumDias()>0 && vacacion.getVccFechaInicio()!=null) {
			vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio(), vacacion.getVccNumDias()));
			CalcularSaldoVacacion(vacacion.getVccNumDias());
		}
		else {
			//implementacion de mensajes
		}
	
	}
	
	public void GuardarVacacion(){
		boolean retorno=false;
		detallePuesto= srvDetallePuesto.DetallePuestoBuscarPorId(seleccionPersona.getDtpsId());
		
		vacacion.setDetallePuesto(detallePuesto);
		if(esActualizacion) {
			Vacacion vac=srvVacacion.VacacionActualizar(vacacion);
			if(vac!=null) {
				retorno= true;
			}else{
				retorno =false;
			}
		}	
		else {
			vacacion.setVccEstado(Estados.Activo.getId());
			retorno =srvVacacion.VacionInsertar(vacacion);	
		}
		
			 
			 if(retorno) {
				 saldoVacacion1.setDetallePuesto(detallePuesto);
				 saldoVacacion2.setDetallePuesto(detallePuesto);
				 srvVacacion.ActualizarSaldoVacacion(saldoVacacion1);
				 srvVacacion.ActualizarSaldoVacacion(saldoVacacion2);
			  }else  {
				// no se inserto o no se actualizo 
			 }
			
		}
	                    
	public void EditarVacacion(){
		
	} 
	
	public Date calcularFechaFinal( Date fechaInicio , int numDias){
		Calendar fechaFinal = Calendar.getInstance();
			fechaFinal.setTime(fechaInicio);
			fechaFinal.add(Calendar.DAY_OF_YEAR, numDias-1); 	
		  
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
	
		int diasDisp=0;
		if(saldoVacacion1!=null && saldoVacacion2!=null) {
	
		int saldoDias1=saldoVacacion1.getSlvcDiasRestantes();
		int saldoDias2=saldoVacacion2.getSlvcDiasRestantes();
		String saldoHoras1=saldoVacacion1.getSlvcTotalHoras();
		
		int saldoTotaldias= saldoDias1-num;
		if(saldoTotaldias<0){
			saldoVacacion1.setSlvcDiasRestantes(0);
			saldoTotaldias= saldoTotaldias+saldoDias2;
			if(saldoTotaldias<0) {
				System.out.println("El Usuario no tiene disponible el numero de dias solicitadas");
				}
			else{
				saldoVacacion2.setSlvcDiasRestantes(saldoTotaldias);
				saldoVacacion2.setSlvcTotalHoras(saldoVacacion1.getSlvcTotalHoras());
				saldoVacacion1.setSlvcTotalHoras("00:00");
			
			}
	
		}   else {  
			saldoVacacion1.setSlvcDiasRestantes(saldoTotaldias);
		}
		}
		
	}
	
	
	public void anularVacacion() {
		
		boolean retorno = false;
		vacacion.setVccEstado(Estados.Anulado.getId());
		vacacion.getVccObservacionEstado();
		srvVacacion.VacacionActualizar(vacacion);
		
	} 
	
	public String regresar() {
		String ruta="/controlAsistencia/vacaciones/VacacionesRegistros.xhtml";
		//limpiar();
		return ruta;
	}
	
	public void limpiar() {
		
		vacacion=null;
		esActualizacion=false;
	}
	
	
	
	public void cargarVariables(Vacacion seleccionVacacion ) {
		
		if(seleccionVacacion==null){
			esActualizacion=false;
			vacacion=new Vacacion();
			vacacion.setVccNumAutorizacion(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			vacacion.setVccFechaEmision(fechaEmision);
			
		}	else {
			esActualizacion=true;
			vacacion=seleccionVacacion;
		}
		
		saldoVacacion1=srvVacacion.ObtenerSaldoVacacionPorPeriodo(1, seleccionPersona.getDtpsId());
		saldoVacacion2=srvVacacion.ObtenerSaldoVacacionPorPeriodo(2, seleccionPersona.getDtpsId());
		if(saldoVacacion1==null && saldoVacacion2==null) {
			saldoVacacion1= new SaldoVacacion();
			saldoVacacion1.setSlvcDiasAnticipados(0);
			saldoVacacion1.setSlvcDiasRegistrados(0);
			saldoVacacion1.setSlvcDiasRestantes(0);
			saldoVacacion1.setSlvcEstado(Estados.Activo.getId());
			saldoVacacion1.setSlvcPeriodo(1);
			saldoVacacion1.setSlvcTotalDias(calcularDiasDisponibles());
			saldoVacacion1.setSlvcTotalHoras("00:00");
			// comentario
	}
	
		
		
		
	}
	
	
public int  calcularDiasDisponibles() {
int diasDisp=0;
Calendar fechActual = Calendar.getInstance();
Calendar fechaContrato=Calendar.getInstance();

Contrato contrato=srvContrato.obtenerporDetallePuestoId(seleccionPersona.getDtpsId());
if(contrato!=null) {
	GregorianCalendar cal = new GregorianCalendar();
	fechaContrato.setTime(contrato.getCntFechaInicio()); 
	int anoAct=fechActual.get(Calendar.YEAR);
	int mesAct=fechActual.get(Calendar.MONTH);
	int diaAct=fechActual.get(Calendar.DAY_OF_MONTH);
	
	int anoCont=fechaContrato.get(Calendar.YEAR);
	int mesCont=fechaContrato.get(Calendar.MONTH);
	int  diaCont=fechaContrato.get(Calendar.DAY_OF_MONTH);
	

         int[] monthDay = { 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
         int Anios = anoAct- anoCont;
         int Meses;
         int Dias;

          fechaContrato.add(Calendar.YEAR, Anios);
         if (fechActual.compareTo(fechaContrato) < 0)
             Anios--;

         int increment = 0;
         if (diaCont > diaAct)
             increment = monthDay[mesCont];

         if (increment == -1)
         {
             increment = (short)(cal.isLeapYear(anoCont) ? 29 : 28);
         }

         if (increment != 0)
         {
             Dias = (diaAct + increment) - diaCont;
             increment = 1;
         }
         else
             Dias = diaAct - diaCont;

         if ((mesCont + increment) > mesAct)
             Meses = (mesAct + 12) - (mesCont + increment);
         else
             Meses = (mesAct) - (mesCont + increment);
	

if(Anios==0) {
	System.out.println("Años "+ Anios + "  Meses "  +Meses+  " Dias "+Dias);
	
	String valorNumDias=srvParamVacaciones.buscarPorId(ParametrosVacacion.NumDiasxAño.getId(), seleccionPersona.getRgmId()).getPrvcrgValor();
	int diasReg=Integer.parseInt(valorNumDias);
	int diasxmes=diasReg /12;
	diasDisp=diasxmes*Meses;
	
	
}

else {
	
	// No existe fecha de contracion;
	
}
}
return diasDisp;

	
}
}

