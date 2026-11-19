package com.rps.bluewave.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.ruben.bluewave.model.Rol;
import com.ruben.bluewave.service.impl.RolServiceImpl;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/roles-login")
public class RolServlet extends HttpServlet {
	private final RolServiceImpl rolService = new RolServiceImpl();
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			response.setContentType("application/json");
			List<Rol> rolesActivos = new ArrayList<>();
			for (Rol rol : rolService.findAll()) {
				if (!Boolean.FALSE.equals(rol.getActivo())) {
					rolesActivos.add(rol);
				}
			}
			String json = gson.toJson(rolesActivos);
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
