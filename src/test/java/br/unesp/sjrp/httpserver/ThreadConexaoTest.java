package br.unesp.sjrp.httpserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.Socket;

class ThreadConexaoTest {

    private Socket mockSocket;
    private ThreadConexao threadConexao;

    @BeforeEach
    void setUp() {
        mockSocket = mock(Socket.class);
        threadConexao = new ThreadConexao(mockSocket);
    }

    @Test
    void testConstructor() {
        // Test that constructor accepts socket and creates ThreadConexao
        assertNotNull(threadConexao);
        assertTrue(threadConexao instanceof Runnable);
    }

    @Test 
    void testConstructorWithNullSocket() {
        // Test constructor with null socket - should not throw exception
        assertDoesNotThrow(() -> {
            ThreadConexao nullThreadConexao = new ThreadConexao(null);
            assertNotNull(nullThreadConexao);
        });
    }

    @Test
    void testImplementsRunnable() {
        // Verify ThreadConexao implements Runnable interface
        assertTrue(threadConexao instanceof Runnable);
        
        // Verify it can be used as a Runnable
        Runnable runnable = threadConexao;
        assertNotNull(runnable);
    }

    @Test
    void testCanBeUsedInThread() {
        // Test that ThreadConexao can be used to create a Thread
        Thread thread = new Thread(threadConexao);
        assertNotNull(thread);
        assertEquals(Thread.State.NEW, thread.getState());
    }

    @Test
    void testRunMethodExists() {
        // Verify the run method exists and is public
        try {
            var runMethod = threadConexao.getClass().getMethod("run");
            assertTrue(java.lang.reflect.Modifier.isPublic(runMethod.getModifiers()));
            assertEquals(void.class, runMethod.getReturnType());
            assertEquals(0, runMethod.getParameterCount());
        } catch (NoSuchMethodException e) {
            fail("run() method should exist and be public");
        }
    }

    @Test
    void testClassStructure() {
        // Test basic class structure
        assertEquals("ThreadConexao", threadConexao.getClass().getSimpleName());
        assertEquals("br.unesp.sjrp.httpserver.ThreadConexao", threadConexao.getClass().getName());
        assertTrue(Runnable.class.isAssignableFrom(threadConexao.getClass()));
    }

    @Test
    void testMultipleInstances() {
        // Test that multiple instances can be created
        Socket socket1 = mock(Socket.class);
        Socket socket2 = mock(Socket.class);
        
        ThreadConexao tc1 = new ThreadConexao(socket1);
        ThreadConexao tc2 = new ThreadConexao(socket2);
        
        assertNotNull(tc1);
        assertNotNull(tc2);
        assertNotSame(tc1, tc2);
    }

    @Test
    void testObjectMethods() {
        // Test basic Object methods
        assertNotNull(threadConexao.toString());
        assertEquals(threadConexao.hashCode(), threadConexao.hashCode()); // Consistent
        assertTrue(threadConexao.equals(threadConexao)); // Reflexive
        assertFalse(threadConexao.equals(null));
        assertFalse(threadConexao.equals("not a ThreadConexao"));
    }

    @Test
    void testThreadCreationAndProperties() {
        // Test creating multiple threads with ThreadConexao instances
        Socket socket1 = mock(Socket.class);
        Socket socket2 = mock(Socket.class);
        
        ThreadConexao tc1 = new ThreadConexao(socket1);
        ThreadConexao tc2 = new ThreadConexao(socket2);
        
        Thread thread1 = new Thread(tc1);
        Thread thread2 = new Thread(tc2);
        
        assertNotNull(thread1);
        assertNotNull(thread2);
        assertNotSame(thread1, thread2);
        assertEquals(Thread.State.NEW, thread1.getState());
        assertEquals(Thread.State.NEW, thread2.getState());
    }

    @Test
    void testRunnableInterfaceCompatibility() {
        // Test that ThreadConexao can be treated as different interface types
        Object obj = threadConexao;
        Runnable runnable = threadConexao;
        
        assertNotNull(obj);
        assertNotNull(runnable);
        assertSame(threadConexao, obj);
        assertSame(threadConexao, runnable);
        
        // Test instanceof checks
        assertTrue(obj instanceof ThreadConexao);
        assertTrue(obj instanceof Runnable);
        assertTrue(runnable instanceof ThreadConexao);
    }
}