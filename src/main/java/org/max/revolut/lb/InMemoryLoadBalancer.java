package org.max.revolut.lb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;


public class InMemoryLoadBalancer implements LoadBalancer {

    private static final int MAX_POOL_SIZE = 10;

    private final List<String> registeredAddresses = new ArrayList<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock readLock = lock.readLock();

    private final Lock writeLock = lock.writeLock();

    private final AtomicInteger curIndex = new AtomicInteger(-1);

    private final Random rand;


    public InMemoryLoadBalancer(long initialSeed) {
        this.rand = new Random(initialSeed);
    }


    /**
     * time: O(1)
     * space: O(1)
     */
    @Override
    public Optional<String> getAny() {

        readLock.lock();

        try {
            if (registeredAddresses.isEmpty()) {
                return Optional.empty();
            }

            int randIndex = rand.nextInt(registeredAddresses.size());

            return Optional.of(registeredAddresses.get(randIndex));
        }
        finally {
            readLock.unlock();
        }
    }

    /**
     * time: O(1)
     * space: O(1)
     */
    @Override
    public Optional<String> getRoundRobin() {

        readLock.lock();

        try {
            if (registeredAddresses.isEmpty()) {
                return Optional.empty();
            }
            int index = curIndex.updateAndGet(prevValue -> (prevValue + 1) % registeredAddresses.size());
            return Optional.of(registeredAddresses.get(index));
        }
        finally {
            readLock.unlock();
        }
    }

    /**
     * time: O(N)
     * space: O(1)
     */
    @Override
    public boolean register(String address) {
        checkNotNull(address);
        checkValidAddress(address);

        readLock.lock();
        try {
            if (registeredAddresses.contains(address)) {
                return false;
            }
            if (registeredAddresses.size() == MAX_POOL_SIZE) {
                throw new IllegalStateException("Pool size exhausted");
            }
        }
        finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            if (registeredAddresses.contains(address)) {
                return false;
            }
            if (registeredAddresses.size() == MAX_POOL_SIZE) {
                throw new IllegalStateException("Pool size exhausted");
            }

            registeredAddresses.add(address);
        }
        finally {
            writeLock.unlock();
        }

        return true;
    }

    private static final Pattern ADDRESS_REGEXP = Pattern.compile("[\\d]{1,3}[.][\\d]{1,3}[.][\\d]{1,3}[.][\\d]{1,3}");

    private void checkValidAddress(String address) {
        if (!ADDRESS_REGEXP.matcher(address).matches()) {
            throw new IllegalArgumentException("Incorrect 'address' detected: " + address);
        }
    }

    private void checkNotNull(String address) {
        if (address == null) {
            throw new IllegalArgumentException("'address' can't be null");
        }
    }
}
