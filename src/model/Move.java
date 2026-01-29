package model;

public enum Move {
    ROCK(1, "Rock"),
    PAPER(2, "Paper"),
    SCISSORS(3, "Scissors"),
    INVALID(0, "Invalid");

    private final int value;
    private final String name;

    Move(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static Move fromInt(int value) {
        switch (value) {
            case 1: return ROCK;
            case 2: return PAPER;
            case 3: return SCISSORS;
            default: return INVALID;
        }
    }

    public boolean beats(Move other) {
        if (this == ROCK && other == SCISSORS) return true;
        if (this == PAPER && other == ROCK) return true;
        if (this == SCISSORS && other == PAPER) return true;
        return false;
    }
}

