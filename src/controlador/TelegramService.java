package controlador;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TelegramService {

    static String obtenerTokenBot() {
        String token = System.getenv("TELEGRAM_BOT_TOKEN");
        return token == null ? "" : token.trim();
    }

    @SuppressWarnings("deprecation")
	public static void enviarNotificacionAsync(String chatId, String mensaje) {
        if (chatId == null || chatId.trim().isEmpty()) {
            System.out.println("TelegramService: Chat ID está vacío.");
            return;
        }

        String botToken = obtenerTokenBot();
        if (botToken.isEmpty()) {
            System.err.println("No se envió la notificación: falta configurar TELEGRAM_BOT_TOKEN.");
            return;
        }

        new Thread(() -> {
            try {
                String textoCodificado = URLEncoder.encode(mensaje, "UTF-8");
                String urlString = "https://api.telegram.org/bot" + botToken
                        + "/sendMessage?chat_id=" + chatId.trim() 
                        + "&text=" + textoCodificado
                        + "&parse_mode=Markdown"; // Formato Markdown activado

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("Notificación de Telegram enviada con éxito.");
                } else {
                    System.err.println("Error al enviar Telegram. Respuesta HTTP: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                System.err.println("Error en hilo de Telegram: " + e.getMessage());
            }
        }).start();
    }
}