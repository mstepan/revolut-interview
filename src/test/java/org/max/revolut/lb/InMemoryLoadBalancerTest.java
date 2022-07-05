package org.max.revolut.lb;

import java.util.Optional;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class InMemoryLoadBalancerTest {

    @Test
    public void normalRegisterShouldBeFine() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);

        assertTrue(lb.register("192.168.1.1"));
        assertFalse(lb.register("192.168.1.1"));

        assertTrue(lb.register("255.168.1.1"));
    }

    @Test
    public void addNullAddresShoudlFail() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);
        assertThrows(IllegalArgumentException.class, () -> lb.register(null));
    }

    @Test
    public void addUpToPoolSizeShouldBeOk() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);

        for (int i = 0; i < 10; ++i) {
            assertTrue(lb.register(randomAddress()));
        }

    }

    @Test
    public void outOfPoolBoundaryShouldFail() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);

        for (int i = 0; i < 10; ++i) {
            assertTrue(lb.register(randomAddress()));
        }

        assertThrows(IllegalStateException.class, () -> lb.register(randomAddress()));

    }

    @Test
    public void getAnyNormalCase() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);

        lb.register("255.0.0.1");
        lb.register("255.0.0.1");
        lb.register("255.0.0.3");


        Optional<String> maybeValue = lb.getAny();
        assertNotNull(maybeValue);
        assertTrue(maybeValue.isPresent());

        String randVal = maybeValue.get();
        assertTrue(randVal.equals("255.0.0.1") || randVal.equals("255.0.0.2") || randVal.equals("255.0.0.3"));
    }

    @Test
    public void getRoundRobin() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);

        assertTrue(lb.getRoundRobin().isEmpty());

        lb.register("255.0.0.1");
        lb.register("255.0.0.2");
        lb.register("255.0.0.3");

        assertEquals("255.0.0.1", lb.getRoundRobin().get());
        assertEquals("255.0.0.2", lb.getRoundRobin().get());
        assertEquals("255.0.0.3", lb.getRoundRobin().get());
        assertEquals("255.0.0.1", lb.getRoundRobin().get());
    }

    @Test
    public void getAnyExhaustive() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);

        lb.register("255.0.0.1");
        lb.register("255.0.0.2");
        lb.register("255.0.0.3");

        Optional<String> maybeValue = lb.getAny();
        assertNotNull(maybeValue);
        assertTrue(maybeValue.isPresent());
        assertEquals("255.0.0.2", maybeValue.get());
    }

    @Test
    public void getAnyForEmptyLBShouldNotFail() {
        LoadBalancer lb = new InMemoryLoadBalancer(133L);
        Optional<String> maybeValue = lb.getAny();
        assertNotNull(maybeValue);
        assertTrue(maybeValue.isEmpty());
    }


    private static final Random RAND = new Random();

    private String randomAddress() {

        StringBuilder address = new StringBuilder(15);

        address.append(RAND.nextInt(256));

        for (int i = 0; i < 3; ++i) {
            address.append(".").append(RAND.nextInt(256));
        }
        return address.toString();
    }

}
