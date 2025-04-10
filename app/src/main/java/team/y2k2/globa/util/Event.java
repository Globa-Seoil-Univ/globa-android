package team.y2k2.globa.util; // Or your preferred utility package

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * @param <T> The type of the content.
 */
public class Event<T> {

    private final T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Returns the content and prevents its use again.
     * @return The content if not handled, null otherwise.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     * @return The content.
     */
    public T peekContent() {
        return content;
    }

    /**
     * Returns whether the content has been handled.
     * @return true if handled, false otherwise.
     */
    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}