package dag.handlers;

/**
 * @author xijiu
 * @since 2022/4/15 上午10:25
 */
public class Ref<T> {
    private T t;

    public Ref(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }
}
