package br.unesp.sjrp.httpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class RequisicaoHTTPTest {

    private RequisicaoHTTP requisicao;

    @BeforeEach
    void setUp() {
        requisicao = new RequisicaoHTTP();
    }

    @Test
    void testGettersAndSetters() {
        // Test método
        requisicao.setMetodo("GET");
        assertEquals("GET", requisicao.getMetodo());

        // Test recurso
        requisicao.setRecurso("/index.html");
        assertEquals("/index.html", requisicao.getRecurso());

        // Test protocolo
        requisicao.setProtocolo("HTTP/1.1");
        assertEquals("HTTP/1.1", requisicao.getProtocolo());

        // Test manterViva
        requisicao.setManterViva(false);
        assertFalse(requisicao.isManterViva());
        
        requisicao.setManterViva(true);
        assertTrue(requisicao.isManterViva());

        // Test tempoLimite
        requisicao.setTempoLimite(5000);
        assertEquals(5000, requisicao.getTempoLimite());
    }

    @Test
    void testDefaultValues() {
        // Test default values
        assertTrue(requisicao.isManterViva()); // default is true
        assertEquals(3000, requisicao.getTempoLimite()); // default is 3000
    }

    @Test
    void testSetCabecalho() {
        // Test setting single header value
        requisicao.setCabecalho("Content-Type", "text/html");
        
        Map<String, List<String>> cabecalhos = requisicao.getCabecalhos();
        assertNotNull(cabecalhos);
        assertTrue(cabecalhos.containsKey("Content-Type"));
        assertEquals(Arrays.asList("text/html"), cabecalhos.get("Content-Type"));
    }

    @Test
    void testSetCabecalhoMultipleValues() {
        // Test setting multiple header values
        requisicao.setCabecalho("Accept", "text/html", "application/xml", "application/json");
        
        Map<String, List<String>> cabecalhos = requisicao.getCabecalhos();
        assertNotNull(cabecalhos);
        assertTrue(cabecalhos.containsKey("Accept"));
        assertEquals(Arrays.asList("text/html", "application/xml", "application/json"), 
                    cabecalhos.get("Accept"));
    }

    @Test
    void testSetCabecalhoOverwrite() {
        // Test overwriting header value
        requisicao.setCabecalho("Host", "localhost:8080");
        requisicao.setCabecalho("Host", "example.com");
        
        Map<String, List<String>> cabecalhos = requisicao.getCabecalhos();
        assertEquals(Arrays.asList("example.com"), cabecalhos.get("Host"));
    }

    @Test
    void testSetCabecalhosMap() {
        Map<String, List<String>> headerMap = new java.util.TreeMap<>();
        headerMap.put("Content-Type", Arrays.asList("text/html"));
        headerMap.put("Content-Length", Arrays.asList("100"));
        
        requisicao.setCabecalhos(headerMap);
        
        Map<String, List<String>> cabecalhos = requisicao.getCabecalhos();
        assertEquals(headerMap, cabecalhos);
    }

    @Test
    void testLerRequisicaoBasic() throws IOException {
        String httpRequest = "GET /index.html HTTP/1.1\r\n" +
                           "Host: localhost:8000\r\n" +
                           "User-Agent: Mozilla/5.0\r\n" +
                           "\r\n";
        
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        
        RequisicaoHTTP req = RequisicaoHTTP.lerRequisicao(inputStream);
        
        assertNotNull(req);
        assertEquals("GET", req.getMetodo());
        assertEquals("/index.html", req.getRecurso());
        assertEquals("HTTP/1.1", req.getProtocolo());
        
        Map<String, List<String>> cabecalhos = req.getCabecalhos();
        assertNotNull(cabecalhos);
        assertTrue(cabecalhos.containsKey("Host"));
        assertEquals(Arrays.asList("localhost"), cabecalhos.get("Host"));
        assertTrue(cabecalhos.containsKey("User-Agent"));
        assertEquals(Arrays.asList("Mozilla/5.0"), cabecalhos.get("User-Agent"));
        
        // Default behavior: keep-alive should be true when not specified
        assertTrue(req.isManterViva());
    }

    @Test
    void testLerRequisicaoWithKeepAlive() throws IOException {
        String httpRequest = "GET / HTTP/1.1\r\n" +
                           "Host: localhost:8000\r\n" +
                           "Connection: keep-alive\r\n" +
                           "\r\n";
        
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        
        RequisicaoHTTP req = RequisicaoHTTP.lerRequisicao(inputStream);
        
        assertTrue(req.isManterViva());
        assertEquals("/", req.getRecurso());
    }

    @Test
    void testLerRequisicaoWithCloseConnection() throws IOException {
        String httpRequest = "POST /submit HTTP/1.1\r\n" +
                           "Host: localhost:8000\r\n" +
                           "Connection: close\r\n" +
                           "\r\n";
        
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        
        RequisicaoHTTP req = RequisicaoHTTP.lerRequisicao(inputStream);
        
        assertFalse(req.isManterViva());
        assertEquals("POST", req.getMetodo());
        assertEquals("/submit", req.getRecurso());
    }

    @Test
    void testLerRequisicaoMultipleHeaderValues() throws IOException {
        String httpRequest = "GET /test HTTP/1.1\r\n" +
                           "Host: localhost:8000\r\n" +
                           "Accept: text/html, application/xml, application/json\r\n" +
                           "\r\n";
        
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        
        RequisicaoHTTP req = RequisicaoHTTP.lerRequisicao(inputStream);
        
        Map<String, List<String>> cabecalhos = req.getCabecalhos();
        assertTrue(cabecalhos.containsKey("Accept"));
        assertEquals(Arrays.asList("text/html", " application/xml", " application/json"), 
                    cabecalhos.get("Accept"));
    }

    @Test
    void testLerRequisicaoMinimal() throws IOException {
        String httpRequest = "HEAD /favicon.ico HTTP/1.0\r\n" +
                           "Host: localhost\r\n" +  // Need at least one header to avoid NPE
                           "\r\n";
        
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        
        RequisicaoHTTP req = RequisicaoHTTP.lerRequisicao(inputStream);
        
        assertEquals("HEAD", req.getMetodo());
        assertEquals("/favicon.ico", req.getRecurso());
        assertEquals("HTTP/1.0", req.getProtocolo());
        
        // No Connection header, so should remain default (true)
        assertTrue(req.isManterViva());
        
        // Should have the Host header
        assertNotNull(req.getCabecalhos());
        assertTrue(req.getCabecalhos().containsKey("Host"));
    }
}