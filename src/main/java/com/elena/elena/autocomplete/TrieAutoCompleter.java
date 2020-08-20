package com.elena.elena.autocomplete;

import com.elena.elena.model.AbstractElenaGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TrieAutoCompleter implements AutoCompleter{

    private AbstractElenaGraph graph;
    private Collection<String> locationNames;
    private Trie root;


    public TrieAutoCompleter(AbstractElenaGraph graph){
        this.graph = graph;
        locationNames = this.graph.getLocationNames();
        root = new Trie(false, new ArrayList<>());
        populateTrie();
    }

    @Override
    public Collection<NameSuggestion> getNameSuggestions(String initialName) {

        Trie current = root;
        List<NameSuggestion> suggestions = new ArrayList<>();

        for(Character character : initialName.toLowerCase().toCharArray()){
            if(current.nextTries.containsKey(character)) {
                current = current.nextTries.get(character);
            }
            else{
                break;
            }
        }

        this.addWords(suggestions, current);

        return suggestions;
    }

    /**
     * Given a Trie and a collection, this method recursively adds
     * all words in the Trie
     */
    private void addWords(Collection<NameSuggestion> suggestions, Trie trie){

        if(trie.nextTries.isEmpty()){
            return;
        }

        for(Trie nextTrie : trie.nextTries.values()){
            if(nextTrie.isWord){
                String name = nextTrie.characters.stream().map(String::valueOf)
                        .collect(Collectors.joining());
                NameSuggestion nameSuggestion = new NameSuggestion(name);
                suggestions.add(nameSuggestion);
            }
            addWords(suggestions, nextTrie);
        }
    }

    private void populateTrie(){

        Trie current = root;

        for(String locationName : locationNames){
            for(Character character : locationName.toCharArray()){
                current.insertIfabsent(character, false);
                current = current.nextTries.get(character);
            }
            current.isWord = true;
            current = root;
        }
    }

    private class Trie{

        private boolean isWord;
        private List<Character> characters;
        private Map<Character, Trie> nextTries;

        private Trie(Boolean isWord, List<Character> initialCharacters){
            this.isWord = isWord;
            this.nextTries = new HashMap<>();
            characters = new ArrayList<>();
            characters.addAll(initialCharacters);
        }

        /**
         * Returns true if it successfully inserts a key into trie,
         * returns false otherwise.
         */
        private boolean insertIfabsent(Character key, boolean isWord){
            if(!nextTries.containsKey(key)){
                Trie nextTrie = new Trie(isWord, this.characters);
                nextTrie.characters.add(key);
                nextTries.put(key, nextTrie);
                return true;
            }
            return false;
        }
    }

}
