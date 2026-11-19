package com.rps.bluewave.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.google.gson.Gson;
import com.ruben.bluewave.model.Ciudad;
import com.ruben.bluewave.service.impl.CiudadServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/localidad")
public class LocalidadServlet extends HttpServlet {
	private final CiudadServiceImpl ciudadService = new CiudadServiceImpl();
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		try {
			Long provinciaId = Long.valueOf(request.getParameter("provinceId"));
			List<Ciudad> ciudades = ciudadService.findByProvinciaId(provinciaId);
			String json = gson.toJson(ciudades);
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
