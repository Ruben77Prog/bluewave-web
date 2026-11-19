package com.rps.bluewave.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.google.gson.Gson;
import com.ruben.bluewave.model.Provincia;
import com.ruben.bluewave.service.impl.ProvinciaServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/provincia")
public class ProvinciaServlet extends HttpServlet {
	private final ProvinciaServiceImpl provinciaService = new ProvinciaServiceImpl();
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		try {
			Long paisId = Long.valueOf(request.getParameter("id-pais"));
			List<Provincia> provincias = provinciaService.findByPais(paisId);
			String json = gson.toJson(provincias);
			OutputStream os = response.getOutputStream();
			os.write(json.getBytes());
			os.flush();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			OutputStream os = response.getOutputStream();
			os.write("[]".getBytes());
			os.flush();
		}
	}
}
