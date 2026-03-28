package com.ait.deliveri.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

public final class FileUtils {

	private FileUtils() {
		
	}
	
	public static String saveBase64File(String basePath, String fileName, String base64) {
        try {
            String extension = getExtension(base64);
            byte[] data = Base64.getDecoder().decode(base64);

            File directorio = new File(basePath);
            if (!directorio.exists()) {
            	directorio.mkdirs();
            }

            String fullPath = basePath + "/" + fileName + "." + extension;

            try (FileOutputStream e = new FileOutputStream(fullPath)) {
                e.write(data);
            }

            return fullPath;

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }
    }

	private static String getExtension(String base64) {
	    if (base64 == null || base64.isEmpty()) {
	        throw new RuntimeException("Archivo vacio");
	    }

	    if (base64.contains("application/pdf")) {
	        return "pdf";
	    }

	    if (base64.contains("image/jpeg") || base64.contains("image/jpg")) {
	        return "jpg";
	    }

	    if (base64.contains("image/png")) {
	        return "png";
	    }

	    throw new RuntimeException("Formato no soportado");
	}
}
