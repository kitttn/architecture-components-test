package betrip.kitttn.architecturecomponentstest.view.adapters;

/**
 * @author kitttn
 */

public interface Handler<T> {
    void invoke(T param);
}
