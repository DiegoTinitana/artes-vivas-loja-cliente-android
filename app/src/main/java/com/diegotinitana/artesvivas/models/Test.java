package com.diegotinitana.artesvivas.models;

public class Test {
    String quiz;
    String id;

    public Test(String quiz, String id) {
        this.quiz = quiz;
        this.id = id;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
