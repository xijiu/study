package com.lkn;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        String str = "aaaa:::  [DhtmlXQ_move_0_37_2]73632526636226362836727667556042[/DhtmlXQ_move_0_37_2][DhtmlXQ_comment2_44]双方大体均势。[/DhtmlXQ_comment2_44][DhtmlXQ_comment41]双方均势。[/DhtmlXQ_comment41][DhtmlXQ_comment11]如图形势，黑方可走：||（1）马3进2||（2）炮2进2||（3）象3进5||具体走法分演如下。[/DhtmlXQ_comment11]";
        int beginIndex = str.indexOf("DhtmlXQ_comment41]") + "DhtmlXQ_comment41]".length();
        int endIndex = str.lastIndexOf("[/DhtmlXQ_comment41");
        String substring = str.substring(beginIndex, endIndex);
        System.out.println(substring);
    }
}
