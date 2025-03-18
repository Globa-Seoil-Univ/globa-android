package team.y2k2.globa.api.model.entity;

public class Pair implements Comparable<Pair> {
    private final int number;
    private final String text;

    public Pair(int number, String text) {
        this.number = number;
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    @Override
    public int compareTo(Pair other) {
        return Integer.compare(this.number, other.number);
    }
}
