package com.ait.deliveri.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ait.deliveri")
public class GlobalException {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("codigo", 400, "mensaje", ex.getMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFound(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("codigo", 404, "mensaje", ex.getMessage()));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handleGeneral(RuntimeException ex) {
		if (ex instanceof AccessDeniedException ade) throw ade;
        if (ex instanceof AuthenticationException ae) throw ae;
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("codigo", 500, "mensaje", "Ocurrio un error en el sistema, intentelo mas tarde."));
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public void handleAccessDenied(AccessDeniedException ex) throws Exception {
	    throw ex;
	}
}
