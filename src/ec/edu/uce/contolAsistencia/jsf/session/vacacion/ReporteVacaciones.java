package ec.edu.uce.contolAsistencia.jsf.session.vacacion;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;

@ManagedBean(name = "reporteVacaciones")
@SessionScoped
public class ReporteVacaciones implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int anio;
	private int mes;
	private List<Vacacion> lstVacacion  = new ArrayList<>();
	private  Date fecha;
	private  List<String> ausencias=null;
	private List<String> dependencia;
	private List<String> regimen;
	
	
	
	@PostConstruct
	public void init() {
		List<Dependencia> lstDependecia=srvDependencia.obtenerListTodo();
		for(Dependencia d: lstDependecia) {
			dependencia.add(d.getDpnDescripcion());
		}
		List<Regimen> lstRegimen=srvRegimen.ObtenerLstTodos();
		for(Regimen d: lstRegimen) {
			dependencia.add(d.getRgmDescripcion());
		}
		
		
	}
	
	@EJB
	private VacacionServicio srvVacacion;
	
	@EJB
	private DependenciaServicio srvDependencia;
	
	@EJB
	private RegimenServicio srvRegimen;
	
	
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	
	public List<Vacacion> getLstVacacion() {
		return lstVacacion;
	}
	public void setLstVacacion(List<Vacacion> lstVacacion) {
		this.lstVacacion = lstVacacion;
	}
	public Date getFecha() {
		
		return fecha;
	}
	public void setFecha(Date fecha) {

		this.fecha = fecha;
	}
	
	public List<String> getAusencias() {
		return ausencias;
	}
	public void setAusencias(List<String> ausencias) {
		this.ausencias = ausencias;
	}
	
	
	
	public List<String> getDependencia() {
		return dependencia;
	}
	public void setDependencia(List<String> dependencia) {
		this.dependencia = dependencia;
	}
	public List<String> getRegimen() {
		return regimen;
	}
	public void setRegimen(List<String> regimen) {
		this.regimen = regimen;
	}
	public void BuscarVacacionesFiltros() {
		Calendar c=Calendar.getInstance();
		c.setTime(fecha);
		anio=c.get(Calendar.YEAR);
		mes = c.get(Calendar.MONTH)+1;
		lstVacacion= new ArrayList<>();
		if(ausencias!=null) {
			if(ausencias.contains("vacacion")&&  ausencias.contains("permiso") && ausencias.contains("licencia")) {
				
			}else {
				if(ausencias.contains("vacacion")&&  ausencias.contains("permiso")) {
				
				
			}
				else {
					if(ausencias.contains("vacacion") && ausencias.contains("licencia")) {
					}
					else {
						if(ausencias.contains("permiso") && ausencias.contains("licencia")) {
							
						
					}else {
						if(ausencias.contains("vacacion")){
							lstVacacion=srvVacacion.ObtenerLstDtSancionesPorAnioMes(mes, anio);
						}
						if(ausencias.contains("permiso")){
							lstVacacion=srvVacacion.ObtenerLstDtSancionesPorAnioMes(mes, anio);
						}
						if(ausencias.contains("licencia")){
							lstVacacion=srvVacacion.ObtenerLstDtSancionesPorAnioMes(mes, anio);
						}
					}
					}
					
				}
			}
				
		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Escoja al menos un tipo de Ausencia para generar el Reporte,"));
		}
		
		
	}

	
}
