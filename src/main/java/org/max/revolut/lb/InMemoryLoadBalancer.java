package org.max.revolut.lb;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class InMemoryLoadBalancer implements LoadBalancer {

    private static final int MAX_POOL_SIZE = 10;

    private final Set<String> registeredAddresses = new LinkedHashSet<>();

    private int curIndex;

    private final Object mutex = new Object();


    private final Random rand;


    public InMemoryLoadBalancer(long initialSeed) {
        this.rand = new Random(initialSeed);
    }


    @Override
    public Optional<String> getAny() {

        String[] copy;

        synchronized (mutex) {
            if (registeredAddresses.isEmpty()) {
                return Optional.empty();
            }

            copy = makeSnapshot();
        }

        int randIndex = rand.nextInt(copy.length);

        assert randIndex >= 0 && randIndex < copy.length;

        return Optional.of(copy[randIndex]);
    }

    @Override
    public Optional<String> getRoundRobin() {

        String[] copy;
        int indexToRet;

        synchronized (mutex) {
            if (registeredAddresses.isEmpty()) {
                return Optional.empty();
            }

            copy = makeSnapshot();
            indexToRet = curIndex;
            curIndex = (curIndex + 1) % copy.length;
        }

        return Optional.of(copy[indexToRet]);
    }

    @Override
    public boolean register(String address) {
        Objects.requireNonNull(address);
        // regexp check for validity

        synchronized (mutex) {
            if (registeredAddresses.contains(address)) {
                return false;
            }

            if (registeredAddresses.size() == MAX_POOL_SIZE) {
                throw new IllegalStateException("Pool size exhausted");
            }

            registeredAddresses.add(address);
        }

        return true;
    }

    private String[] makeSnapshot() {
        return registeredAddresses.toArray(new String[] {});
    }
}
