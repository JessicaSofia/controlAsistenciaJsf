package ec.edu.uce.contolAsistencia.jsf.session.licencias;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import ec.edu.uce.controlAsistencia.ejb.datos.DetallePuestoDto;
import ec.edu.uce.controlAsistencia.ejb.datos.Estados;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.FichaEmpleadoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.LicenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.TipoLicenciaServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.FichaEmpleado;
import ec.edu.uce.controlAsistencia.jpa.entidades.Licencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.Puesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.TipoLicencia;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@ManagedBean(name = "registrosLicencia")
@SessionScoped
public class RegistrosLicencias implements Serializable {

	/**
	 * VARIABLES
	 */
	private static final long serialVersionUID = 1L;

	private List<Licencia> listaLicencias = new ArrayList<>();
	private Licencia seleccionLicencia;

	private DetallePuestoDto detallePuestoEmpleado;
	private FichaEmpleado fichaEmpleado;
	private Dependencia dependencia;
	private Puesto puesto = null;
	private Regimen regimen = null;

	private DetallePuesto detallePuesto;
	private Licencia licencia;
	private boolean esActualizacion = false;
	private List<TipoLicencia> listaTipoLicencia = new ArrayList<>();
	private List<TipoLicencia> listaTipoLicenciaHijos = new ArrayList<>();
	private List<TipoLicencia> listaTipoLicenciaHijos2 = new ArrayList<>();
	private String tipoLicencia;
	private String tipoLicenciaHijo;
	private String tipoLicenciaHijo2;
	private Map<String, String> tiposLicencias;
	private Map<String, String> tiposLicenciasHijos;
	private Map<String, String> tiposLicenciasHijos2;
	private Estados estado;
	private TipoLicencia tipoLicenciaEntidad;
	private boolean btnConCargoVacaciones = false;
	private String nombreEtiqueta;
	private boolean YesNoEtiqueta = false;
	private boolean renderEtiqueta;

	private boolean btnComboHijo = false;
	private boolean btnComboHijo2 = false;
	private boolean disableNumDias = true;

	@ManagedProperty(value = "#{busEmpleado.seleccionPersona}")
	private PersonaDto seleccionPersona;

	/**
	 * SERVICIOS
	 */

	@EJB
	private DetallePuestoServicio srvDetallePuesto;

	@EJB
	private FichaEmpleadoServicio srvFichaEmpleado;

	@EJB
	private DependenciaServicio srvDependencia;

	@EJB
	private RegimenServicio srvRegimen;

	@EJB
	private PuestoServicio srvPuesto;

	@EJB
	private LicenciaServicio srvLicencia;

	@EJB
	private TipoLicenciaServicio srvTipoLicencia;

	@EJB
	private LicenciaServicio srvlicencia;

	@PostConstruct
	public void init() {
		this.listaTipoLicencia = srvTipoLicencia.listarTipoLicencia();
		this.tiposLicencias = new LinkedHashMap<>();
		this.listaTipoLicencia.forEach((tipoLicenciaEach) -> {
			tiposLicencias.put(tipoLicenciaEach.getTplcNombre(), String.valueOf(tipoLicenciaEach.getTplcId()));
		});
	}

	/**
	 * METODOS
	 */

	public void cargarHijos() {
		// tipoLicenciaEntidad =
		// srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
		this.listaTipoLicenciaHijos = srvTipoLicencia.buscarTipoLicenciaPorPadre(Integer.parseInt(this.tipoLicencia));
		if (this.listaTipoLicenciaHijos.isEmpty()) {
			// compruebo si la licencia es de tipo enfermedad
			if (Integer.parseInt(this.tipoLicencia) == 1) {
				this.btnComboHijo = false;
				this.disableNumDias = false;
				this.renderEtiqueta = false;
				this.btnComboHijo2 = false;
			} else {
				if (Integer.parseInt(this.tipoLicencia) == 2) {
					this.renderEtiqueta = true;
					this.nombreEtiqueta = "Nacimiento multiple o Cesaria?";
					this.btnComboHijo2 = false;
					this.disableNumDias = true;
				} else {
					this.renderEtiqueta = false;
					this.btnComboHijo2 = false;
					this.disableNumDias = true;
				}
				setNumeroDias(Integer.parseInt(this.tipoLicencia));
				this.btnComboHijo = false;
				this.disableNumDias = true;

			}

		} else {

			this.tiposLicenciasHijos = new LinkedHashMap<>();
			this.listaTipoLicenciaHijos.forEach((tipoLicenciaHijoEach) -> {
				tiposLicenciasHijos.put(tipoLicenciaHijoEach.getTplcNombre(),
						String.valueOf(tipoLicenciaHijoEach.getTplcId()));
			});
			this.renderEtiqueta = false;
			this.btnComboHijo = true;
			this.btnComboHijo2 = false;
			this.disableNumDias = true;
		}

	}

