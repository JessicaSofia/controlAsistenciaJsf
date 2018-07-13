package ec.edu.uce.contolAsistencia.jsf.session.licencias;

import java.io.Serializable;
import java.sql.Timestamp;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;

import ec.edu.uce.controlAsistencia.ejb.datos.Estados;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.LicenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.TipoLicenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Licencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.TipoLicencia;

@ManagedBean(name = "licenciaForm")
@SessionScoped
public class LicenciaForm implements Serializable {

	/**
	 * Variables
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * private Vacacion vacacion; /*
	 */

	private DetallePuesto detallePuesto;
	private Licencia licencia;
	private boolean esActualizacion = false;
	private List<TipoLicencia> listaTipoLicencia = new ArrayList<>();
	private String tipoLicencia;
	private Map<String, String> tiposLicencias;
	private Estados estado;
	private TipoLicencia tipoLicenciaEntidad;
	private boolean panelLicencia = false;
	private boolean panelPermiso = false;
	private boolean btnConCargoVacaciones = false;
	private boolean tabEditDias = false;
	private boolean tabEditHora = false;

	@ManagedProperty(value = "#{busEmpleado.seleccionPersona}")
	private PersonaDto seleccionPersona;
	/*
	 * @ManagedProperty(value="#{registrosVacacion.saldoVacacion1}") private
	 * SaldoVacacion saldoVacacion1;
	 * 
	 * @ManagedProperty(value="#{registrosVacacion.saldoVacacion2}") private
	 * SaldoVacacion saldoVacacion2;
	 */

	/**
	 * SERVICIOS
	 */

	@EJB private VacacionServicio srvVacacion;
	

	@EJB
	private DetallePuestoServicio srvDetallePuesto;

	@EJB
	private LicenciaServicio srvlicencia;

	@EJB
	private TipoLicenciaServicio srvTipoLicencia;

	/**
	 * ============= Métodos==============
	 */

	@PostConstruct
	public void init() {
		this.listaTipoLicencia = srvTipoLicencia.listarTipoLicencia();
		this.tiposLicencias = new LinkedHashMap<>();
		this.listaTipoLicencia.forEach((tipoLicenciaEach) -> {
			tiposLicencias.put(tipoLicenciaEach.getTplcNombre(), String.valueOf(tipoLicenciaEach.getTplcId()));
		});

	}

	/**
	 * Metodo paara generar el Numero De Autorizacion
	 */
	public int generarNumAutorizacion() {
		int numAutorizacion = 0;
		if (srvlicencia.MaximaNumAutorizacion() != 0) {
			numAutorizacion = srvlicencia.MaximaNumAutorizacion() + 1;
		} else {
			numAutorizacion = 1;
		}
		/*
		 * if (srvVacacion.MaximaNumAutorizacion() != 0) { numAutorizacion =
		 * srvVacacion.MaximaNumAutorizacion() + 1; } else { numAutorizacion =
		 * 1; }
		 */
		return numAutorizacion;
	}

	public String regresar() {
		String ruta = "/controlAsistencia/vacaciones/VacacionesRegistros.xhtml";
		// limpiar();
		return ruta;
	}

	public void calcularPeriodoLicencia() {
		if (licencia.getLcnNumDias() > 0 && licencia.getLcnFechaInicio() != null) {
			licencia.setLcnFechaFin(
					calcularFechaFinal(licencia.getLcnFechaInicio(), licencia.getLcnNumDias()));
		}

		/*
		 * if(vacacion.getVccNumDias()>0 && vacacion.getVccFechaInicio()!=null)
		 * {
		 * vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio
		 * (), vacacion.getVccNumDias()));
		 * CalcularSaldoVacacion(vacacion.getVccNumDias()); } else {
		 * //implementacion de mensajes }
		 */

	}
	

	

