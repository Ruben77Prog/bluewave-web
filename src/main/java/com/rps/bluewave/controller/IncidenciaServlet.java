package com.rps.bluewave.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.ruben.bluewave.dao.criteria.IncidenciaCriteria;
import com.ruben.bluewave.model.Cliente;
import com.ruben.bluewave.model.ContratoDTO;
import com.ruben.bluewave.model.EmpleadoDTO;
import com.ruben.bluewave.model.IncidenciaDTO;
import com.ruben.bluewave.model.Results;
import com.ruben.bluewave.service.ContratoService;
import com.ruben.bluewave.service.IncidenciaService;
import com.ruben.bluewave.service.impl.ContratoServiceImpl;
import com.ruben.bluewave.service.impl.IncidenciaServiceImpl;
import com.rps.bluewave.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/incidencia")
public class IncidenciaServlet extends HttpServlet {
	private final IncidenciaService incidenciaService = new IncidenciaServiceImpl();
	private final ContratoService contratoService = new ContratoServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		Object usuario = request.getSession().getAttribute("usuario");
		boolean admin = isAdmin(usuario);
		if (!(usuario instanceof Cliente) && !admin) {
			response.sendRedirect(request.getContextPath() + Views.CLIENTE_LOGIN);
			return;
		}
		Cliente cliente = usuario instanceof Cliente ? (Cliente) usuario : null;
		request.setAttribute("admin", admin);

		String action = request.getParameter("action");
		if ("detail".equalsIgnoreCase(action)) {
			try {
				IncidenciaCriteria criteria = new IncidenciaCriteria();
				criteria.setId(Long.valueOf(request.getParameter("id")));
				if (cliente != null) criteria.setClienteNombre(cliente.getNombre());
				Results<IncidenciaDTO> matches = incidenciaService.findByCriteria(criteria, 0, 1);
				IncidenciaDTO incidencia = matches.getPage().isEmpty() ? null : matches.getPage().get(0);
				if (incidencia == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				request.setAttribute("incidencia", incidencia);
				request.getRequestDispatcher(Views.INCIDENCIA_DETAIL).forward(request, response);
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se ha podido cargar la incidencia.");
			}
			return;
		}

		if (action == null || "search".equalsIgnoreCase(action)) {
			IncidenciaCriteria criteria = new IncidenciaCriteria();
			if (cliente != null) {
				criteria.setClienteNombre(cliente.getNombre());
			} else {
				criteria.setTipoIncidenciaId(parseLong(request.getParameter("tipoIncidenciaId")));
				criteria.setEstadoIncidenciaId(parseLong(request.getParameter("estadoIncidenciaId")));
			}
			criteria.setNumeroIncidencia(request.getParameter("numero"));
			criteria.setTitulo(request.getParameter("titulo"));
			try {
				criteria.setFechaDesde(parseDate(request.getParameter("desde"), false));
				criteria.setFechaHasta(parseDate(request.getParameter("hasta"), true));
				Results<IncidenciaDTO> results;
				Long clienteId = admin ? parseLong(request.getParameter("clienteId")) : null;
				if (clienteId == null) {
					results = incidenciaService.findByCriteria(criteria, 0, 100);
				} else {
					results = findByClient(criteria, clienteId);
				}
				request.setAttribute("results", results);
			} catch (Exception e) {
			request.setAttribute("error", "No se han podido obtener las incidencias. Revisa los filtros introducidos.");
			}
			request.getRequestDispatcher(Views.INCIDENCIA_SEARCH).forward(request, response);
			return;
		}

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private Date parseDate(String value, boolean endOfDay) {
		if (value == null || value.isBlank()) return null;
		LocalDate date = LocalDate.parse(value);
		return Date.from((endOfDay ? date.plusDays(1).atStartOfDay().minusNanos(1) : date.atStartOfDay())
				.atZone(ZoneId.systemDefault()).toInstant());
	}

	private Results<IncidenciaDTO> findByClient(IncidenciaCriteria criteria, Long clienteId) throws Exception {
		Results<IncidenciaDTO> combined = new Results<>();
		List<IncidenciaDTO> page = new ArrayList<>();
		List<ContratoDTO> contracts = contratoService.findByCliente(clienteId);
		for (ContratoDTO contract : contracts) {
			criteria.setContratoId(contract.getId());
			Results<IncidenciaDTO> matches = incidenciaService.findByCriteria(criteria, 0, 100);
			page.addAll(matches.getPage());
			combined.setTotal(combined.getTotal() + matches.getTotal());
		}
		combined.setPage(page);
		return combined;
	}

	private Long parseLong(String value) {
		return value == null || value.isBlank() ? null : Long.valueOf(value);
	}

	private boolean isAdmin(Object usuario) {
		if (!(usuario instanceof EmpleadoDTO)) return false;
		String rol = ((EmpleadoDTO) usuario).getRolNombre();
		return rol != null && rol.toLowerCase().contains("admin");
	}
}
