package org.loadbalancer;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class LoadBalancerSimulation extends Simulation {
    //HttpProtocolBuilder
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:1221");

    //ScenarioBuilder
    ScenarioBuilder scenarioBuilder = scenario("Load balancer load test").exec(http("Request 1").get("/"))
            .pause(2).exec(http("Request 2").get("/"));

    //Setup
//    {
//        setUp(
//                scenarioBuilder.injectOpen(constantUsersPerSec(2).during(60))
//        ).protocols(httpProtocol);
//    }

    //nothing for first 4 sec, inject 10 users at once, gradually
    //inject 50 users over 20 sec (2.5 users per second); each user
    //sends 2 requests with a pause of 2 sec in between. Total no. of
    //requests =  60 * 2= 120
    {
        setUp(
                scenarioBuilder.injectOpen(
                        nothingFor( 4),
                        atOnceUsers(10),
                        rampUsers(50).during(20)
                )
        ).protocols(httpProtocol);
    }
}