	public Date calcularFechaFinal(Date fechaInicio, int numDias) {
		Calendar fechaFinal = Calendar.getInstance();
		fechaFinal.setTime(fechaInicio);
		fechaFinal.add(Calendar.DAY_OF_YEAR, numDias);

		return (Date) fechaFinal.getTime();
	}

	public void limpiar() {

		licencia = null;
		esActualizacion = false;
	}

	public void cargarVariables(Licencia seleccionLicencia) {
		if (seleccionLicencia == null) {
			esActualizacion = false;
			licencia = new Licencia();
			licencia.setLcnNumLicencia(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			licencia.setLcnFechaEmision(fechaEmision);
			this.tabEditDias = true;
			this.tabEditHora = true;

		} else {
			esActualizacion = true;
			licencia = seleccionLicencia;
			this.tipoLicencia = String.valueOf(this.licencia.getTipoLicencia().getTplcId()); 
			if(this.tipoLicencia.equals("2")){
				this.panelPermiso = true;
				this.panelLicencia = false;
				if(this.licencia.getLcnNumDias() != 0){
					this.tabEditHora = false;
					this.tabEditDias = true;
				}else{
					this.tabEditHora = true;
					this.tabEditDias = false;
				}
				
			}else{
				this.panelLicencia = true;
				this.panelPermiso = false;
			}
		}

	}

	public void guardadLicencia() {
		boolean retorno = false;

		detallePuesto = srvDetallePuesto.DetallePuestoBuscarPorId(seleccionPersona.getDtpsId());
		licencia.setDetallePuesto(detallePuesto);
		
		tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
		licencia.setTipoLicencia(tipoLicenciaEntidad);

		if (esActualizacion) {
			Licencia lp = srvlicencia.LicenciaActualizar(licencia);
			if (lp != null) {
				retorno = true;
			} else {
				retorno = false;
			}
		} else {
			licencia.setLcnEstado(Estados.Activo.getId());
			if(this.tipoLicenciaEntidad.getTplcId()==2){
				licencia.setLcnNumLicencia(0);
			}
			retorno = srvlicencia.LicenciaInsertar(licencia);
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "PrimeFaces Rocks."));
		}
	}
	
	
	/**
	 * =============Getters and Setters==============
	 */

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

	public Licencia getlicencia() {
		return licencia;
	}

	public void setlicencia(Licencia licencia) {
		this.licencia = licencia;
	}

	public List<TipoLicencia> getListaTipoLicencia() {
		return listaTipoLicencia;
	}

	public void setListaTipoLicencia(List<TipoLicencia> listaTipoLicencia) {
		this.listaTipoLicencia = listaTipoLicencia;
	}

	public String getTipoLicencia() {
		return tipoLicencia;
	}

	public void setTipoLicencia(String tipoLicencia) {
		this.tipoLicencia = tipoLicencia;
	}

	public Map<String, String> getTiposLicencias() {
		return tiposLicencias;
	}

	public void setTiposLicencias(Map<String, String> tiposLicencias) {
		this.tiposLicencias = tiposLicencias;
	}

	public boolean isPanelLicencia() {
		return panelLicencia;
	}

	public void setPanelLicencia(boolean panelLicencia) {
		this.panelLicencia = panelLicencia;
	}

	public boolean isPanelPermiso() {
		return panelPermiso;
	}

	public void setPanelPermiso(boolean panelPermiso) {
		this.panelPermiso = panelPermiso;
	}

	public boolean isBtnConCargoVacaciones() {
		return btnConCargoVacaciones;
	}

	public void setBtnConCargoVacaciones(boolean btnConCargoVacaciones) {
		this.btnConCargoVacaciones = btnConCargoVacaciones;
	}

	public boolean isTabEditDias() {
		return tabEditDias;
	}

	public void setTabEditDias(boolean tabEditDias) {
		this.tabEditDias = tabEditDias;
	}

	public boolean isTabEditHora() {
		return tabEditHora;
	}

	public void setTabEditHora(boolean tabEditHora) {
		this.tabEditHora = tabEditHora;
	}
	
	

}