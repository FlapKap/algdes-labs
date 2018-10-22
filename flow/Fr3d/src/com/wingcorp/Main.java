package com.wingcorp;

import com.wingcorp.GraphUtils.FileParser;
import com.wingcorp.GraphUtils.MaxFlowUndirected;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
	    var g = FileParser.Parse(args[0]);
        var mf = new MaxFlowUndirected(g, 0, g.V() - 1);
        mf.printValue();
    }
}