	public void cargarHijos2() {
		// tipoLicenciaEntidad =
		// srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
		this.listaTipoLicenciaHijos2 = srvTipoLicencia
				.buscarTipoLicenciaPorPadre(Integer.parseInt(this.tipoLicenciaHijo));
		if (this.listaTipoLicenciaHijos2.isEmpty()) {
			if (Integer.parseInt(this.tipoLicenciaHijo) == 4 || Integer.parseInt(this.tipoLicenciaHijo) == 5) {
				this.renderEtiqueta = true;
				this.nombreEtiqueta = "Nacimiento prematuro?";
				this.disableNumDias = true;
			} else {
				this.renderEtiqueta = false;
				this.disableNumDias = true;
			}
			setNumeroDias(Integer.parseInt(this.tipoLicenciaHijo));
			this.btnComboHijo2 = false;
			this.disableNumDias = true;
		} else {

			this.tiposLicenciasHijos2 = new LinkedHashMap<>();
			this.listaTipoLicenciaHijos2.forEach((tipoLicenciaHijoEach2) -> {
				tiposLicenciasHijos2.put(tipoLicenciaHijoEach2.getTplcNombre(),
						String.valueOf(tipoLicenciaHijoEach2.getTplcId()));
			});
			this.renderEtiqueta = false;
			this.btnComboHijo2 = true;
			this.disableNumDias = true;
		}

	}

	public void setNumeroDias(int id_tl) {
		int valor = id_tl;
		this.tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(valor);
		this.licencia.setLcnNumDias(this.tipoLicenciaEntidad.getTplcMinDias());
	}
	
	public void setNumDias(){
		setNumeroDias(Integer.parseInt(this.tipoLicenciaHijo2));
	}

	public void cambiarValorNumMax() {
		if (this.YesNoEtiqueta) {
			this.licencia.setLcnNumDias(this.tipoLicenciaEntidad.getTplcMaxDias());
		} else {
			this.licencia.setLcnNumDias(this.tipoLicenciaEntidad.getTplcMinDias());
		}
	}

	public void calcularPeriodoLicencia() {
		if (licencia.getLcnNumDias() > 0 && licencia.getLcnFechaInicio() != null) {
			licencia.setLcnFechaFin(calcularFechaFinal(licencia.getLcnFechaInicio(), licencia.getLcnNumDias()));
		}

	}

	public Date calcularFechaFinal(Date fechaInicio, int numDias) {
		Calendar fechaFinal = Calendar.getInstance();
		fechaFinal.setTime(fechaInicio);
		fechaFinal.add(Calendar.DAY_OF_YEAR, numDias - 1);

		return (Date) fechaFinal.getTime();
	}

