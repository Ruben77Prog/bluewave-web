package com.rps.bluewave.web.service;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.ruben.bluewave.service.impl.TipoIncidenciaServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tipos-incidencia")
public class TipoIncidenciaServlet extends HttpServlet {
	private final TipoIncidenciaServiceImpl service = new TipoIncidenciaServiceImpl();
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		try {
			String json = gson.toJson(service.findAll());
			OutputStream os = response.getOutputStream();
			os.write(json.getBytes());
			os.flush();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			OutputStream os = response.getOutputStream();
			os.write("[]".getBytes());
			os.flush();
		}
	}
}
