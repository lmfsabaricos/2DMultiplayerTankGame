package tankgamepack.Resources;

public class Pair <T, U>{
    private T first;
    private U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getfirst() {
        return this.first;
    }

    public U getSecond() {
        return this.second;
    }
}
