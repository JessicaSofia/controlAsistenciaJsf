package ec.edu.uce.contolAsistencia.jsf.session.vacacion;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import ec.edu.uce.controlAsistencia.ejb.datos.DetallePuestoDto;
import ec.edu.uce.controlAsistencia.ejb.datos.Estados;
import ec.edu.uce.controlAsistencia.ejb.datos.ParametrosVacacion;
import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ContratoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DependenciaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.FichaEmpleadoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.ParametroVacacionesServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PermisoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PersonaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.RegimenServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.VacacionServicio;
import ec.edu.uce.controlAsistencia.jpa.entidades.Dependencia;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.FichaEmpleado;
import ec.edu.uce.controlAsistencia.jpa.entidades.Permiso;
import ec.edu.uce.controlAsistencia.jpa.entidades.Puesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.Regimen;
import ec.edu.uce.controlAsistencia.jpa.entidades.SaldoVacacion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Vacacion;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;

@ManagedBean(name = "vacacionForm")
@SessionScoped
public class VacacionForm implements Serializable {

	/**
	 * Variables
	 */
	private static final long serialVersionUID = 1L;
	private Vacacion vacacion;
	private DetallePuesto detallePuesto;
	private Estados estado;
	private List<SaldoVacacion> saldoVacacion = null;
	private boolean esActualizacion = false;
	private List<Vacacion> listaVacacion = new ArrayList<>();
	private Vacacion seleccionVacacion;
	private SaldoVacacion saldoVacacion1;
	private SaldoVacacion saldoVacacion2;
	private DetallePuestoDto detallePuestoEmpleado;
	private FichaEmpleado fichaEmpleado;
	private Dependencia dependencia;
	private Puesto puesto = null;
	private Regimen regimen = null;
	private PersonaDto seleccionPersona;
	private SaldoVacacion salVacaCal1;
	private SaldoVacacion salVacaCal2;
	
	
	/*Permiso Personal*/
	private Permiso permiso;
	private List<Permiso> listaPermisos;
	private Permiso seleccionPermiso;
	// Si es false, el permiso es por horas
	private boolean diasHoras = false;
	private boolean disableHoras = true;
	private boolean disableDias = false;

	/***
	 * Declaracion de servicios
	 */
	@EJB
	private FichaEmpleadoServicio srvFichaEmpleado;
	@EJB
	private DependenciaServicio srvDependencia;
	@EJB
	private RegimenServicio srvRegimen;
	@EJB
	private PuestoServicio srvPuesto;
	@EJB
	private VacacionServicio srvVacacion;

	@EJB
	private DetallePuestoServicio srvDetallePuesto;

	@EJB
	private ContratoServicio srvContrato;

	@EJB
	private PersonaServicio srvPersona;
	@EJB
	private ParametroVacacionesServicio srvParamVacaciones;

	@EJB
	private PermisoServicio srvPermiso;

	/**
	 * Getters and setters
	 */

	public Dependencia getDependencia() {
		if (dependencia == null) {
			dependencia = srvDependencia.ObtenerPorId(seleccionPersona.getDpnId());
		}
		return dependencia;
	}

	public List<Permiso> getListaPermisos() {
		if (seleccionPersona != null) {
			listaPermisos = srvPermiso.ListaPermisoPorDetallePuestoId(seleccionPersona.getDtpsId());
		}
		return listaPermisos;
	}

