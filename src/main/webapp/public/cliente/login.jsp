<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp"%>
<% String error = (String) request.getAttribute("error"); String message = (String) request.getAttribute("message"); %>
<div class="page-title"><h1>Iniciar sesion</h1><p>Accede a tu cuenta de Bluewave.</p></div>
<div class="profile-card">
<% if (error != null) { %><p class="error-message"><%=error%></p><% } %>
<% if (message != null) { %><p class="success-message"><%=message%></p><% } %>
<form action="<%=request.getContextPath()%>/Cliente?action=login" method="post" accept-charset="UTF-8">
<label for="rol">Tipo de cuenta</label><select id="rol" name="rol" required><option value="">Cargando roles...</option></select>
<label for="email">Correo electronico</label><input id="email" name="email" type="email" required>
<label for="password">Contrasena</label><input id="password" name="password" type="password" required>
<button class="btn" type="submit">Entrar</button>
</form><p><a href="<%=request.getContextPath()%>/Cliente?action=register">Crear cuenta</a></p></div>
<script>
$(function () {
  $.ajax({
    url: '<%=request.getContextPath()%>/roles-login',
    type: 'GET',
    dataType: 'json',
    success: function (roles) {
      const select = $('#rol').empty().append($('<option>', { value: '', text: 'Selecciona tu tipo de cuenta' }));
      select.append($('<option>', { value: 'cliente', text: 'Cliente' }));
      $.each(roles, function (_, role) {
        select.append($('<option>', { value: 'empleado:' + role.id, text: role.nombre }));
      });
    },
    error: function (xhr, status, error) {
      console.error('Error AJAX:', status, error);
      $('#rol').html('<option value="">No se pudieron cargar los roles</option>');
    }
  });
});
</script>
<%@ include file="/WEB-INF/jsp/common/footer.jsp"%>
