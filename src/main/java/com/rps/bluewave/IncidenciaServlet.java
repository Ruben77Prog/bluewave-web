package com.rps.bluewave;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.ruben.bluewave.dao.criteria.IncidenciaCriteria;
import com.ruben.bluewave.model.Cliente;
import com.ruben.bluewave.model.IncidenciaDTO;
import com.ruben.bluewave.model.Results;
import com.ruben.bluewave.service.IncidenciaService;
import com.ruben.bluewave.service.impl.IncidenciaServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/incidencia")
public class IncidenciaServlet extends HttpServlet {
	private final IncidenciaService incidenciaService = new IncidenciaServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		Cliente cliente = (Cliente) request.getSession().getAttribute("usuario");
		if (cliente == null) {
			response.sendRedirect(request.getContextPath() + Views.CLIENTE_LOGIN);
			return;
		}

		String action = request.getParameter("action");
		if ("detail".equalsIgnoreCase(action)) {
			try {
				IncidenciaCriteria criteria = new IncidenciaCriteria();
				criteria.setId(Long.valueOf(request.getParameter("id")));
				criteria.setClienteNombre(cliente.getNombre());
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
			criteria.setClienteNombre(cliente.getNombre());
			criteria.setNumeroIncidencia(request.getParameter("numero"));
			criteria.setTitulo(request.getParameter("titulo"));
			try {
				criteria.setFechaDesde(parseDate(request.getParameter("desde"), false));
				criteria.setFechaHasta(parseDate(request.getParameter("hasta"), true));
				Results<IncidenciaDTO> results = incidenciaService.findByCriteria(criteria, 0, 100);
				request.setAttribute("results", results);
			} catch (Exception e) {
			request.setAttribute("error", "No se han podido obtener las incidencias. Revisa las fechas introducidas.");
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
}
