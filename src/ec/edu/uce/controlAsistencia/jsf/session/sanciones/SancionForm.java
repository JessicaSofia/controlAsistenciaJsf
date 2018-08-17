package ec.edu.uce.controlAsistencia.jsf.session.sanciones;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import ec.edu.uce.controlAsistencia.ejb.datos.Estados;

import ec.edu.uce.controlAsistencia.ejb.datos.PersonaDto;
import ec.edu.uce.controlAsistencia.ejb.datos.ReporteMulta;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.DetallePuestoServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.PersonaServicio;
import ec.edu.uce.controlAsistencia.ejb.servicios.interfaces.SancionesServicio;

import ec.edu.uce.controlAsistencia.jpa.entidades.CategoriaFalta;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuesto;
import ec.edu.uce.controlAsistencia.jpa.entidades.DetallePuestoSancion;
import ec.edu.uce.controlAsistencia.jpa.entidades.Falta;
import ec.edu.uce.controlAsistencia.jpa.entidades.Sancion;
import ec.edu.uce.controlAsistencia.jpa.entidades.TipoLicencia;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean(name = "sancionForm")
@SessionScoped
public class SancionForm implements Serializable {

	/**
	 * VARIABLES
	 */
	private static final long serialVersionUID = 1L;

	private List<DetallePuestoSancion> lstSanciones = new ArrayList<>();
	private DetallePuestoSancion seleccionDtSancion = null;
	private PersonaDto seleccionPersona;
	private Falta falta;
	private String tipoFalta;
	private List<Falta> listaFaltas = null;
	private Sancion sancion;
	private Map<String, String> tipoFaltas;
	private CategoriaFalta categoriaFaltaAplicar;

	private DetallePuesto detallePuesto;
	private DetallePuestoSancion dtSancion = new DetallePuestoSancion();
	private boolean esActualizacion = false;
	private List<Sancion> lstTipSanciones = null;
	private String tipoSancion;
	private int faltaId;
	private Map<String, String> tiposSanciones;
	private TipoLicencia tipoSancionEntidad;
	private boolean EsDescuento = false;
	private boolean esPorFrecuencia = false;
	private boolean esImprimir = false;
	private boolean esBloqueado = false;
	private Date fecha;
	private boolean activar = false;
	private int sueldo = 0;
	private float valor = 0;
	private boolean btnRenderMulta = false;
	private float valoTotalMultas = 0;
	private HashMap<String, String> meses = new HashMap<>();
	private String mesReporte;
	private String anioReporte;

	/* Reporte multas */
	private List<DetallePuestoSancion> listaMultas = new ArrayList<>();

	@PostConstruct
	public void init() {
		this.listaFaltas = srvSanciones.listarFalta();
		this.tipoFaltas = new LinkedHashMap<>();
		this.listaFaltas.forEach((tipoFaltaEach) -> {
			tipoFaltas.put(tipoFaltaEach.getFlNombre(), String.valueOf(tipoFaltaEach.getFlId()));
		});

		this.lstTipSanciones = srvSanciones.listarSancion();
		this.tiposSanciones = new LinkedHashMap<>();
		this.lstTipSanciones.forEach((tipoSancionEach) -> {
			tiposSanciones.put(tipoSancionEach.getSnNombre(), String.valueOf(tipoSancionEach.getSnId()));
		});
		
		this.meses.put("1", "ENERO");
		this.meses.put("2", "FEBRERO");
		this.meses.put("3", "MARZO");
		this.meses.put("4", "ABRIL");
		this.meses.put("5", "MAYO");
		this.meses.put("6", "JUNIO");
		this.meses.put("7", "JULIO");
		this.meses.put("8", "AGOSTO");
		this.meses.put("9", "SEPTIEMBRE");
		this.meses.put("10", "OCTUBRE");
		this.meses.put("11", "NOVIEMBRE");
		this.meses.put("12", "DICIEMBRE");
	}

