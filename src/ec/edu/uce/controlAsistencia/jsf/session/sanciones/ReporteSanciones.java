package ec.edu.uce.controlAsistencia.jsf.session.sanciones;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ec.edu.uce.controlAsistencia.ejb.datos.ReporteSancion;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.LicenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PermisoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.SancionesServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuestoSancion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Falta;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.Sancion;
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
	private int anioFin;
	private int mesFin;
	private List<ReporteSancion> lstDtSanciones= new ArrayList<>();
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
	private List<Falta> lstFaltas;
	private List<String> faltas;
	private int idTpSan=0;
	private int idReg= 0;
	private List<ReporteSancion> lstDtSancionesDesglosado  =new ArrayList<>();
	private Map <Integer, String> meses;
	private Map <String, Integer> mesesNombre;
	private List<String> mesesBusqueda= new ArrayList<>();
	private List<ReporteSancion> lstDtSancionesAnual= new ArrayList<>();
	
	 
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
		
		this.lstFaltas=srvSancion.listarFalta();
		this.faltas=new ArrayList<>();
		this.lstFaltas.forEach((fal)->{
			faltas.add(fal.getFlNombre());
		});
		
		this.meses= new HashMap<Integer, String>();
		
		this.meses.put(1,"Enere");
		this.meses.put(2,"Febrero");
		this.meses.put(3,"Marzo");
		this.meses.put(4,"Abril");
		this.meses.put(5,"Mayo");
		this.meses.put(6,"Junio");
		this.meses.put(7,"Julio");
		this.meses.put(8,"Agosto");
		this.meses.put(9,"Septiembre");
		this.meses.put(10,"Octubre");
		this.meses.put(11,"Noviembre");
		this.meses.put(12,"Diciembre");
		
