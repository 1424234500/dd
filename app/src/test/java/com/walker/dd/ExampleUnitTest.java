package com.walker.dd;

import com.walker.dd.service.NetModel;
import com.walker.dd.service.WebService;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testWeb(){
        NetModel.setServerWebIp("127.0.0.1");
        NetModel.setServerWebPort(9093);
        WebService.getInstance().getToken("001", "222");


    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}