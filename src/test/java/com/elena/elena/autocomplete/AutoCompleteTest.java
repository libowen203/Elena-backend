package com.elena.elena.autocomplete;

import com.elena.elena.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class AutoCompleteTest {


    @Autowired
    private TrieAutoCompleter autoCompleter;

    @Test
    public void testAutocomplete(){

        int count = 0;
        String input = "new ";
        for(NameSuggestion suggestion : autoCompleter.getNameSuggestions(input)){
            if(suggestion.getName().substring(0, 4).contains(input)){
                count++;
            }
        }
        Assert.assertEquals(2, count);
    }

}