	/**
	 * Declaracion de Servicios
	 * 
	 */

	@EJB
	private DetallePuestoServicio srvDetallePuesto;

	@EJB
	private PersonaServicio srvPersona;

	@EJB
	private SancionesServicio srvSanciones;

	public List<DetallePuestoSancion> getLstSanciones() {
		lstSanciones = srvSanciones.listarSancionPorDetallePuestoId(seleccionPersona.getDtpsId());

		return lstSanciones;
	}

	public void setLstSanciones(List<DetallePuestoSancion> lstSanciones) {
		this.lstSanciones = lstSanciones;
	}

	public DetallePuestoSancion getSeleccionDtSancion() {
		return seleccionDtSancion;
	}

	public CategoriaFalta getCategoriaFaltaAplicar() {
		return categoriaFaltaAplicar;
	}

	public void setCategoriaFaltaAplicar(CategoriaFalta categoriaFaltaAplicar) {
		this.categoriaFaltaAplicar = categoriaFaltaAplicar;
	}

	public PersonaDto getSeleccionPersona() {
		return seleccionPersona;
	}

	public void setSeleccionPersona(PersonaDto seleccionPersona) {
		this.seleccionPersona = seleccionPersona;
	}

	public void setSeleccionDtSancion(DetallePuestoSancion seleccionDtSancion) {
		this.seleccionDtSancion = seleccionDtSancion;
	}

	public DetallePuesto getDetallePuesto() {
		return detallePuesto;
	}

	public void setDetallePuesto(DetallePuesto detallePuesto) {
		this.detallePuesto = detallePuesto;
	}

	public DetallePuestoSancion getDtSancion() {

		return dtSancion;
	}

	public void setDtSancion(DetallePuestoSancion dtSancion) {
		this.dtSancion = dtSancion;
	}

	public boolean isEsActualizacion() {
		return esActualizacion;
	}

	public void setEsActualizacion(boolean esActualizacion) {
		this.esActualizacion = esActualizacion;
	}

	public List<Sancion> getLstTipSanciones() {
		if (lstTipSanciones == null) {
			lstTipSanciones = srvSanciones.listarSancion();
		}
		return lstTipSanciones;
	}

	public void setLstTipSanciones(List<Sancion> lstTipSanciones) {
		this.lstTipSanciones = lstTipSanciones;
	}

	public String getTipoSancion() {
		return tipoSancion;
	}

	public void setTipoSancion(String tipoSancion) {
		this.tipoSancion = tipoSancion;
	}

	public Map<String, String> getTiposSanciones() {
		return tiposSanciones;
	}

	public void setTiposSanciones(Map<String, String> tiposSanciones) {
		this.tiposSanciones = tiposSanciones;
	}

	public TipoLicencia getTipoSancionEntidad() {
		return tipoSancionEntidad;
	}

	public void setTipoSancionEntidad(TipoLicencia tipoSancionEntidad) {
		this.tipoSancionEntidad = tipoSancionEntidad;
	}

	public Falta getFalta() {
		return falta;
	}

	public void setFalta(Falta falta) {
		this.falta = falta;
	}

	public Sancion getSancion() {
		return sancion;
	}

	public void setSancion(Sancion sancion) {
		this.sancion = sancion;
	}

	public List<Falta> getListaFaltas() {
		if (listaFaltas == null) {
			listaFaltas = srvSanciones.listarFalta();
		}
		return listaFaltas;
	}

	public void setListaFaltas(List<Falta> listaFaltas) {
		this.listaFaltas = listaFaltas;
	}

	public String getTipoFalta() {
		return tipoFalta;
	}

	public void setTipoFalta(String tipoFalta) {
		this.tipoFalta = tipoFalta;
	}

	public Map<String, String> getTipoFaltas() {
		return tipoFaltas;
	}

	public void setTipoFaltas(Map<String, String> tipoFaltas) {
		this.tipoFaltas = tipoFaltas;

	}

