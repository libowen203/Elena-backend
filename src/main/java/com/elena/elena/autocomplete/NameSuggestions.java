package com.elena.elena.autocomplete;

import lombok.Getter;

import java.util.Collection;

public class NameSuggestions {

    @Getter
    private Collection<NameSuggestion> values;

    public NameSuggestions(Collection<NameSuggestion> suggestions){
        this.values = suggestions;
    }
}
