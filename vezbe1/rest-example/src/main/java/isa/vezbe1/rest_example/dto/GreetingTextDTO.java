package isa.vezbe1.rest_example.dto;

public class GreetingTextDTO {
    private String text;

    public GreetingTextDTO() {
    }

    public GreetingTextDTO(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