	public int getFaltaId() {
		return faltaId;
	}

	public void setFaltaId(int faltaId) {
		this.faltaId = faltaId;
	}

	public boolean isEsDescuento() {
		if (dtSancion.getDtpssnDescontar() == 1) {
			EsDescuento = true;
		} else {
			EsDescuento = false;
		}
		return EsDescuento;
	}

	public void setEsDescuento(boolean esDescuento) {
		EsDescuento = esDescuento;
	}

	public boolean isEsPorFrecuencia() {
		return esPorFrecuencia;
	}

	public void setEsPorFrecuencia(boolean esPorFrecuencia) {
		this.esPorFrecuencia = esPorFrecuencia;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public boolean isEsImprimir() {
		return esImprimir;
	}

	public void setEsImprimir(boolean esImprimir) {
		this.esImprimir = esImprimir;
	}

	public boolean isEsBloqueado() {
		return esBloqueado;
	}

	public void setEsBloqueado(boolean esBloqueado) {
		this.esBloqueado = esBloqueado;
	}

	public boolean isActivar() {
		return activar;
	}

	public void setActivar(boolean activar) {
		this.activar = activar;
	}

	public boolean isBtnRenderMulta() {
		return btnRenderMulta;
	}

	public void setBtnRenderMulta(boolean btnRenderMulta) {
		this.btnRenderMulta = btnRenderMulta;
	}
	
	

	public HashMap<String, String> getMeses() {
		return meses;
	}

	public void setMeses(HashMap<String, String> meses) {
		this.meses = meses;
	}

	/**
	 * Metodos
	 */

	public void limpiarFormSancion() {
		seleccionDtSancion = null;
		dtSancion = new DetallePuestoSancion();
		tipoSancion = "";
		tipoFalta = "";
		// categoriaFaltaAplicar=null;

	}

	/**
	 * Metodo paara generar el Numero De Autorizacion
	 * 
	 * @return
	 */
	public int generarNumAutorizacion(int tpSanId) {
		int numAutorizacion = 0;
		if (srvSanciones.MaximaNumAutorizacion(tpSanId) != 0) {
			numAutorizacion = srvSanciones.MaximaNumAutorizacion(tpSanId) + 1;
		} else {
			numAutorizacion = 1;
		}
		return numAutorizacion;
	}

	public void calcularSancion() {

		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		dtSancion.setDtpssnAno(c.get(Calendar.YEAR));
		dtSancion.setDtpssnMes(c.get(Calendar.MONTH) + 1);

		String txtDias = dtSancion.getDtpssnDias();

		valor = 0;
		sueldo = srvSanciones.ObtnerSueldoPorDetallePuestoId(seleccionPersona.getDtpsId());

		int min = dtSancion.getDtpssnMinutos();

		String[] Dias = txtDias.split(",");
		int frecuencia = Dias.length;

		falta = srvSanciones.ObtenerFaltaPorI(Integer.parseInt(tipoFalta));
		if (seleccionPersona.getCtgId() != 0) {
			categoriaFaltaAplicar = obtenerCategoriaFaltaPorParametros(seleccionPersona.getCtgId(), falta.getFlId(),
					min, frecuencia);
		}

		if (categoriaFaltaAplicar == null) {
			// if(Faltas.Atrasos.getId()==falta.getFlId()) {
			// categoriaFaltaAplicar=
			// obtenerCategoriaFaltaPorParametros(empleado.getCategoria().getCtgId(),
			// Faltas.AbandonodeTrabajo.getId() , min);
			//
			// }
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Información.", "No se ha paramatrizado estos rangos"));
			return;
		}
		dtSancion.setDtpssnFrecuencia(frecuencia);
		// int idTiposancion=categoriaFaltaAplicar.getTipoSancion().getTpsnId();

		sancion = obtenerSancionAplicar(categoriaFaltaAplicar.getSancion());
		if (sancion.getTipoSancion().getTpsnId() == 1) {
			if (VerificarSancionConsecutivoMuta(sancion, categoriaFaltaAplicar)) {
				Sancion sanAux = srvSanciones.obtenerSancionPorNivelPorTipoSancion(1, 2);
				sancion = obtenerSancionAplicar(sanAux);

			}
		}

		this.tipoSancion = String.valueOf(sancion.getSnId());
		calcularValores();

	}

	public void calcularValores() {
		if (sancion.getSnDescuento() != 0) {
			if (sancion.getSnPorcentaje() > 0) {
				valor = sancion.getSnPorcentaje();
			} else {
				if (esPorFrecuencia) {
					if (sancion.getSnPorcentaje() != 0) {
						valor = (dtSancion.getDtpssnFrecuencia() * sancion.getSnPorcentaje());
					} else {
						valor = (dtSancion.getDtpssnFrecuencia() * categoriaFaltaAplicar.getCtgflPorcentajeBase());
					}

				} else {
					if (sancion.getSnPorcentaje() != 0) {
						valor = (dtSancion.getDtpssnMinutos() * sancion.getSnPorcentaje());
					} else {
						valor = (dtSancion.getDtpssnMinutos() * categoriaFaltaAplicar.getCtgflPorcentajeBase());
					}
				}
			}
		}

		if (valor != 0) {
			valor = (valor / 100) * sueldo;
			EsDescuento = true;
		} else {
			EsDescuento = false;
		}

		dtSancion.setDtpssnValor(valor);
		int numAccion=generarNumAutorizacion(sancion.getTipoSancion().getTpsnId());
		dtSancion.setDtpssnNumaccion(numAccion);
		
	}

	public CategoriaFalta obtenerCategoriaFaltaPorParametros(int ctgId, int flId, int min, int frc) {
		CategoriaFalta categoriaFalta = null;
		List<CategoriaFalta> parametros = srvSanciones.listarcategoriaFaltaPorCategoriaIdFaltaId(ctgId, flId);
		for (CategoriaFalta s : parametros) {
			if (s.getCtgflMinuntosMin() != -1 && s.getCtgflMinutosMax() != -1) {

				if (s.getCtgflMinuntosMin() <= min && min <= s.getCtgflMinutosMax()) {
					categoriaFalta = s;
					esPorFrecuencia = false;
					break;
				}
			} else {

				if (s.getCtgflMinuntosMin() != -1 && s.getCtgflMinutosMax() == -1) {
					if (min >= s.getCtgflMinuntosMin()) {
						categoriaFalta = s;
						esPorFrecuencia = false;
						break;
					}
				} else {
					if (s.getCtgflFrecuenciaMin() >= 1 && s.getCtgflFrecuenciaMax() != -1) {
						if (s.getCtgflFrecuenciaMin() <= frc && s.getCtgflFrecuenciaMax() >= frc) {
							categoriaFalta = s;
							esPorFrecuencia = true;
							break;
						}
					} else {
						if ((s.getCtgflFrecuenciaMin() >= 1) && (s.getCtgflFrecuenciaMax() == -1)) {
							categoriaFalta = s;
							esPorFrecuencia = true;
							break;

						}

					}
				}

			}
		}
		return categoriaFalta;
	}

	public Sancion obtenerSancionAplicar(Sancion sancion) {
		Sancion retorno = null;
		int nivel = sancion.getSnNivel();
		int tpsn = 0;
		DetallePuestoSancion dtSanUlt = null;
		Sancion ultimaSancion = null;
		if (sancion.getTipoSancion().getTpsnId() != 1) {
			dtSanUlt = srvSanciones.obtenerUltimaSancion(seleccionPersona.getDtpsId(),
					sancion.getTipoSancion().getTpsnId(), dtSancion.getDtpssnAno(), dtSancion.getDtpssnMes());
		} else {
			dtSanUlt = srvSanciones.obtenerUltimaSancionPorTpSancionFaltaId(seleccionPersona.getDtpsId(),
					sancion.getTipoSancion().getTpsnId(), categoriaFaltaAplicar.getFalta().getFlId());
		}
		if (dtSanUlt != null) {
			ultimaSancion = dtSanUlt.getSancion();
			tpsn = ultimaSancion.getTipoSancion().getTpsnId();
		}

		if (ultimaSancion == null) {
			retorno = sancion;
		} else {
			if (tpsn == 2) {
				if (calcularTiempoUltimaSancion(dtSancion, dtSanUlt)) {

					int ulnivel = ultimaSancion.getSnNivel();
					if (ulnivel >= nivel) {
						if (ulnivel == 6) {
							retorno = srvSanciones.obtenerSancionPorNivelPorTipoSancion(ulnivel, tpsn);
						} else {
							ulnivel = ulnivel + 1;
							retorno = srvSanciones.obtenerSancionPorNivelPorTipoSancion(ulnivel, tpsn);

						}

					} else {
						retorno = sancion;
					}

				} else {
					retorno = sancion;
				}
			} else {

				retorno = sancion;
			}
		}

		return retorno;

	}

	public boolean VerificarSancionConsecutivoMuta(Sancion sancion, CategoriaFalta categoriaFal) {
		boolean retorno = false;
		Calendar fs = Calendar.getInstance();
		fs.set(dtSancion.getDtpssnAno(), dtSancion.getDtpssnMes() - 1, 1);
		for (int i = 1; i <= 2; i++) {
			fs.add(Calendar.MONTH, -1);
			DetallePuestoSancion detSan = srvSanciones.obtenerSancionPorMesAnio(seleccionPersona.getDtpsId(),
					categoriaFaltaAplicar.getCtgflId(), fs.get(Calendar.MONTH) + 1, dtSancion.getDtpssnAno(),
					sancion.getTipoSancion().getTpsnId());
			if (detSan == null) {
				retorno = false;
				break;
			} else {
				retorno = true;
			}
		}

		return retorno;

	}

	public void cargarSancionFormEdit(DetallePuestoSancion seleccionDtSancion) {

		if (seleccionDtSancion.getDtpssnDescontar() == 1) {

			EsDescuento = true;
		} else {
			EsDescuento = false;
		}
		if (seleccionDtSancion.getDtpssnEstado() == 3) {
			esBloqueado = true;
		} else {
			esBloqueado = false;
		}
		Calendar fedit = Calendar.getInstance();
		fedit.set(seleccionDtSancion.getDtpssnAno(), seleccionDtSancion.getDtpssnMes(), 1);
		fecha = fedit.getTime();

		falta = seleccionDtSancion.getCategoriaFalta().getFalta();
		categoriaFaltaAplicar = seleccionDtSancion.getCategoriaFalta();
		sancion = seleccionDtSancion.getSancion();
		tipoFalta = String.valueOf(falta.getFlId());
		tipoSancion = String.valueOf(sancion.getSnId());
		activar = false;
		esImprimir = true;

	}

	public void guardarSancion() {

		if (EsDescuento) {
			dtSancion.setDtpssnDescontar(1);
		} else {

			dtSancion.setDtpssnDescontar(0);
		}

		dtSancion.setCategoriaFalta(categoriaFaltaAplicar);
		dtSancion.setSancion(sancion);

		if (esActualizacion) {

			DetallePuestoSancion retorno = srvSanciones.actualizarSancion(dtSancion);
			if (retorno != null) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Información.", "Sanción Actualizada Existosamente"));

			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Información.", "Error al Actualizar la Sanción"));
				return;
			}

		} else {
			dtSancion.setDtpsId(seleccionPersona.getDtpsId());
			dtSancion.setDtpssnEstado(Estados.Activo.getId());
			if (srvSanciones.insertaSancion(dtSancion)) {
				if (dtSancion.getSancion().getSnId() == 1) {
					System.out.println(meses.get(String.valueOf(this.dtSancion.getDtpssnMes())));
					esImprimir = false;
					btnRenderMulta = true;
					this.listaMultas = srvSanciones.listarSancionMultasPorDetallePuestoId(
							this.seleccionPersona.getDtpsId(), this.dtSancion.getDtpssnMes(),
							this.dtSancion.getDtpssnAno());
					this.mesReporte = String.valueOf(this.dtSancion.getDtpssnMes());
					this.anioReporte = String.valueOf(this.dtSancion.getDtpssnAno());
					for(int i = 0; i < this.listaMultas.size(); i++){
						valoTotalMultas = valoTotalMultas + this.listaMultas.get(i).getDtpssnValor();
					}
					
				} else {
					esImprimir = true;
					btnRenderMulta = false;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Información.", "Sanción Registrada Existosamente"));
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Información.", "Error al Insertar la Sanción"));
				return;
			}

		}

	}

	public void anularSancion() {
		dtSancion.setDtpssnEstado(Estados.Anulado.getId());
		guardarSancion();
		esBloqueado = true;
	}

	public String regresar() {
		String retorno = "";
		retorno = "/controlAsistencia/sanciones/SancionesRegistros.xhtml";
		this.esImprimir = false;
		this.btnRenderMulta = false;
		this.listaMultas.clear();
		this.valoTotalMultas = 0;
		return retorno;

	}

	public void verPDF() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("txt_num_auto", String.valueOf(dtSancion.getDtpssnNumaccion()));
			parametros.put("txt_nombres", seleccionPersona.nombresCompetos());
			parametros.put("txt_dependencia", seleccionPersona.getDpnNombre());
			parametros.put("txt_cedula", seleccionPersona.getPrsIdentificacion());
			parametros.put("txt_explicacion", dtSancion.getDtpssnObservacion());
			parametros.put("txt_puesto", seleccionPersona.getPstNombre());
			String sueldoAux = String.valueOf(sueldo);
			parametros.put("txt_renumeracion", sueldoAux);
			parametros.put("txt_tipo_falta", dtSancion.getCategoriaFalta().getFalta().getFlNombre());
			parametros.put("txt_sancion", dtSancion.getSancion().getSnDescripcion());
			String valorAux = String.valueOf(valor);
			parametros.put("txt_valor", valorAux);
			parametros.put("txt_partida", "1234567890000000000");
			parametros.put("txt_individual", "123456");
			// parametros.put("txt_remuneracion",
			// detallePuesto.getFichaEmpleado().);
			// parametros.put("txt_partida",
			// String.valueOf(salVacaCal1.getSlvcDiasRestantes()));
			// parametros.put("txt_individual",
			// String.valueOf(salVacaCal2.getSlvcDiasRestantes()));

			File jasper = new File(FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/controlAsistencia/reportes/sanciones.jasper"));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros);

			InputStream rptStream = FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/controlAsistencia/reportes/sanciones.jasper");
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			response.addHeader("Content-disposition",
					"attachment; filename=ACCION_DE_PERSONAL_Nº" + dtSancion.getDtpssnNumaccion() + "_"
							+ seleccionPersona.nombresCompetos() + "_" + sdf.format(new Date()).toString() + ".pdf");
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

	public boolean calcularTiempoUltimaSancion(DetallePuestoSancion dtSancionNueva,
			DetallePuestoSancion dtSancionAnterior) {

		String diasNuevo = dtSancionNueva.getDtpssnDias();
		String diasAnterior = dtSancionAnterior.getDtpssnDias();

		String[] dias1 = diasNuevo.split(",");
		String[] dias2 = diasAnterior.split(",");

		int n2 = dias2.length;

		int ultimoDiaNuevo = Integer.parseInt(dias1[0]);
		int ultimoDiaAnterior = Integer.parseInt(dias2[n2 - 1]);

		int mesNuevo = dtSancionNueva.getDtpssnMes() - 1;
		int mesAnterior = dtSancionAnterior.getDtpssnMes() - 1;

		int anioNuevo = dtSancionNueva.getDtpssnAno();
		int anioAnterior = dtSancionAnterior.getDtpssnAno();

		boolean retorno = false;
		Calendar fecAct = Calendar.getInstance();

		fecAct.set(anioNuevo, mesNuevo, ultimoDiaNuevo);

		Calendar fUltSan = Calendar.getInstance();
		fUltSan.set(anioAnterior, mesAnterior, ultimoDiaAnterior);

		int monthDay[] = { 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int anios = anioNuevo - anioAnterior;
		int Meses;
		int Dias;

		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();

		Calendar sanAhora = Calendar.getInstance();
		sanAhora.setTime(fUltSan.getTime());
		sanAhora.add(Calendar.YEAR, anios);

		if (fecAct.compareTo(sanAhora) < 0)
			anios--;

		if (anios >= 1) {
			retorno = false;
		} else {
			retorno = true;
		}

		return retorno;
	}

	public String regresarEmpleado() {
		String retorno = "";
		retorno = "/controlAsistencia/empleado/busquedaEmpleadoSancion.xhtml";
		return retorno;
	}

	public void activarDialogoAnular() {
		activar = true;
	}

	public void cargarVariables(DetallePuestoSancion dt) {
		seleccionDtSancion = dt;
		if (seleccionDtSancion != null) {
			dtSancion = seleccionDtSancion;
			cargarSancionFormEdit(seleccionDtSancion);
			esActualizacion = true;
		} else {

			esActualizacion = false;
			limpiarFormSancion();
			Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());
			dtSancion.setDtpssnFechaEmision(fechaEmision);
			dtSancion.setDtpssnDescontar(0);
			esBloqueado = false;
		}

	}

	public void calcularSancionDefinido() {
		valor = 0;
		int snId = Integer.parseInt(tipoSancion);
		sancion = srvSanciones.ObtenerSancionPorId(snId);
		calcularValores();

	}
	public String obtenerMesTexto(int m) {
		String  retorno="";
		String mes=String.valueOf(m);
		retorno=meses.get(mes);
		return retorno;
		
	}

	public void generarReporteMultas() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

			List<ReporteMulta> multaModel = new ArrayList<>();
			for (DetallePuestoSancion dtpSancionEach : this.listaMultas) {
				ReporteMulta detalle = new ReporteMulta();
				detalle.setFalta(dtpSancionEach.getCategoriaFalta().getFalta().getFlNombre());
				detalle.setFrecuencia(dtpSancionEach.getDtpssnFrecuencia());
				detalle.setFechas(dtpSancionEach.getDtpssnDias());
				detalle.setObservaciones(dtpSancionEach.getDtpssnObservacion());
				multaModel.add(detalle);
			}
			
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("txt_nombres", seleccionPersona.nombresCompetos());
			parametros.put("txt_dependencia", seleccionPersona.getDpnNombre());
			parametros.put("txt_anio", this.anioReporte);
			parametros.put("txt_mes", this.meses.get(this.mesReporte));
			parametros.put("txt_cargo", seleccionPersona.getPstNombre());
			parametros.put("txt_valor", String.valueOf(this.valoTotalMultas));

			File jasper = new File(FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/controlAsistencia/reportes/rptMultas.jasper"));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(), parametros,
					new JRBeanCollectionDataSource(multaModel));

			InputStream rptStream = FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/controlAsistencia/reportes/rptMultas.jasper");
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			response.addHeader("Content-disposition", "attachment; filename=APLICACION_DE_MULTAS" + "_"
					+ seleccionPersona.nombresCompetos() + "_" + sdf.format(new Date()).toString() + ".pdf");
			ServletOutputStream stream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
			JasperRunManager.runReportToPdfStream(rptStream, stream, parametros,
					new JRBeanCollectionDataSource(multaModel));
			stream.flush();
			stream.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
