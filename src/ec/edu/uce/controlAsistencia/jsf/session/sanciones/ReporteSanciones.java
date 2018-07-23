package ec.edu.uce.controlAsistencia.jsf.session.sanciones;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.LicenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PermisoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.SancionesServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuestoSancion;

import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.TipoSancion;

@ManagedBean(name = "reporteSanciones")
@SessionScoped
public class ReporteSanciones implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int anio;
	private int mes;
	private List<DetallePuestoSancion> lstDtSanciones= new ArrayList<>();
	private  Date fecha;
	private Date fechaFin;
	private  Map<String, String> tpSanciones;
	private  Map<String, String> regimens;
	private String regimen="0";
	private String tpSacion="0";
	private List<TipoSancion>  lstTipoSancion;
	private List<Regimen> lstRegimen;
	private boolean desgloce=false;
	private List<String> dependencia=null;
	private String tipoReporte="1";
	private  boolean AnualActv=false;
	
	
	 
	
	
	@PostConstruct
	public void init() {
		
		this.lstTipoSancion= srvSancion.ObtenerLstTipoSancionTodo();
		this.tpSanciones = new LinkedHashMap<>();
		this.lstTipoSancion.forEach((tipoSancionEach) -> {
			tpSanciones.put(tipoSancionEach.getTpsnNomBre(), String.valueOf(tipoSancionEach.getTpsnId()));
		});
	
		this.lstRegimen= srvRegimen.ObtenerLstTodos();
		this.regimens = new LinkedHashMap<>();
		this.lstRegimen.forEach((regimenEach) -> {
			regimens.put(regimenEach.getRgmDescripcion(), String.valueOf(regimenEach.getRgmId() ));
		});
	
	}
		
		
	
	
	@EJB
	private VacacionServicio srvVacacion;
	
	@EJB
	private DependenciaServicio srvDependencia;
	
	@EJB
	private RegimenServicio srvRegimen;
	
	@EJB
	private LicenciaServicio srvLicencia;
	
	@EJB
	private PermisoServicio srvPermiso;
	
	@EJB
	private  SancionesServicio srvSancion;
	
	
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
	
	
	public Date getFecha() {
		
		return fecha;
	}
	public void setFecha(Date fecha) {

		this.fecha = fecha;
	}
	
	public List<String> getDependencia() {
		if(dependencia==null) {
			List<Dependencia> lstDependecia=srvDependencia.obtenerListTodo();
			for(Dependencia d: lstDependecia) {
			
				dependencia.add(d.getDpnDescripcion());
			}
		}
		return dependencia;
	}
	public void setDependencia(List<String> dependencia) {
		this.dependencia = dependencia;
	}
	
	public List<DetallePuestoSancion> getLstDtSanciones() {
		//lstDtSanciones=srvSancion.listarDtSancionTodos();
		return lstDtSanciones;
	}
	public void setLstDtSanciones(List<DetallePuestoSancion> lstDtSanciones) {
		this.lstDtSanciones = lstDtSanciones;
	}
	public Map<String, String> getTpSanciones() {
		return tpSanciones;
	}
	public void setTpSanciones(Map<String, String> tpSanciones) {
		this.tpSanciones = tpSanciones;
	}
	public Map<String, String> getRegimens() {
		return regimens;
	}
	public void setRegimens(Map<String, String> regimens) {
		this.regimens = regimens;
	}
	public String getRegimen() {
		return regimen;
	}
	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}
	public String getTpSacion() {
		return tpSacion;
	}
	public void setTpSacion(String tpSacion) {
		this.tpSacion = tpSacion;
	}
	public List<TipoSancion> getLstTipoSancion() {
		return lstTipoSancion;
	}
	public void setLstTipoSancion(List<TipoSancion> lstTipoSancion) {
		this.lstTipoSancion = lstTipoSancion;
	}
	public List<Regimen> getLstRegimen() {
		return lstRegimen;
	}
	public void setLstRegimen(List<Regimen> lstRegimen) {
		this.lstRegimen = lstRegimen;
	}
	
	
	
	public boolean isDesgloce() {
		return desgloce;
	}
	public void setDesgloce(boolean desgloce) {
		this.desgloce = desgloce;
	}
	
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	
	
	public String getTipoReporte() {
		return tipoReporte;
	}
	public void setTipoReporte(String tipoReporte) {
		this.tipoReporte = tipoReporte;
	}
	
	
	public boolean isAnualActv() {
		int valor=Integer.parseInt(tipoReporte);
		if(valor==1) {
			AnualActv=false;
			
		}else{
			if(valor==2) 
			AnualActv=true;
		}
			
		
		return AnualActv;
	}
	public void setAnualActv(boolean anualActv) {
		AnualActv = anualActv;
	}
	/**
	 * Metodos
	 */
	
	public void BuscarSancionesFiltros() {
		Calendar c=Calendar.getInstance();
		c.setTime(fecha);
		anio=c.get(Calendar.YEAR);
		mes = c.get(Calendar.MONTH)+1;
		int idTpSan=Integer.parseInt(tpSacion);
		int idReg= Integer.parseInt(regimen);
		lstDtSanciones= new ArrayList<>();
		if(idTpSan!=0) {
			if(idReg!=0) {
				lstDtSanciones=srvSancion.listarDtSancionPorAnioMesRegimenIdTipoSancionId(anio, mes, idReg, idTpSan);
			}else {
				lstDtSanciones=srvSancion.listarDtSancionPorAnioMesTipoSancionId(anio, mes,idTpSan);
			}
			
		}else {
			if(idReg!=0) {
				lstDtSanciones=srvSancion.listarDtSancionPorAnioMesRegimenId(anio, mes, idTpSan);
			}else {
				lstDtSanciones=srvSancion.listarDtSancionPorAnioMes(anio, mes);
			}	
	}	
}
	
	
}