package com.uade.entrelibros.backend.service;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import com.uade.entrelibros.backend.entity.dto.PagoRequest;
import com.uade.entrelibros.backend.exceptions.CamposInvalidosException;

// Valida los datos de la tarjeta antes de registrar un pago con tarjeta. El pago es simulado:
// no se cobra ni se guarda la tarjeta, pero los datos tienen que tener la forma de una tarjeta real.
public final class ValidadorTarjeta {

    private ValidadorTarjeta() {
    }

    public static void validar(PagoRequest request) {
        if (!"tarjeta".equalsIgnoreCase(request.getProveedor())) {
            return;
        }
        Map<String, String> errores = new LinkedHashMap<>();

        String numero = soloDigitos(request.getNumeroTarjeta());
        if (numero.isEmpty()) {
            errores.put("numeroTarjeta", "Ingresá el número de la tarjeta.");
        } else if (numero.length() < 13 || numero.length() > 19 || !pasaLuhn(numero)) {
            errores.put("numeroTarjeta", "El número de tarjeta no es válido.");
        }

        String titular = request.getTitular() == null ? "" : request.getTitular().trim();
        if (titular.length() < 3) {
            errores.put("titular", "Ingresá el nombre como figura en la tarjeta.");
        }

        String vencimiento = request.getVencimiento() == null ? "" : request.getVencimiento().trim();
        if (!vencimiento.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            errores.put("vencimiento", "Ingresá el vencimiento como MM/AA.");
        } else {
            YearMonth vence = YearMonth.of(2000 + Integer.parseInt(vencimiento.substring(3)),
                    Integer.parseInt(vencimiento.substring(0, 2)));
            if (vence.isBefore(YearMonth.now())) {
                errores.put("vencimiento", "La tarjeta está vencida.");
            }
        }

        String codigo = request.getCodigoSeguridad() == null ? "" : request.getCodigoSeguridad().trim();
        if (!codigo.matches("\\d{3,4}")) {
            errores.put("codigoSeguridad", "El código de seguridad tiene 3 o 4 dígitos.");
        }

        if (!errores.isEmpty()) {
            throw new CamposInvalidosException(errores);
        }
    }

    private static String soloDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("[\\s-]", "");
    }

    // Algoritmo de Luhn: el digito verificador que tienen todas las tarjetas reales.
    // Detecta numeros inventados o con un digito mal tipeado.
    private static boolean pasaLuhn(String numero) {
        if (!numero.matches("\\d+")) {
            return false;
        }
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }
}
