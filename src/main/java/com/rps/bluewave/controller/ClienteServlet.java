package com.rps.bluewave.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.ruben.bluewave.model.Cliente;
import com.ruben.bluewave.model.ContentData;
import com.ruben.bluewave.model.ContentMetadata;
import com.ruben.bluewave.model.Direccion;
import com.ruben.bluewave.model.EmpleadoDTO;
import com.ruben.bluewave.service.ClienteService;
import com.ruben.bluewave.service.ContentService;
import com.ruben.bluewave.service.DireccionService;
import com.ruben.bluewave.service.EmpleadoService;
import com.ruben.bluewave.service.impl.ClienteServiceImpl;
import com.ruben.bluewave.service.impl.ContentServiceFileImpl;
import com.ruben.bluewave.service.impl.DireccionServiceImpl;
import com.ruben.bluewave.service.impl.EmpleadoServiceImpl;
import com.rps.bluewave.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/Cliente")
@MultipartConfig(fileSizeThreshold = 2 * 1024 * 1024, maxFileSize = 5 * 1024 * 1024,
		maxRequestSize = 6 * 1024 * 1024)
public class ClienteServlet extends HttpServlet {

	private ClienteService clienteService = null;
	private EmpleadoService empleadoService = null;
	private DireccionService direccionService = null;
	private ContentService contentService = null;

	public ClienteServlet() {
		super();
		clienteService = new ClienteServiceImpl();
		empleadoService = new EmpleadoServiceImpl();
		direccionService = new DireccionServiceImpl();
		contentService = new ContentServiceFileImpl();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);

		if ("profile".equalsIgnoreCase(action)) {
			if (session == null || !(session.getAttribute("usuario") instanceof Cliente)) {
				response.sendRedirect(request.getContextPath() + "/incidencia?action=search");
				return;
			}
			showOrUpdateProfile(request, response, session);
			return;
		}

		if ("photo".equalsIgnoreCase(action)) {
			showPhoto(request, response, session);
			return;
		}

