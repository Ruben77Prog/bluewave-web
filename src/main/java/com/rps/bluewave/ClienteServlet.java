package com.rps.bluewave;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.ruben.bluewave.model.Cliente;
import com.ruben.bluewave.model.ContentData;
import com.ruben.bluewave.model.ContentMetadata;
import com.ruben.bluewave.service.ClienteService;
import com.ruben.bluewave.service.ContentService;
import com.ruben.bluewave.service.impl.ClienteServiceImpl;
import com.ruben.bluewave.service.impl.ContentServiceFileImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/Cliente")
@MultipartConfig(fileSizeThreshold = 2 * 1024 * 1024, maxFileSize = 10 * 1024 * 1024,
		maxRequestSize = 12 * 1024 * 1024)
public class ClienteServlet extends HttpServlet {

	private ClienteService clienteService = null;
	private ContentService contentService = null;

	public ClienteServlet() {
		super();
		clienteService = new ClienteServiceImpl();
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

			try {

				Cliente cliente = new Cliente();
				cliente.setEmail(email);
				cliente.setContrasena(password);

				Cliente clienteEncontrado = clienteService.login(cliente);

				if (clienteEncontrado != null) {

					if (session != null) session.invalidate();
					// Creamos una sesión nueva después de validar las credenciales.
					session = request.getSession();
					session.setAttribute("usuario", clienteEncontrado);

					// Redirigimos a la página de incidencias
					response.sendRedirect(request.getContextPath() + "/incidencia?action=search");

				} else {

					// Login incorrecto
					request.setAttribute("error", "Email o contrasena incorrectos.");

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
				cliente.setEmail(request.getParameter("email"));
				cliente.setContrasena(request.getParameter("password"));
				if (cliente.getNombre() == null || cliente.getNombre().isBlank()
						|| cliente.getEmail() == null || cliente.getEmail().isBlank()
						|| cliente.getContrasena() == null || cliente.getContrasena().length() < 8) {
					throw new IllegalArgumentException("Completa nombre y correo; la contrasena debe tener al menos 8 caracteres.");
				}
				if (!clienteService.findByEmail(cliente.getEmail()).isEmpty()) {
					throw new IllegalArgumentException("Ya existe una cuenta con ese correo.");
				}
				Long id = clienteService.create(cliente);
				if (id == null) throw new Exception("No se pudo crear la cuenta.");
				cliente.setId(id);

				Part imagen = request.getPart("imagen_perfil");
				String extension = null;
				if (imagen != null && imagen.getSize() > 0) {
					extension = saveProfileImage(imagen, cliente);
				}
				session = request.getSession();
				session.setAttribute("usuario", cliente);
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
		String contentType = image.getContentType();
		String extension = extensionFor(contentType);
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

}