	public void cargarVariables(Licencia seleccionLicencia) {
		if (seleccionLicencia == null) {
			esActualizacion = false;
			licencia = new Licencia();
			licencia.setLcnNumLicencia(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			licencia.setLcnFechaEmision(fechaEmision);

		} else {
			esActualizacion = true;
			licencia = seleccionLicencia;
			this.tipoLicencia = String.valueOf(this.licencia.getTipoLicencia().getTplcId());

		}

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

			retorno = srvlicencia.LicenciaInsertar(licencia);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "PrimeFaces Rocks."));
		}
	}

	public void verPDF(ActionEvent actionEvent) throws Exception {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("txt_num_auto", licencia.getLcnNumLicencia());
		parametros.put("txt_nombres", "la mar");
		parametros.put("txt_licencia", "la Mar");
		String resumen = "EXPLICACIÓN:\n\n" + licencia.getLcnExplicacion() + "\n\n Registra: "
				+ licencia.getLcnNumDias() + " días\n" + "Desde: " + licencia.getLcnFechaInicio() + " 	Hasta: "
				+ licencia.getLcnFechaFin() + "\n\n OBSERVACIÓN:\n" + licencia.getLcnObservacion();
		parametros.put("txt_resumen", resumen);
		parametros.put("txt_copia", licencia.getLcnCopia());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		File jasper = new File("C:\\ireports\\licencias.jasper");
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros);
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		response.addHeader("Content-disposition", "attachment; filename=licencia_" + sdf.format(new Date()) + ".pdf");
		ServletOutputStream stream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
		stream.flush();
		stream.close();
		FacesContext.getCurrentInstance().responseComplete();
		System.out.println("finaliza");

	}

	/**
	 * GETTER & SETTER
	 * 
	 * @return
	 */
	public Dependencia getDependencia() {
		if (dependencia == null) {
			dependencia = srvDependencia.ObtenerPorId(seleccionPersona.getDpnId());
		}
		return dependencia;
	}

	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}

	public DetallePuestoDto getDetallePuestoEmpleado() {
		if (detallePuestoEmpleado == null) {
			detallePuestoEmpleado = srvDetallePuesto.BuscarPorId(seleccionPersona.getDtpsId());
			if (detallePuestoEmpleado == null) {
				System.out.println(" salio nulo");
			}
		}

		return detallePuestoEmpleado;
	}

	public void setDetallePuestoEmpleado(DetallePuestoDto detallePuestoEmpleado) {
		this.detallePuestoEmpleado = detallePuestoEmpleado;
	}

	public FichaEmpleado getFichaEmpleado() {
		fichaEmpleado = srvFichaEmpleado.BuscarPorid(seleccionPersona.getFcemId());
		return fichaEmpleado;
	}

	public void setFichaEmpleado(FichaEmpleado fichaEmpleado) {
		this.fichaEmpleado = fichaEmpleado;
	}

	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}

	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}

	public Puesto getPuesto() {
		if (puesto == null) {

			puesto = srvPuesto.BuscarPorId(seleccionPersona.getPstId());

		}
		return puesto;
	}

	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}

	public Regimen getRegimen() {

		if (regimen == null) {
			regimen = srvRegimen.BuscarPorId(seleccionPersona.getRgmId());
		}
		return regimen;
	}

	public void setRegimen(Regimen regimen) {
		this.regimen = regimen;
	}

	public List<Licencia> getListaLicencias() {
		if (seleccionPersona != null) {
			listaLicencias = srvLicencia.ListaLicenciaPorDetallePuestoId(seleccionPersona.getDtpsId());
		}
		return listaLicencias;
	}

	public void setListaLicencias(List<Licencia> listaLicencias) {
		this.listaLicencias = listaLicencias;
	}

	public Licencia getSeleccionLicencia() {
		return seleccionLicencia;
	}

	public void setSeleccionLicencia(Licencia seleccionLicencia) {
		this.seleccionLicencia = seleccionLicencia;
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

	public boolean isBtnConCargoVacaciones() {
		return btnConCargoVacaciones;
	}

	public void setBtnConCargoVacaciones(boolean btnConCargoVacaciones) {
		this.btnConCargoVacaciones = btnConCargoVacaciones;
	}

	public Licencia getlicencia() {
		return licencia;
	}

	public void setlicencia(Licencia licencia) {
		this.licencia = licencia;
	}

	public String getTipoLicenciaHijo() {
		return tipoLicenciaHijo;
	}

	public void setTipoLicenciaHijo(String tipoLicenciaHijo) {
		this.tipoLicenciaHijo = tipoLicenciaHijo;
	}

	public Map<String, String> getTiposLicenciasHijos() {
		return tiposLicenciasHijos;
	}

	public void setTiposLicenciasHijos(Map<String, String> tiposLicenciasHijos) {
		this.tiposLicenciasHijos = tiposLicenciasHijos;
	}

	public boolean isBtnComboHijo() {
		return btnComboHijo;
	}

	public void setBtnComboHijo(boolean btnComboHijo) {
		this.btnComboHijo = btnComboHijo;
	}

	public String getTipoLicenciaHijo2() {
		return tipoLicenciaHijo2;
	}

	public void setTipoLicenciaHijo2(String tipoLicenciaHijo2) {
		this.tipoLicenciaHijo2 = tipoLicenciaHijo2;
	}

	public Map<String, String> getTiposLicenciasHijos2() {
		return tiposLicenciasHijos2;
	}

	public void setTiposLicenciasHijos2(Map<String, String> tiposLicenciasHijos2) {
		this.tiposLicenciasHijos2 = tiposLicenciasHijos2;
	}

	public boolean isBtnComboHijo2() {
		return btnComboHijo2;
	}

	public void setBtnComboHijo2(boolean btnComboHijo2) {
		this.btnComboHijo2 = btnComboHijo2;
	}

	public boolean isDisableNumDias() {
		return disableNumDias;
	}

	public void setDisableNumDias(boolean disableNumDias) {
		this.disableNumDias = disableNumDias;
	}

	public String getNombreEtiqueta() {
		return nombreEtiqueta;
	}

	public void setNombreEtiqueta(String nombreEtiqueta) {
		this.nombreEtiqueta = nombreEtiqueta;
	}

	public boolean isYesNoEtiqueta() {
		return YesNoEtiqueta;
	}

	public void setYesNoEtiqueta(boolean yesNoEtiqueta) {
		YesNoEtiqueta = yesNoEtiqueta;
	}

	public boolean isRenderEtiqueta() {
		return renderEtiqueta;
	}

	public void setRenderEtiqueta(boolean renderEtiqueta) {
		this.renderEtiqueta = renderEtiqueta;
	}

}