		if ("login".equalsIgnoreCase(action)) {
			if (!"POST".equalsIgnoreCase(request.getMethod())) {
				request.getRequestDispatcher(Views.CLIENTE_LOGIN).forward(request, response);
				return;
			}

			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String rolSeleccionado = request.getParameter("rol");

			try {
				Object usuarioAutenticado = null;
				String nombreRol = null;
				if ("cliente".equals(rolSeleccionado)) {
					Cliente cliente = new Cliente();
					cliente.setEmail(email);
					cliente.setContrasena(password);
					usuarioAutenticado = clienteService.login(cliente);
					nombreRol = "Cliente";
				} else if (rolSeleccionado != null && rolSeleccionado.startsWith("empleado:")) {
					Long rolId = Long.valueOf(rolSeleccionado.substring("empleado:".length()));
					EmpleadoDTO empleado = empleadoService.login(email, password);
					if (empleado != null && Boolean.TRUE.equals(empleado.getActivo())
							&& rolId.equals(empleado.getRolId())) {
						usuarioAutenticado = empleado;
						nombreRol = empleado.getRolNombre();
					}
				}

				if (usuarioAutenticado != null) {
					if (session != null) session.invalidate();
					session = request.getSession();
					session.setAttribute("usuario", usuarioAutenticado);
					session.setAttribute("rolUsuario", nombreRol);
					response.sendRedirect(request.getContextPath() + "/incidencia?action=search");
				} else {
					request.setAttribute("error", "Correo, contrasena o tipo de cuenta incorrectos.");
					request.getRequestDispatcher(Views.CLIENTE_LOGIN).forward(request, response);
				}

			} catch (Exception e) {

				e.printStackTrace();

				request.setAttribute("error", "Se ha producido un error durante el login.");

				request.getRequestDispatcher(Views.CLIENTE_LOGIN).forward(request, response);
			}

		} else if ("register".equalsIgnoreCase(action)) {
			if (!"POST".equalsIgnoreCase(request.getMethod())) {
				request.getRequestDispatcher(Views.CLIENTE_REGISTER).forward(request, response);
				return;
			}
			try {
				Cliente cliente = new Cliente();
				cliente.setNombre(request.getParameter("nombre"));
				cliente.setApellido1(request.getParameter("apellido1"));
				cliente.setApellido2(request.getParameter("apellido2"));
				cliente.setDni(request.getParameter("dni"));
				cliente.setTelefono(request.getParameter("telefono"));
				cliente.setEmail(request.getParameter("email"));
				cliente.setContrasena(request.getParameter("password"));
				cliente.setFechaNacimiento(java.sql.Date.valueOf(request.getParameter("fechaNacimiento")));
				cliente.setGeneroId(Long.valueOf(request.getParameter("generoId")));
				if (cliente.getNombre() == null || cliente.getNombre().isBlank()
						|| cliente.getApellido1() == null || cliente.getApellido1().isBlank()
						|| cliente.getDni() == null || cliente.getDni().isBlank()
						|| cliente.getTelefono() == null || cliente.getTelefono().isBlank()
						|| cliente.getEmail() == null || cliente.getEmail().isBlank()
						|| cliente.getContrasena() == null || cliente.getContrasena().length() < 8) {
					throw new IllegalArgumentException("Completa los campos obligatorios y usa una contrasena de al menos 8 caracteres.");
				}
				if (!clienteService.findByEmail(cliente.getEmail()).isEmpty()) {
					throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");
				}

				Direccion direccion = new Direccion();
				direccion.setCalle(request.getParameter("calle"));
				direccion.setNumero(request.getParameter("numero"));
				direccion.setPiso(request.getParameter("piso"));
				direccion.setPuerta(request.getParameter("puerta"));
				direccion.setCodigoPostal(request.getParameter("codigoPostal"));
				direccion.setCiudadId(Long.valueOf(request.getParameter("ciudadId")));
				Direccion direccionCreada = direccionService.create(direccion);
				if (direccionCreada == null || direccionCreada.getId() == null) {
					throw new Exception("No se pudo guardar la direccion.");
				}
				cliente.setDireccionId(direccionCreada.getId());
				Long id = clienteService.create(cliente);
				if (id == null) throw new Exception("No se pudo crear la cuenta.");
				cliente.setId(id);

				String extension = null;
				Part imagen = request.getPart("imagen_perfil");
				if (imagen != null && imagen.getSize() > 0) {
					extension = saveProfileImage(imagen, cliente);
				}
				session = request.getSession();
				session.setAttribute("usuario", cliente);
				session.setAttribute("rolUsuario", "Cliente");
				if (extension != null) session.setAttribute("fotoFormato", extension);
				response.sendRedirect(request.getContextPath() + "/incidencia?action=search");
			} catch (Exception e) {
				request.setAttribute("error", e.getMessage());
				request.getRequestDispatcher(Views.CLIENTE_REGISTER).forward(request, response);
			}

		} else if ("logout".equalsIgnoreCase(action)) {

			// Recuperamos la sesión existente
			if (session != null) {

				session.invalidate();
			}

			response.sendRedirect(request.getContextPath() + Views.CLIENTE_LOGIN);

		} else {

			if (session != null && session.getAttribute("usuario") != null) {

				response.sendRedirect(request.getContextPath() + "/incidencia?action=search");

			} else {

				request.getRequestDispatcher(Views.CLIENTE_LOGIN).forward(request, response);
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	private void showOrUpdateProfile(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws ServletException, IOException {
		if (session == null || session.getAttribute("usuario") == null) {
			response.sendRedirect(request.getContextPath() + Views.CLIENTE_LOGIN);
			return;
		}

		Cliente cliente = (Cliente) session.getAttribute("usuario");
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			try {
				cliente.setNombre(request.getParameter("nombre"));
				cliente.setApellido1(request.getParameter("apellido1"));
				cliente.setApellido2(request.getParameter("apellido2"));
				cliente.setTelefono(request.getParameter("telefono"));

				if (!clienteService.update(cliente)) {
					request.setAttribute("error", "No se han podido actualizar los datos del cliente.");
				} else {
					session.setAttribute("usuario", cliente);
					Part imagen = request.getPart("imagen_perfil");
					if (imagen != null && imagen.getSize() > 0) {
						session.setAttribute("fotoFormato", saveProfileImage(imagen, cliente));
					}
					request.setAttribute("message", "Datos del cliente actualizados correctamente.");
				}
			} catch (Exception e) {
				request.setAttribute("error", "No se ha podido guardar el perfil: " + e.getMessage());
			}
		}

		request.setAttribute("cliente", cliente);
		if (session.getAttribute("fotoFormato") == null) {
			String storedFormat = findProfileImageFormat(cliente);
			if (storedFormat != null) session.setAttribute("fotoFormato", storedFormat);
		}
		request.getRequestDispatcher(Views.CLIENTE_PROFILE).forward(request, response);
	}

	private String findProfileImageFormat(Cliente cliente) {
		for (String extension : new String[] { ".jpg", ".png", ".gif", ".webp" }) {
			try {
				ContentMetadata metadata = new ContentMetadata();
				metadata.setEntityType("cliente");
				metadata.setEntityId(String.valueOf(cliente.getId()));
				metadata.setResourceId("foto-perfil");
				metadata.setFormat(extension);
				contentService.get(metadata);
				return extension;
			} catch (Exception e) {
				// La siguiente extensión puede ser la usada al guardar la foto.
			}
		}
		return null;
	}

	private String saveProfileImage(Part image, Cliente cliente) throws Exception {
		String extension = extensionFor(image.getContentType());
		if (extension == null) {
			throw new IllegalArgumentException("Solo se permiten imagenes JPG, PNG, GIF o WEBP.");
		}

		ContentMetadata metadata = new ContentMetadata();
		metadata.setEntityType("cliente");
		metadata.setEntityId(String.valueOf(cliente.getId()));
		metadata.setResourceId("foto-perfil");
		metadata.setFormat(extension);

		ContentData data = new ContentData();
		data.setMetadata(metadata);
		try (InputStream input = image.getInputStream()) {
			data.setRaw(input.readAllBytes());
		}
		contentService.save(data);
		return extension;
	}

	private void showPhoto(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		if (session == null || session.getAttribute("usuario") == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Cliente cliente = (Cliente) session.getAttribute("usuario");
		String extension = (String) session.getAttribute("fotoFormato");
		for (String candidate : new String[] { extension, ".jpg", ".png", ".gif", ".webp" }) {
			if (candidate == null) {
				continue;
			}
			try {
				ContentMetadata metadata = new ContentMetadata();
				metadata.setEntityType("cliente");
				metadata.setEntityId(String.valueOf(cliente.getId()));
				metadata.setResourceId("foto-perfil");
				metadata.setFormat(candidate);
				ContentData data = contentService.get(metadata);
				response.setContentType(contentTypeFor(candidate));
				response.setHeader("X-Content-Type-Options", "nosniff");
				if ("true".equalsIgnoreCase(request.getParameter("download"))) {
					response.setHeader("Content-Disposition", "attachment; filename=perfil" + candidate);
				}
				response.getOutputStream().write(data.getRaw());
				return;
			} catch (Exception e) {
				// Probamos la siguiente extensión admitida para recuperar imágenes de sesiones anteriores.
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private String contentTypeFor(String extension) {
		switch (extension) {
		case ".png":
			return "image/png";
		case ".gif":
			return "image/gif";
		case ".webp":
			return "image/webp";
		default:
			return "image/jpeg";
		}
	}

	private String extensionFor(String contentType) {
		if (contentType == null) {
			return null;
		}
		switch (contentType.toLowerCase(Locale.ROOT)) {
		case "image/jpeg":
			return ".jpg";
		case "image/png":
			return ".png";
		case "image/gif":
			return ".gif";
		case "image/webp":
			return ".webp";
		default:
			return null;
		}
	}

}
