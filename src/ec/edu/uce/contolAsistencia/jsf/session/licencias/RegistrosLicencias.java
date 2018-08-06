package ec.edu.uce.contolAsistencia.jsf.session.licencias;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.TipoLicencia;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
	private boolean renderBtnImprimir = false;

	/*
	 * @ManagedProperty(value = "#{busEmpleado.seleccionPersona}")
	 */
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

	}

	/**
	 * METODOS
	 */

	public void cargarHijos() {

		this.listaTipoLicenciaHijos = srvTipoLicencia.buscarTipoLicenciaPorPadre(Integer.parseInt(this.tipoLicencia));
		if (this.listaTipoLicenciaHijos.isEmpty()) {
			// compruebo si la licencia es de tipo enfermedad
			TipoLicencia tipoLicenciaAux = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
			if (tipoLicenciaAux.getTplcNombre().equals("ENFERMEDAD")) {
				this.btnComboHijo = false;
				this.disableNumDias = false;
				this.renderEtiqueta = false;
				this.btnComboHijo2 = false;
				tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
				this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
			} else {
				if (tipoLicenciaAux.getTplcNombre().equals("MATERNIDAD") && this.regimen.getRgmId() != 3) {
					this.renderEtiqueta = true;
					this.nombreEtiqueta = "Nacimiento multiple o Cesaria?";
					this.btnComboHijo2 = false;
					this.disableNumDias = true;
					tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
					this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
				} else {
					this.renderEtiqueta = false;
					this.btnComboHijo2 = false;
					this.disableNumDias = true;
					tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
					this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
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
			tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
			this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
		}

	}

	public void cargarHijos2() {
		// tipoLicenciaEntidad =
		// srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicencia));
		this.listaTipoLicenciaHijos2 = srvTipoLicencia
				.buscarTipoLicenciaPorPadre(Integer.parseInt(this.tipoLicenciaHijo));
		if (this.listaTipoLicenciaHijos2.isEmpty()) {
			TipoLicencia tipoLicenciaAux = srvTipoLicencia
					.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicenciaHijo));
			if (tipoLicenciaAux.getTplcNombre().equals("PARTO: NORMAL")
					|| tipoLicenciaAux.getTplcNombre().equals("PARTO: CESARIA")) {
				if (this.regimen.getRgmId() != 3) {
					this.renderEtiqueta = true;
					this.nombreEtiqueta = "Nacimiento prematuro?";
					this.disableNumDias = true;
				}else{
					this.renderEtiqueta = false;
					this.disableNumDias = true;
				}

				tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicenciaHijo));
				this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
			} else {
				this.renderEtiqueta = false;
				this.disableNumDias = true;
				tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicenciaHijo));
				this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
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
			tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicenciaHijo));
			this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
		}

	}

	public void setNumeroDias(int id_tl) {
		int valor = id_tl;
		this.tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(valor);
		this.licencia.setLcnNumDias(this.tipoLicenciaEntidad.getTplcMinDias());
	}

	public void setNumDias() {
		setNumeroDias(Integer.parseInt(this.tipoLicenciaHijo2));
		tipoLicenciaEntidad = srvTipoLicencia.buscarTipoLicenciaPorId(Integer.parseInt(this.tipoLicenciaHijo2));
		this.licencia.setLcnObservacion(tipoLicenciaEntidad.getTplcDescripcion());
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
		System.out.println(seleccionPersona.getDtpsId());
		//licencia.setDetallePuesto(detallePuesto);

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
			if (this.tipoLicencia.equals("0") || this.licencia.getLcnFechaInicio() == null
					|| this.licencia.getLcnFechaFin() == null || this.licencia.getLcnExplicacion().equals("")) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Advertencia.", "Los campos marcados con (*) son obligatorios."));

			} else {
				retorno = srvlicencia.LicenciaInsertar(licencia);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Información.", "Licencia registrada exitosamente."));
				this.renderBtnImprimir = true;
			}

		}
	}

	public void verPDF() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("txt_num_auto", String.valueOf(licencia.getLcnNumLicencia()));
			parametros.put("txt_nombres", seleccionPersona.nombresCompetos());
			parametros.put("txt_licencia", licencia.getTipoLicencia().getTplcNombre());
			parametros.put("txt_dependencia",  seleccionPersona.getDpnNombre());
			String fecha_inicio = sdf.format(licencia.getLcnFechaInicio());
			String fecha_fin = sdf.format(licencia.getLcnFechaFin());

			String resumen = "EXPLICACIÓN:\n\n" + licencia.getLcnExplicacion() + "\n\nRegistra: "
					+ licencia.getLcnNumDias() + " días\n\n" + "Desde: " + fecha_inicio + " 	Hasta: " + fecha_fin
					+ "\n\nOBSERVACIÓN:\n" + licencia.getLcnObservacion();
			parametros.put("txt_resumen", resumen);
			parametros.put("txt_copia", licencia.getLcnCopia());

			File jasper = new File(FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/controlAsistencia/reportes/licencias.jasper"));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros);

			InputStream rptStream = FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/controlAsistencia/reportes/licencias.jasper");
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			response.addHeader("Content-disposition", "attachment; filename=LICENCIA_"
					+ this.seleccionPersona.nombresCompetos() + "_" + sdf.format(new Date()).toString() + ".pdf");
			ServletOutputStream stream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
			JasperRunManager.runReportToPdfStream(rptStream, stream, parametros, new JREmptyDataSource());
			stream.flush();
			stream.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void cargarVariables(Licencia seleccionLicencia) {
		if (seleccionLicencia == null) {
			esActualizacion = false;
			licencia = new Licencia();
			licencia.setLcnNumLicencia(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			licencia.setLcnFechaEmision(fechaEmision);
			//licencia.setLcnCopia(dependencia.getDpnDescripcion());
			licencia.setLcnCopia(seleccionPersona.getDpnNombre());
			//licencia.setDetallePuesto(detallePuesto);
			detallePuesto = srvDetallePuesto.DetallePuestoBuscarPorId(seleccionPersona.getDtpsId());
			System.out.println(seleccionPersona.getDtpsId());
			//licencia.setDetallePuesto(detallePuesto);
			licencia.setDtpsId(seleccionPersona.getDtpsId());
			

		} else {
			esActualizacion = true;
			licencia = seleccionLicencia;
			this.tipoLicencia = String.valueOf(this.licencia.getTipoLicencia().getTplcId());

		}

	}

	public void cargarVariablesFuncionario(PersonaDto persona) {
		limpiar();
		if (persona != null) {
			seleccionPersona = persona;
			//this.regimen = srvRegimen.BuscarPorId(seleccionPersona.getRgmId());
			this.listaTipoLicencia = srvTipoLicencia.listarTipoLicencia(seleccionPersona.getRgmId());
			this.tiposLicencias = new LinkedHashMap<>();
			this.listaTipoLicencia.forEach((tipoLicenciaEach) -> {
				tiposLicencias.put(tipoLicenciaEach.getTplcNombre(), String.valueOf(tipoLicenciaEach.getTplcId()));
			});
		}

	}

	public void limpiar() {
		seleccionPersona = null;
		this.listaLicencias = null;
		dependencia = null;
		regimen = null;
		puesto = null;
		this.licencia = null;

	}

	public String regresar() {
		String ruta = "/controlAsistencia/licencias/LicenciasRegistros.xhtml";
		this.licencia = null;
		this.renderBtnImprimir = false;
		this.tipoLicencia = "0";
		this.tipoLicenciaHijo = "0";
		this.tipoLicenciaHijo2 = "0";
		this.btnComboHijo = false;
		this.btnComboHijo2 = false;
		// saldoVacacion = null;
		// cargarVariables();
		return ruta;
	}

	public String regresarEmpleados() {
		String ruta = "/controlAsistencia/empleado/busEmpleado.xhtml";
		this.seleccionPersona = null;
		// this.licencia = null;
		// saldoVacacion = null;
		// cargarVariables();
		return ruta;
	}

	/**
	 * GETTER & SETTER
	 * 
	 * @return
	 */
	/*public Dependencia getDependencia() {
		/*if (dependencia == null) {
			dependencia = srvDependencia.ObtenerPorId(seleccionPersona.getDpnId());
		}
		return dependencia;

	}*/

	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}

	/*public DetallePuestoDto getDetallePuestoEmpleado() {
		if (detallePuestoEmpleado == null) {
			detallePuestoEmpleado = srvDetallePuesto.BuscarPorId(seleccionPersona.getDtpsId());
			if (detallePuestoEmpleado == null) {
				System.out.println(" salio nulo");
			}
		}

		return detallePuestoEmpleado;
	}*/

	public void setDetallePuestoEmpleado(DetallePuestoDto detallePuestoEmpleado) {
		this.detallePuestoEmpleado = detallePuestoEmpleado;
	}

	/*public FichaEmpleado getFichaEmpleado() {
		fichaEmpleado = srvFichaEmpleado.BuscarPorid(seleccionPersona.getFcemId());
		return fichaEmpleado;
	}*/

	public void setFichaEmpleado(FichaEmpleado fichaEmpleado) {
		this.fichaEmpleado = fichaEmpleado;
	}

	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}

	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}

	/*public Puesto getPuesto() {
		if (puesto == null) {

			puesto = srvPuesto.BuscarPorId(seleccionPersona.getPstId());

		}
		return puesto;
	}*/

	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}

	/*public Regimen getRegimen() {

		if (regimen == null) {
			regimen = srvRegimen.BuscarPorId(seleccionPersona.getRgmId());
		}
		return regimen;
	}*/

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

	public boolean isRenderBtnImprimir() {
		return renderBtnImprimir;
	}

	public void setRenderBtnImprimir(boolean renderBtnImprimir) {
		this.renderBtnImprimir = renderBtnImprimir;
	}

}
