package ec.edu.ec.controlAsistencia.jsf.session.licencias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import ec.edu.ec.controlAsistencia.ejb.servicios.impl.LicenciaPermiso;
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
import ec.edu.uce.controlAsistencia.jpa.entidades.LicenciaYPermiso;
import ec.edu.uce.controlAsistencia.jpa.entidades.Puesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;

@ManagedBean(name="registrosLicencias")
@SessionScoped
public  class RegistrosLicencias  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<LicenciaYPermiso> listaLicencias = new ArrayList<>();
	private LicenciaYPermiso seleccionLicencia;
	
	private DetallePuestoDto detallePuestoEmpleado;
	private FichaEmpleado fichaEmpleado;
	private Dependencia dependencia;
	private Puesto puesto= null;
	private Regimen regimen=null;
	
	
	@ManagedProperty(value="#{busquedaEmpleado.seleccionPersona}")
	private PersonaDto seleccionPersona;
	
	
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
	
	@EJB
	private LicenciaPermiso srvLicencia;
	
	                      
	
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



	public List<LicenciaYPermiso> getListaLicencias() {
		if(seleccionPersona != null)
			listaLicencias = srvLicencia.ListaLicenciaYPermisoPorDetallePuestoId(seleccionPersona.getDtpsId());
		return listaLicencias;
	}



	public void setListaLicencias(List<LicenciaYPermiso> listaLicencias) {
		this.listaLicencias = listaLicencias;
	}



	public LicenciaYPermiso getSeleccionLicencia() {
		return seleccionLicencia;
	}



	public void setSeleccionLicencia(LicenciaYPermiso seleccionLicencia) {
		this.seleccionLicencia = seleccionLicencia;
	}



	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}

	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}

	
	public LicenciaPermiso getSrvLicencia() {
		return srvLicencia;
	}



	public void setSrvLicencia(LicenciaPermiso srvLicencia) {
		this.srvLicencia = srvLicencia;
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
	 

