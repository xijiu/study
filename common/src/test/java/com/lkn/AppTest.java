package com.lkn;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        String abc = "a.b.c";
        String[] split = abc.split("\\.");
        System.out.println(Arrays.asList(split));
    }
}
