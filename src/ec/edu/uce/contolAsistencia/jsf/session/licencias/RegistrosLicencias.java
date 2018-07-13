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
	private String tipoLicencia;
	private Map<String, String> tiposLicencias;
	private Estados estado;
	private TipoLicencia tipoLicenciaEntidad;
	private boolean btnConCargoVacaciones = false;

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

	public void calcularPeriodoLicencia() {
		if (licencia.getLcnNumDias() > 0 && licencia.getLcnFechaInicio() != null) {
			licencia.setLcnFechaFin(
					calcularFechaFinal(licencia.getLcnFechaInicio(), licencia.getLcnNumDias()));
		}

	}

	public Date calcularFechaFinal(Date fechaInicio, int numDias) {
		Calendar fechaFinal = Calendar.getInstance();
		fechaFinal.setTime(fechaInicio);
		fechaFinal.add(Calendar.DAY_OF_YEAR, numDias);

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
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "PrimeFaces Rocks."));
		}
	}
	
	public void verPDF(ActionEvent actionEvent) throws Exception{
		Map<String, Object> parametros = new HashMap<>();
            parametros.put("txt_num_auto", licencia.getLcnNumLicencia());
            parametros.put("txt_nombres", "la mar");
            parametros.put("txt_licencia", licencia.getTipoLicencia().getTplcNombre());
            String resumen = "EXPLICACIÓN:\n\n"+licencia.getLcnExplicacion()+"\n\n Registra: "+licencia.getLcnNumDias()+" días\n"+
            "Desde: "+licencia.getLcnFechaInicio()+" 	Hasta: "+licencia.getLcnFechaFin()+"\n\n OBSERVACIÓN:\n"+licencia.getLcnObservacion();
            parametros.put("txt_resumen", resumen);
            parametros.put("txt_copia", licencia.getLcnCopia());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            
            File jasper = new File("C:\\ireports\\licencias.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
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
	
	

}
