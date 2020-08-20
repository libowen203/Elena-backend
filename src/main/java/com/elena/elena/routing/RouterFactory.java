package com.elena.elena.routing;

import org.springframework.stereotype.Component;

@Component
public class RouterFactory  {

    public static AbstractRouter getRouter(Algorithm algorithm, int percentage) {

        switch(algorithm){

            case A_STAR_MULTIROUTES:
                if(percentage == 100){
                    return RouterFactory.getRouter(Algorithm.A_STAR);
                }
                return new MultiRoutesAstarRouter(percentage);

            case A_STAR_YEN:
                if(percentage == 100){
                    return RouterFactory.getRouter(Algorithm.A_STAR);
                }
                return new YenRouter(10, RouterFactory.getRouter(Algorithm.A_STAR));

            case DIJKSTRA_YEN:
                if(percentage == 100){
                    return RouterFactory.getRouter(Algorithm.DIJKSTRA);
                }
                return new YenRouter(10, RouterFactory.getRouter(Algorithm.DIJKSTRA));

            default:
                return RouterFactory.getRouter(algorithm);
        }
    }

    public static AbstractRouter getRouter(Algorithm algorithm) {

        switch(algorithm){

            case A_STAR:
                return new AstarRouter(null);

            default:
                return new DijkstraRouter();
        }
    }
}