	public void setListaPermisos(List<Permiso> listaPermisos) {
		this.listaPermisos = listaPermisos;
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

	public List<Vacacion> getListaVacacion() {
		if (seleccionPersona != null) {
			listaVacacion = srvVacacion.ListaVacacionesPorDetallePuestoId(seleccionPersona.getDtpsId());
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

	public VacacionServicio getSrvVacacion() {
		return srvVacacion;
	}

	public void setSrvVacacion(VacacionServicio srvVacacion) {
		this.srvVacacion = srvVacacion;
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

	public SaldoVacacion getSalVacaCal1() {
		return salVacaCal1;
	}

	public void setSalVacaCal1(SaldoVacacion salVacaCal1) {
		this.salVacaCal1 = salVacaCal1;
	}

	public SaldoVacacion getSalVacaCal2() {
		return salVacaCal2;
	}

	public void setSalVacaCal2(SaldoVacacion salVacaCal2) {
		this.salVacaCal2 = salVacaCal2;
	}

	public Permiso getPermiso() {
		return permiso;
	}

	public void setPermiso(Permiso permiso) {
		this.permiso = permiso;
	}

	

	public Permiso getSeleccionPermiso() {
		return seleccionPermiso;
	}

	public void setSeleccionPermiso(Permiso seleccionPermiso) {
		this.seleccionPermiso = seleccionPermiso;
	}

	@PostConstruct
	public void init() {

	}

	/**
	 * 
	 * =============Getters and Setters==============
	 */
	
	
	public Vacacion getVacacion() {
		return vacacion;
	}

	public boolean isDisableHoras() {
		return disableHoras;
	}

	public void setDisableHoras(boolean disableHoras) {
		this.disableHoras = disableHoras;
	}

	public boolean isDisableDias() {
		return disableDias;
	}

	public void setDisableDias(boolean disableDias) {
		this.disableDias = disableDias;
	}

	public boolean isDiasHoras() {
		return diasHoras;
	}

	public void setDiasHoras(boolean diasHoras) {
		this.diasHoras = diasHoras;
	}

	public void setVacacion(Vacacion vacacion) {
		this.vacacion = vacacion;
	}

	public List<SaldoVacacion> getSaldoVacacion() {
		return saldoVacacion;
	}

	public void setSaldoVacacion(List<SaldoVacacion> saldoVacacion) {
		this.saldoVacacion = saldoVacacion;
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

	public void CalcularVacaciones() {
		if (vacacion.getVccNumDias() > 0 && vacacion.getVccFechaInicio() != null) {
			CalcularSaldoVacacion(vacacion.getVccFechaInicio(), vacacion.getVccNumDias());
		} else {
			// implementacion de mensajes
		}

	}

	public void GuardarVacacion() {
		boolean retorno = false;
		detallePuesto = srvDetallePuesto.DetallePuestoBuscarPorId(seleccionPersona.getDtpsId());

		vacacion.setDetallePuesto(detallePuesto);
		if (esActualizacion) {
			Vacacion vac = srvVacacion.VacacionActualizar(vacacion);
			if (vac != null) {
				retorno = true;
			} else {
				retorno = false;
			}
		} else {
			vacacion.setVccEstado(Estados.Activo.getId());
			retorno = srvVacacion.VacionInsertar(vacacion);
		}

		if (retorno) {
			if (salVacaCal1 != null && salVacaCal2 != null) {

				salVacaCal1.setDetallePuesto(detallePuesto);
				salVacaCal2.setDetallePuesto(detallePuesto);
				srvVacacion.ActualizarSaldoVacacion(salVacaCal1);
				srvVacacion.ActualizarSaldoVacacion(salVacaCal2);

			} else {
				if (salVacaCal1 == null && salVacaCal2 != null) {
					salVacaCal2.setDetallePuesto(detallePuesto);
					srvVacacion.ActualizarSaldoVacacion(salVacaCal2);

				}
			}
		} else {
			// no se inserto o no se actualizo
		}

	}

	public void EditarVacacion() {

	}

	/***
	 * Calcula la fecha final de vacaciones
	 * 
	 * @param fechaInicio
	 * @param numDias
	 * @return
	 */
	public Date calcularFechaFinal(Date fechaInicio, int numDias) {
		Calendar fechaFinal = Calendar.getInstance();
		fechaFinal.setTime(fechaInicio);
		fechaFinal.add(Calendar.DAY_OF_YEAR, numDias - 1);
		return (Date) fechaFinal.getTime();
	}

	/**
	 * Metodo paara generar el Numero De Autorizacion
	 * 
	 * @return
	 */
	public int generarNumAutorizacion() {
		int numAutorizacion = 0;
		if (srvVacacion.MaximaNumAutorizacion() != 0) {
			numAutorizacion = srvVacacion.MaximaNumAutorizacion() + 1;
		} else {
			numAutorizacion = 1;
		}
		return numAutorizacion;
	}

	/***
	 * calcula el Salo de vacaciones por periodos disponibles
	 * 
	 * @param fechaInicio
	 * @param num
	 */
	public void CalcularSaldoVacacion(Date fechaInicio, int num) {

		int totaldias2 = 0, saldoDias2 = 0, diasReg2 = 0, diasAnt2 = 0, numFSem1 = 0, numFsTotal1 = 0;
		int totaldias1 = 0, saldoDias1 = 0, diasReg1 = 0, diasAnt1 = 0, numFSem2 = 0, numFsTotal2 = 0;

		if (salVacaCal1 != null) {
			saldoDias1 = salVacaCal1.getSlvcDiasRestantes();
			totaldias1 = salVacaCal1.getSlvcTotalDias();
			diasReg1 = salVacaCal1.getSlvcDiasRegistrados();
			diasAnt1 = salVacaCal1.getSlvcDiasAnticipados();
			numFSem1 = salVacaCal1.getSlvcNumFinSemana();
			numFsTotal1 = salVacaCal1.getSlvcTotalDias() / 7;

		}
		if (salVacaCal2 != null) {
			totaldias2 = salVacaCal2.getSlvcTotalDias();
			saldoDias2 = salVacaCal2.getSlvcDiasRestantes();
			diasReg2 = salVacaCal2.getSlvcDiasRegistrados();
			diasAnt2 = salVacaCal2.getSlvcDiasAnticipados();
			numFSem2 = salVacaCal2.getSlvcNumFinSemana();
			numFsTotal2 = salVacaCal2.getSlvcTotalDias() / 7;
		}

		if (saldoVacacion1 != null && saldoVacacion2 != null) {
			int f = num / 7;

			int saldoTotaldias = saldoDias1 - num;
			if (saldoTotaldias < 0) {
				salVacaCal1.setSlvcDiasRegistrados(totaldias1);
				salVacaCal1.setSlvcDiasRestantes(0);
				salVacaCal1.setSlvcEstado(Estados.DesActivo.getId());
				salVacaCal1.setSlvcNumFinSemana(numFsTotal1);
				salVacaCal2.setSlvcPeriodo(1);
				saldoTotaldias = saldoTotaldias + saldoDias2;
				if (saldoTotaldias < 0) {
					int numres = saldoTotaldias * (-1);
					System.out.println("El Usuario no tiene disponible el numero de dias solicitadas");
					salVacaCal2.setSlvcDiasAnticipados(numres);
					salVacaCal2.setSlvcDiasRegistrados(totaldias2);
					salVacaCal2.setSlvcDiasRestantes(0);
					vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio(), num));
					salVacaCal2.setSlvcNumFinSemana(numFsTotal2);

				} else {

					Map<String, Integer> resultado = CalcularNumDiasADescontar(saldoVacacion2, num);
					int n = resultado.get("diasDescontar");
					salVacaCal2.setSlvcDiasRegistrados(diasReg2 + n);
					salVacaCal2.setSlvcDiasRestantes(totaldias2 - (diasReg2 + n));
					salVacaCal2.setSlvcTotalHoras(salVacaCal1.getSlvcTotalHoras());
					salVacaCal1.setSlvcTotalHoras("00:00");
					vacacion.setVccFechaFin(
							calcularFechaFinal(vacacion.getVccFechaInicio(), resultado.get("diasCalcularFecha")));
					salVacaCal2.setSlvcNumFinSemana(numFSem2 + resultado.get("finSemana"));
				}
			}

			else {
				Map<String, Integer> resultado = CalcularNumDiasADescontar(saldoVacacion1, num);
				int n = resultado.get("diasDescontar");
				salVacaCal1.setSlvcDiasRegistrados(diasReg1 + n);
				salVacaCal1.setSlvcDiasRestantes(totaldias1 - (diasReg1 + n));
				salVacaCal1.setSlvcNumFinSemana(numFSem1 + resultado.get("finSemana"));

				vacacion.setVccFechaFin(
						calcularFechaFinal(vacacion.getVccFechaInicio(), resultado.get("diasCalcularFecha")));

			}

		} else {
			if (salVacaCal1 == null && salVacaCal2 != null) {

				int saldoTotaldias = saldoDias2 - num;
				if (saldoTotaldias < 0) {
					System.out.println("El Usuario no tiene disponible el numero de dias solicitadas");
					salVacaCal2.setSlvcDiasAnticipados((-1) * saldoTotaldias);
					salVacaCal2.setSlvcDiasRegistrados(totaldias2);
					salVacaCal2.setSlvcDiasRestantes(0);
					salVacaCal2.setSlvcNumFinSemana(numFsTotal2);
					vacacion.setVccFechaFin(calcularFechaFinal(vacacion.getVccFechaInicio(), num));
				} else {

					Map<String, Integer> resultado = CalcularNumDiasADescontar(saldoVacacion2, num);
					int n = resultado.get("diasDescontar");
					salVacaCal2.setSlvcDiasRegistrados(diasReg1 + n);
					salVacaCal2.setSlvcDiasRestantes(totaldias1 - (diasReg1 + n));
					salVacaCal2.setSlvcNumFinSemana(numFSem1 + resultado.get("finSemana"));
					vacacion.setVccFechaFin(
							calcularFechaFinal(vacacion.getVccFechaInicio(), resultado.get("diasCalcularFecha")));

				}

			}
		}
	}

	public void anularVacacion() {

		boolean retorno = false;
		vacacion.setVccEstado(Estados.Anulado.getId());
		vacacion.getVccObservacionEstado();
		srvVacacion.VacacionActualizar(vacacion);

	}

	/***
	 * calcula el numero de dias a Descontar totales;
	 * 
	 * @param saldoVacacion
	 * @param num
	 * @return
	 */
	public Map<String, Integer> CalcularNumDiasADescontar(SaldoVacacion saldoVacacion, int num) {

		Map<String, Integer> retorno = new HashMap<String, Integer>();
		int n = 0;
		int n1 = 0;
		int nAñF = 0;
		int numFReg = saldoVacacion.getSlvcNumFinSemana();
		int finSemana = saldoVacacion.getSlvcTotalDias() / 7;
		if (finSemana != 0) {
			if (num <= 5) {

				if (numFReg < finSemana) {

					int dsFin = numFReg * 2;
					int sumReg = saldoVacacion.getSlvcDiasRegistrados() + num - dsFin;

					int fins = sumReg / 5;
					if (numFReg < fins) {

						n = num + 2;
						nAñF = 1;
						if (num == 5) {
							n1 = n;
						} else {
							n1 = num;
						}

					} else {
						n = num;
						n1 = num;
					}

				} else {

					n = num;
					n1 = num;
				}

			} else {
				n = num;
				n1 = num;
				int dSobrantes = saldoVacacion.getSlvcDiasRegistrados() % 7;
				int sumReg = num + dSobrantes;
				nAñF = sumReg / 7;
			}

		}
		retorno.put("diasDescontar", n);
		retorno.put("diasCalcularFecha", n1);
		retorno.put("finSemana", nAñF);

		return retorno;

	}

	public String regresar() {
		String ruta = "/controlAsistencia/vacaciones/VacacionesRegistros.xhtml";
		vacacion = null;
		saldoVacacion = null;
		// cargarVariables();
		return ruta;
	}

	public void cargarVariablesVacacion() {

		if (seleccionVacacion == null) {
			esActualizacion = false;
			vacacion = new Vacacion();
			vacacion.setVccNumAutorizacion(generarNumAutorizacion());
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			vacacion.setVccFechaEmision(fechaEmision);

		} else {
			esActualizacion = true;
			vacacion = seleccionVacacion;
		}

		salVacaCal1 = saldoVacacion1;
		salVacaCal2 = saldoVacacion2;

	}

	public void cargarVariablesPermiso() {

		if (seleccionPermiso == null) {
			esActualizacion = false;
			permiso = new Permiso();
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			permiso.setPrmFechaRegistro(fechaEmision);

		} else {
			esActualizacion = true;
			permiso = seleccionPermiso;
		}

		salVacaCal1 = saldoVacacion1;
		salVacaCal2 = saldoVacacion2;

	}

	public void limpiar() {
		seleccionPersona = null;
		listaVacacion = null;
		saldoVacacion1 = null;
		saldoVacacion2 = null;
		dependencia = null;
		regimen = null;
		puesto = null;
		vacacion = null;

	}

	public void cargarVariables(PersonaDto persona) {
		limpiar();
		if (persona != null) {
			seleccionPersona = persona;
		}
		saldoVacacion = srvVacacion.listSaldoVacacionPorDetallePuestoId(seleccionPersona.getDtpsId());
		if (saldoVacacion.size() > 1) {
			for (SaldoVacacion s : saldoVacacion) {
				if (s.getSlvcPeriodo() == 1) {
					saldoVacacion1 = s;
				}
				if (s.getSlvcPeriodo() == 2) {
					saldoVacacion2 = s;
				}
			}
		} else {
			for (SaldoVacacion s : saldoVacacion) {
				saldoVacacion2 = s;
			}

		}
	}
	
	public void cambiarValorDiasHoras(){
		if(this.diasHoras){
			this.disableDias = false;
			this.disableHoras = true;
		}else{
			this.disableDias = true;
			this.disableHoras = false;
		}
	}
	
	
	public void verPDF()  {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("txt_num_auto", String.valueOf(vacacion.getVccNumAutorizacion()));
			parametros.put("txt_nombres", seleccionPersona.nombresCompetos());
			parametros.put("txt_dependencia", dependencia.getDpnDescripcion());
			String fecha_inicio = sdf.format(vacacion.getVccFechaInicio());
			String fecha_fin = sdf.format(vacacion.getVccFechaFin());
			
			parametros.put("txt_dias", String.valueOf(vacacion.getVccNumDias()));
			parametros.put("txt_desde", fecha_inicio);
			parametros.put("txt_hasta", fecha_fin);
			parametros.put("txt_saldo1", String.valueOf(salVacaCal1.getSlvcDiasRestantes()));
			parametros.put("txt_saldo2", String.valueOf(salVacaCal2.getSlvcDiasRestantes()));
			parametros.put("txt_copia", dependencia.getDpnDescripcion());
			

			File jasper = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/controlAsistencia/reportes/vacaciones.jasper"));
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros);
			
			
			InputStream rptStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/controlAsistencia/reportes/vacaciones.jasper");
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			response.addHeader("Content-disposition", "attachment; filename=vacacion_" + sdf.format(new Date()).toString() + ".pdf");
			ServletOutputStream stream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
			JasperRunManager.runReportToPdfStream(rptStream,stream, parametros, new JREmptyDataSource());
			stream.flush();
			stream.close();
			FacesContext.getCurrentInstance().responseComplete();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


}
