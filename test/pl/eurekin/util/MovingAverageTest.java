package pl.eurekin.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Rekin
 */
public class MovingAverageTest {

    public MovingAverageTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddNext() {
        System.out.println("addThreeOnes");
        int windowSize = 10;
        MovingAverage instance = new MovingAverage(windowSize);
        for (int i = 0; i < windowSize; i++) {
            instance.addNext(1.0d);
            assertEquals(1.0d, instance.getAverage(), 1.0e-5);
        }
    }

    @Test
    public void testGetAverage() {
        double[] test = {1, 1, 2, 2, 3, 3};
        double[] result = {1, 1, 1.5, 2, 2.5, 3};
        MovingAverage ma = new MovingAverage(2);
        for (int i = 0; i < result.length; i++) {
            ma.addNext(test[i]);
            assertEquals(result[i], ma.getAverage(), 1.0e-5d);
        }
    }
}
