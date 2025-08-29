package br.unesp.sjrp.httpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class RespostaHTTPTest {

    private RespostaHTTP resposta;

    @BeforeEach
    void setUp() {
        resposta = new RespostaHTTP();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(resposta);
        assertNull(resposta.getProtocolo());
        assertEquals(0, resposta.getCodigoResposta());
        assertNull(resposta.getMensagem());
        assertNull(resposta.getConteudoResposta());
        assertNull(resposta.getCabecalhos());
        assertNull(resposta.getSaida());
    }

    @Test
    void testParameterizedConstructor() {
        RespostaHTTP resp = new RespostaHTTP("HTTP/1.1", 200, "OK");
        
        assertEquals("HTTP/1.1", resp.getProtocolo());
        assertEquals(200, resp.getCodigoResposta());
        assertEquals("OK", resp.getMensagem());
    }

    @Test
    void testGettersAndSetters() {
        // Test protocolo
        resposta.setProtocolo("HTTP/1.1");
        assertEquals("HTTP/1.1", resposta.getProtocolo());

        // Test codigoResposta
        resposta.setCodigoResposta(404);
        assertEquals(404, resposta.getCodigoResposta());

        // Test mensagem
        resposta.setMensagem("Not Found");
        assertEquals("Not Found", resposta.getMensagem());

        // Test conteudoResposta
        byte[] content = "Hello World".getBytes();
        resposta.setConteudoResposta(content);
        assertArrayEquals(content, resposta.getConteudoResposta());

        // Test saida
        OutputStream outputStream = new ByteArrayOutputStream();
        resposta.setSaida(outputStream);
        assertEquals(outputStream, resposta.getSaida());
    }

    @Test
    void testSetCabecalho() {
        // Test setting single header value
        resposta.setCabecalho("Content-Type", "text/html");
        
        Map<String, List<String>> cabecalhos = resposta.getCabecalhos();
        assertNotNull(cabecalhos);
        assertTrue(cabecalhos.containsKey("Content-Type"));
        assertEquals(Arrays.asList("text/html"), cabecalhos.get("Content-Type"));
    }

    @Test
    void testSetCabecalhoMultipleValues() {
        // Test setting multiple header values
        resposta.setCabecalho("Cache-Control", "no-cache", "no-store", "max-age=0");
        
        Map<String, List<String>> cabecalhos = resposta.getCabecalhos();
        assertNotNull(cabecalhos);
        assertTrue(cabecalhos.containsKey("Cache-Control"));
        assertEquals(Arrays.asList("no-cache", "no-store", "max-age=0"), 
                    cabecalhos.get("Cache-Control"));
    }

    @Test
    void testSetCabecalhoOverwrite() {
        // Test overwriting header value
        resposta.setCabecalho("Server", "Apache/2.4");
        resposta.setCabecalho("Server", "MeuServidor/1.0");
        
        Map<String, List<String>> cabecalhos = resposta.getCabecalhos();
        assertEquals(Arrays.asList("MeuServidor/1.0"), cabecalhos.get("Server"));
    }

    @Test
    void testSetCabecalhosMap() {
        Map<String, List<String>> headerMap = new java.util.TreeMap<>();
        headerMap.put("Content-Type", Arrays.asList("text/html"));
        headerMap.put("Content-Length", Arrays.asList("100"));
        
        resposta.setCabecalhos(headerMap);
        
        Map<String, List<String>> cabecalhos = resposta.getCabecalhos();
        assertEquals(headerMap, cabecalhos);
    }

    @Test
    void testGetTamanhoResposta() {
        String content = "Hello, World!";
        resposta.setConteudoResposta(content.getBytes());
        
        assertEquals("13", resposta.getTamanhoResposta());
    }

    @Test
    void testGetTamanhoRespostaEmpty() {
        resposta.setConteudoResposta(new byte[0]);
        assertEquals("0", resposta.getTamanhoResposta());
    }

    @Test
    void testToString() {
        resposta.setProtocolo("HTTP/1.1");
        resposta.setCodigoResposta(200);
        resposta.setMensagem("OK");
        resposta.setCabecalho("Content-Type", "text/html");
        resposta.setCabecalho("Content-Length", "13");
        
        String expected = "HTTP/1.1 200 OK\r\n" +
                         "Content-Length: 13\r\n" +
                         "Content-Type: text/html\r\n" +
                         "\r\n";
        
        assertEquals(expected, resposta.toString());
    }

    @Test
    void testToStringMultipleHeaderValues() {
        resposta.setProtocolo("HTTP/1.1");
        resposta.setCodigoResposta(404);
        resposta.setMensagem("Not Found");
        resposta.setCabecalho("Cache-Control", "no-cache", "no-store");
        
        String responseString = resposta.toString();
        
        assertTrue(responseString.startsWith("HTTP/1.1 404 Not Found\r\n"));
        assertTrue(responseString.contains("Cache-Control: no-cache, no-store\r\n"));
        assertTrue(responseString.endsWith("\r\n"));
    }

    @Test
    void testEnviar() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        resposta.setProtocolo("HTTP/1.1");
        resposta.setCodigoResposta(200);
        resposta.setMensagem("OK");
        resposta.setCabecalho("Content-Type", "text/plain");
        resposta.setCabecalho("Content-Length", "5");
        resposta.setConteudoResposta("Hello".getBytes());
        resposta.setSaida(outputStream);
        
        resposta.enviar();
        
        String output = outputStream.toString();
        
        // Should contain both headers and content
        assertTrue(output.contains("HTTP/1.1 200 OK"));
        assertTrue(output.contains("Content-Type: text/plain"));
        assertTrue(output.contains("Content-Length: 5"));
        assertTrue(output.endsWith("Hello"));
    }

    @Test
    void testEnviarWithComplexContent() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        String htmlContent = "<html><body><h1>Test Page</h1></body></html>";
        
        resposta.setProtocolo("HTTP/1.1");
        resposta.setCodigoResposta(200);
        resposta.setMensagem("OK");
        resposta.setCabecalho("Content-Type", "text/html");
        resposta.setCabecalho("Server", "MeuServidor/1.0");
        resposta.setConteudoResposta(htmlContent.getBytes());
        resposta.setSaida(outputStream);
        
        resposta.enviar();
        
        String output = outputStream.toString();
        
        assertTrue(output.contains("HTTP/1.1 200 OK"));
        assertTrue(output.contains("Content-Type: text/html"));
        assertTrue(output.contains("Server: MeuServidor/1.0"));
        assertTrue(output.endsWith(htmlContent));
    }

    @Test
    void testEnviarErrorResponse() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        String errorContent = "<html><body><h1>404 - Not Found</h1></body></html>";
        
        resposta.setProtocolo("HTTP/1.1");
        resposta.setCodigoResposta(404);
        resposta.setMensagem("Not Found");
        resposta.setCabecalho("Content-Type", "text/html");
        resposta.setConteudoResposta(errorContent.getBytes());
        resposta.setSaida(outputStream);
        
        resposta.enviar();
        
        String output = outputStream.toString();
        
        assertTrue(output.contains("HTTP/1.1 404 Not Found"));
        assertTrue(output.endsWith(errorContent));
    }
}