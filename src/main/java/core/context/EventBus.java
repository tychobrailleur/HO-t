package core.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A simple synchronous Event Bus.
 */
public class EventBus {

    private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    /**
     * Register a listener for a specific event type.
     * 
     * @param eventType The class of the event to listen for.
     * @param listener  The listener to callback when the event is fired.
     * @param <T>       The event type.
     */
    public synchronized <T> void register(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Unregister a listener.
     */
    public synchronized <T> void unregister(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<?>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    /**
     * Post an event to all registered listeners.
     * 
     * @param event The event object.
     */
    @SuppressWarnings("unchecked")
    public synchronized void post(Object event) {
        List<Consumer<?>> list = listeners.get(event.getClass());
        if (list != null) {
            // Copy list to avoid ConcurrentModificationException if listener unregisters
            // during event
            for (Consumer<?> listener : new ArrayList<>(list)) {
                ((Consumer<Object>) listener).accept(event);
            }
        }
    }
}
