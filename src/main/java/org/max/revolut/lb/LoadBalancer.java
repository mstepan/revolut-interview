package org.max.revolut.lb;

import java.util.Optional;


public interface LoadBalancer {

    /**
     * Register instances
     *
     * It should be possible to register an instance, identified by an address
     * Each address should be unique, it should not be possible to register the same address more than once
     * Load balancer should accept up to 10 addresses
     */
    boolean register(String address); // DNS , IP, vIP

    /**
     * Develop an algorithm that, when invoking the Load Balancer's get() method multiple times, should return
     * one backend-instance choosing between the registered ones randomly.
     */
    Optional<String> getAny();

    /*
    Develop an algorithm that, when invoking multiple times the Load Balancer on its get() method, should
    return one backend-instance choosing between the registered one sequentially (round-robin).
     */
    Optional<String> getRoundRobin();

}
