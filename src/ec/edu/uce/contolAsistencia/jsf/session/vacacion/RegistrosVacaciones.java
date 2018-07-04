package ec.edu.uce.contolAsistencia.jsf.session.vacacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ec.edu.uce.controlAsistencia.ejb.datos.DetallePuestoDto;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.FichaEmpleadoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.GrupoOcupacionalServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.FichaEmpleado;
import ec.edu.uce.controlAsistencia.jpa.entidades.GrupoOcupacional;
import ec.edu.uce.controlAsistencia.jpa.entidades.Puesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;

@ManagedBean(name="registrosVacacion")
@SessionScoped
public  class RegistrosVacaciones  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  List<Vacacion>   listaVacacion = new ArrayList<>();
	private Vacacion seleccionVacacion;
	private List<SaldoVacacion> saldoVacacion  = null; 
	private SaldoVacacion saldoVacacion1;
	private SaldoVacacion saldoVacacion2;
	private DetallePuestoDto detallePuestoEmpleado;
	private FichaEmpleado fichaEmpleado;
	private Dependencia dependencia;
	private Puesto puesto= null;
	private Regimen regimen=null;
	
	
	@ManagedProperty(value="#{busquedaEmpleado.seleccionPersona}")
	private PersonaDto seleccionPersona;
	
	@EJB
	private VacacionServicio  srvVacacion;  
	
	@EJB
	private DetallePuestoServicio  srvDetallePuesto; 
	
	@EJB
	private FichaEmpleadoServicio srvFichaEmpleado;
	
	@EJB
	private DependenciaServicio srvDependencia;
	
	@EJB
	private RegimenServicio srvRegimen;
	
	
	@EJB
	private PuestoServicio srvPuesto;
	
	                      
	
	@PostConstruct 
	public void  init(){
	}
	
	

	public Dependencia getDependencia() {
		if(dependencia==null){
			dependencia=srvDependencia.ObtenerPorId(seleccionPersona.getDpnId());
		}
		return dependencia;
	}

	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}



	public DetallePuestoDto getDetallePuestoEmpleado() {
		if(detallePuestoEmpleado==null){
		detallePuestoEmpleado =srvDetallePuesto.BuscarPorId(seleccionPersona.getDtpsId());
		if(detallePuestoEmpleado==null){
			System.out.println(" salio nulo");
		}
		}
		
		return detallePuestoEmpleado;
  	}

	public void setDetallePuestoEmpleado(DetallePuestoDto detallePuestoEmpleado) {
		this.detallePuestoEmpleado = detallePuestoEmpleado;
	}



	public FichaEmpleado getFichaEmpleado() {
		fichaEmpleado= srvFichaEmpleado.BuscarPorid(seleccionPersona.getFcemId());
		return fichaEmpleado;
	}



	public void setFichaEmpleado(FichaEmpleado fichaEmpleado) {
		this.fichaEmpleado = fichaEmpleado;
	}



	public List<Vacacion> getListaVacacion() {
		if(seleccionPersona !=null ){
			listaVacacion=srvVacacion.ListaVacacionesPorDetallePuestoId(seleccionPersona.getDtpsId());	
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
			//saldoVacacion=srvVacacion.listSaldoVacacionPorDetallePuestoId(seleccionDetall.getDtpsId());
		}
		return saldoVacacion;
	}

	public void setSaldoVacacion(List<SaldoVacacion> saldoVacacion) {
		this.saldoVacacion = saldoVacacion;
	}

	

	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}

	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}

	public VacacionServicio getSrvVacacion() {
		return srvVacacion;
	}

	public void setSrvVacacion(VacacionServicio srvVacacion) {
		this.srvVacacion = srvVacacion;
	}

	public SaldoVacacion getSaldoVacacion1() {
		saldoVacacion1=srvVacacion.ObtenerSaldoVacacionPorPeriodo(1, seleccionPersona.getDtpsId() );
		return saldoVacacion1;
	}

	public void setSaldoVacacion1(SaldoVacacion saldoVacacion1) {
		
		this.saldoVacacion1 = saldoVacacion1;
	}

	public SaldoVacacion getSaldoVacacion2() {
		System.out.println("seleccion Persona" + seleccionPersona.getDtpsId());
 		saldoVacacion2=srvVacacion.ObtenerSaldoVacacionPorPeriodo(2,seleccionPersona.getDtpsId());  
		return saldoVacacion2;
	}

	public void setSaldoVacacion2(SaldoVacacion saldoVacacion2) {
		this.saldoVacacion2 = saldoVacacion2;
	}



	





	public Puesto getPuesto() {
		if(puesto==null){
		
			puesto=srvPuesto.BuscarPorId(seleccionPersona.getPstId());
	
		}
		return puesto;
	}

	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}



	public Regimen getRegimen() {
		
		if(regimen ==null){
			regimen=srvRegimen.BuscarPorId(seleccionPersona.getRgmId());
		}
		return regimen;
	}



	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}

	
	
     
	/**
	 * Region Metodos
	 */
	
	
	
	 }
	 

