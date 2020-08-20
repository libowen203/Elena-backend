package com.elena.elena.controller;

import com.elena.elena.autocomplete.AutoCompleter;
import com.elena.elena.autocomplete.NameSuggestions;
import com.elena.elena.model.AbstractElenaGraph;
import com.elena.elena.model.AbstractElenaPath;
import com.elena.elena.routing.*;
import com.elena.elena.util.ElenaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;


@RestController
@CrossOrigin(origins = {"https://elena-front.s3-website-us-east-1.amazonaws.com","http://localhost:3000"})
public class RouteController {

    @Autowired
    private AbstractElenaGraph graph;

    @Autowired
    private AutoCompleter autoCompleter;

    @RequestMapping(method= RequestMethod.GET, value="search")
    public ResponseEntity<String> getRouteCoordinates(@RequestParam("from") String from,
                                                                 @RequestParam("to") String to,
                                                                 @RequestParam("algorithm") String algorithmName,
                                                                 @RequestParam("elemode") String elevationMode,
                                                                 @RequestParam("percentage") int percentage) throws IOException {


        Algorithm algorithm = Algorithm.valueOf(algorithmName.toUpperCase());
        ElevationMode eleMode = ElevationMode.valueOf(elevationMode.toUpperCase());
        AbstractRouter router = RouterFactory.getRouter(algorithm, percentage);

        if(!graph.getNode(from).isPresent() || !graph.getNode(to).isPresent()){
            //Throw an error response back
        }
        long s = System.currentTimeMillis();
        List<AbstractElenaPath> paths = router.getRoute(graph.getNode(from).get(), graph.getNode(to).get(), graph);
        AbstractElenaPath selectedPath = ElenaUtils.selectPath(eleMode, paths, percentage);
        System.out.println((float) (System.currentTimeMillis() - s) / 1000);
        String response = this.aggregatePathInfo(paths.get(0), selectedPath);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

        return responseEntity;
    }

    @RequestMapping(method= RequestMethod.GET, value="autocomplete")
    public ResponseEntity<NameSuggestions> getAutocompleteList(@RequestParam("name") String name) {

        NameSuggestions suggestions = new NameSuggestions(autoCompleter.getNameSuggestions(name));
        ResponseEntity<NameSuggestions> responseEntity = new ResponseEntity<>(suggestions, HttpStatus.OK);

        return responseEntity;
    }

    @PreDestroy
    private void cleanup(){
        graph.cleanup();
    }

    private String aggregatePathInfo(AbstractElenaPath shortestpath, AbstractElenaPath selectedPath){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ \"shortestpath\": ")
                .append(shortestpath.toString())
                .append(",")
                .append(" \"selectedpath\": ")
                .append(selectedPath.toString()).append("}");

        String jsonResult = stringBuilder.toString();

        return jsonResult;
    }
}
