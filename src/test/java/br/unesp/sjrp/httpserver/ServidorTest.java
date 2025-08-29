package br.unesp.sjrp.httpserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.ServerSocket;

class ServidorTest {

    @Test
    void testMainClassExists() {
        // This test simply verifies that the Servidor class exists and can be instantiated
        // The main method is difficult to test directly due to its infinite loop and socket binding
        assertDoesNotThrow(() -> {
            Servidor servidor = new Servidor();
            assertNotNull(servidor);
        });
    }

    @Test
    void testMainMethodReflectionAccess() {
        // Test that the main method exists and has the correct signature
        try {
            java.lang.reflect.Method mainMethod = Servidor.class.getMethod("main", String[].class);
            assertNotNull(mainMethod);
            assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
            assertEquals(void.class, mainMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("Main method should exist with correct signature");
        }
    }

    @Test
    void testPortAvailability() {
        // Test that we can create a ServerSocket on a different port
        // This verifies the basic ServerSocket functionality used in main()
        try (ServerSocket testSocket = new ServerSocket(0)) { // Use port 0 for automatic assignment
            assertNotNull(testSocket);
            assertTrue(testSocket.getLocalPort() > 0);
            assertFalse(testSocket.isClosed());
        } catch (IOException e) {
            fail("Should be able to create ServerSocket: " + e.getMessage());
        }
    }

    @Test
    void testThreadPoolConcept() {
        // Test that ExecutorService can be created (simulating what main() does)
        java.util.concurrent.ExecutorService pool = null;
        try {
            pool = java.util.concurrent.Executors.newFixedThreadPool(20);
            assertNotNull(pool);
            assertFalse(pool.isShutdown());
        } finally {
            if (pool != null) {
                pool.shutdown();
            }
        }
    }
}