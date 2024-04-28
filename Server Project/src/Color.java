public class Color {
    private String colorStringRepresentation;

    public Color(int colVal) {
        this.colorStringRepresentation = String.format("\u001B[%dm" , colVal);
    }

    @Override
    public String toString() {
        return colorStringRepresentation;
    }
}
