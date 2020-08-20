package com.elena.elena.autocomplete;

import lombok.Getter;

public class NameSuggestion {

    @Getter
    private String name;

    public NameSuggestion(String name){
        this.name = name;
    }
}