this.mesesNombre= new HashMap<String, Integer>();
		
		this.mesesNombre.put("Enere",1);
		this.mesesNombre.put("Febrero",2);
		this.mesesNombre.put("Marzo",3);
		this.mesesNombre.put("Abril",4);
		this.mesesNombre.put("Mayo",5);
		this.mesesNombre.put("Junio",6);
		this.mesesNombre.put("Julio",7);
		this.mesesNombre.put("Agosto",8);
		this.mesesNombre.put("Septiembre",9);
		this.mesesNombre.put("Octubre",10);
		this.mesesNombre.put("Noviembre",11);
		this.mesesNombre.put("Diciembre",12);
	
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
	
	public List<ReporteSancion> getLstDtSanciones() {
		//lstDtSanciones=srvSancion.listarDtSancionTodos();
		return lstDtSanciones;
	}
	public void setLstDtSanciones(List<ReporteSancion> lstDtSanciones) {
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
	
	public List<Falta> getLstFaltas() {
		return lstFaltas;
	}
	public void setLstFaltas(List<Falta> lstFaltas) {
		this.lstFaltas = lstFaltas;
	}
	public List<String> getFaltas() {
		return faltas;
	}
	public void setFaltas(List<String> faltas) {
		this.faltas = faltas;
	}
	
	public int getIdTpSan() {
		return idTpSan;
	}
	public void setIdTpSan(int idTpSan) {
		this.idTpSan = idTpSan;
	}
	public int getIdReg() {
		return idReg;
	}
	public void setIdReg(int idReg) {
		this.idReg = idReg;
	}
	
	public int getAnioFin() {
		return anioFin;
	}
	public void setAnioFin(int anioFin) {
		this.anioFin = anioFin;
	}
	public int getMesFin() {
		return mesFin;
	}
	public void setMesFin(int mesFin) {
		this.mesFin = mesFin;
	}
	
	public List<ReporteSancion> getLstDtSancionesDesglosado() {
		
		lstDtSancionesDesglosado=srvSancion.listarDtSancionPorAnioMesRegimenIdTipoSancionIdNoAgrupado(anio, mes, idReg, idTpSan);
		return lstDtSancionesDesglosado;
	}
	public void setLstDtSancionesDesglosado(List<ReporteSancion> lstDtSancionesDesglosado) {
		this.lstDtSancionesDesglosado = lstDtSancionesDesglosado;
	}
	
	public Map< Integer, String> getMeses() {
		return meses;
	}
	public void setMeses(Map<Integer, String> meses) {
		this.meses = meses;
	}
	public List<String> getMesesBusqueda() {
		return mesesBusqueda;
	}
	public void setMesesBusqueda(List<String> mesesBusqueda) {
		this.mesesBusqueda = mesesBusqueda;
	}
	
	
	public List<ReporteSancion> getLstDtSancionesAnual() {
		return lstDtSancionesAnual;
	}
	public void setLstDtSancionesAnual(List<ReporteSancion> lstDtSancionesAnual) {
		this.lstDtSancionesAnual = lstDtSancionesAnual;
	}
	/**
	 * Metodos
	 */
	
	public void BuscarSancionesFiltros() {
		Calendar c=Calendar.getInstance();
		Calendar cf=Calendar.getInstance();
		c.setTime(fecha);
		
		
		anio=c.get(Calendar.YEAR);
		mes = c.get(Calendar.MONTH)+1;
		anioFin= cf.get(Calendar.YEAR);
		mesFin= cf.get(Calendar.MONTH)+1;
		
		idTpSan=Integer.parseInt(tpSacion);
		idReg= Integer.parseInt(regimen);
		lstDtSanciones= new ArrayList<>();
		if(!AnualActv) {
		if(idTpSan!=0) {
			if(idReg!=0) {
				lstDtSanciones=srvSancion.listarDtSancionPorAnioMesRegimenIdTipoSancionId(anio, mes, idReg, idTpSan);
			}else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Debe seleccionar el Regimen."));
			}
			
		}else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Debe seleccionar El Tipo de Sancion"));
			
			
	}	
		}else {
			cf.setTime(fechaFin);
			
			
			anioFin= cf.get(Calendar.YEAR);
			mesFin= cf.get(Calendar.MONTH)+1;
			 
	        int numMeses= mesFin - mes;
	            
	        int mesinicio=mes;
	        mesesBusqueda.clear();
	            for(int i=0; i<=numMeses; i++) {
	            	
	            	mesesBusqueda.add(meses.get(mesinicio));
	            	mesinicio++;
	            }
	            
	            if(idTpSan!=0) {
	    			if(idReg!=0) {
	    				lstDtSancionesAnual=srvSancion. listarDtSancionAnualPorAnioMesRegimenIdTipoSancionId(anio,mes,idReg,idTpSan,anioFin,mesFin);
	    			}else {
	    				FacesContext.getCurrentInstance().addMessage(null,
	    						new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Debe seleccionar el Regimen."));
	    			}
	    			
	    		}else {
	    			FacesContext.getCurrentInstance().addMessage(null,
	    					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Debe seleccionar El Tipo de Sancion"));
	    			
	    			
	    	}	
		}
		
}
	
	
	
	public List<ReporteSancion> listarDesglosado(){
		List<ReporteSancion> list= new ArrayList<>();
		ReporteSancion repDesglo=null;
		if(lstDtSanciones!=null) {
			for(ReporteSancion r:lstDtSanciones) {
				repDesglo= new ReporteSancion(r.getDtpsId(),r.getIdentificacion(), r.getNombres(), r.getValor(), r.getCargo(), r.getDependencia(),null);
			list.add(repDesglo);
			}
		}
		
		
		
		return list;
		
	}
	
	public String  obtenerDiasPorFaltasPorDetallepuestoId(int dtpsId, String falta) {
		String retorno= "";
		 Falta ft=srvSancion.ObtenerFaltaPorNombre(falta);
		
		retorno= srvSancion.DiasSancionPorTpSancionIdFaltaId(idTpSan, ft.getFlId(),dtpsId, anio, mes);
		
		return retorno;
		
	}
	
	
	public List<String> obternerFaltasPorMes(String  mes, int dtpsId, int anio){
		List<String> retorno=null;

			int m=mesesNombre.get(mes);
			int sn= Integer.parseInt(tpSacion);
			Sancion san= srvSancion.ObtenerSancionPorId(sn);
			retorno=srvSancion.listarfaltasPorDtPuestoIdMesAnioTipoSancion(dtpsId, m, anio, san.getTipoSancion().getTpsnId());
		
		return retorno;
	}
	
}