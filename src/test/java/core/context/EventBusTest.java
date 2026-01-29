package core.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class EventBusTest {

    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
    }

    @Test
    void testRegisterAndPost() {
        AtomicBoolean received = new AtomicBoolean(false);
        Consumer<String> listener = event -> received.set(true);

        eventBus.register(String.class, listener);
        eventBus.post("Hello Event");

        assertTrue(received.get(), "Listener should have received the event");
    }

    @Test
    void testUnregister() {
        AtomicInteger callCount = new AtomicInteger(0);
        Consumer<String> listener = event -> callCount.incrementAndGet();

        eventBus.register(String.class, listener);
        eventBus.post("Event 1");
        eventBus.unregister(String.class, listener);
        eventBus.post("Event 2");

        assertEquals(1, callCount.get(), "Listener should receive only the first event");
    }

    @Test
    void testMultipleListeners() {
        AtomicInteger count1 = new AtomicInteger(0);
        AtomicInteger count2 = new AtomicInteger(0);

        eventBus.register(String.class, s -> count1.incrementAndGet());
        eventBus.register(String.class, s -> count2.incrementAndGet());

        eventBus.post("Event");

        assertEquals(1, count1.get());
        assertEquals(1, count2.get());
    }

    @Test
    void testDifferentEventTypes() {
        AtomicBoolean stringReceived = new AtomicBoolean(false);
        AtomicBoolean integerReceived = new AtomicBoolean(false);

        eventBus.register(String.class, s -> stringReceived.set(true));
        eventBus.register(Integer.class, i -> integerReceived.set(true));

        eventBus.post("A String");

        assertTrue(stringReceived.get(), "String listener should be triggered");
        assertFalse(integerReceived.get(), "Integer listener should NOT be triggered");
    }

    @Test
    void testPostWithNoListeners() {
        assertDoesNotThrow(() -> eventBus.post("No Listeners"), "Posting with no listeners should not throw exception");
    }

    @Test
    void testUnregisterUnknownListener() {
        Consumer<String> unknown = s -> {
        };
        assertDoesNotThrow(() -> eventBus.unregister(String.class, unknown),
                "Unregistering unknown listener should be safe");
    }
}
