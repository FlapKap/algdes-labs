package com.wingcorp;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 2) {
            System.out.println(
                    "Usage of this program: java Main <Path to cost matrix file> <One or more paths to sequence files>"
            );
            return;
        }

        CostMatrix costMatrix = CostMatrixParser.ParseCostMatrix(args[0]);
        List<Species> species = SpeciesParser.ParseSpecies(args[1]);
        long start = System.currentTimeMillis();
        for (int i = 0; i < species.size(); i++) {
            var s1 = species.get(i);

            for (int j = i + 1; j < species.size(); j++) {
                var s2 = species.get(j);

                Run(s1, s2, costMatrix);
            }
        }
        System.out.println(System.currentTimeMillis() - start);

    }

    public static void Run(Species si, Species sj, CostMatrix costMatrix){
        Pair<Integer>[][] M = new Pair[si.characters.length + 1][sj.characters.length + 1];
        HashMap<Pair<Integer>, Pair<Integer>> known = new HashMap<>();
        for (int i = 0; i <= si.characters.length; i++) {
            M[i][0] = new Pair<Integer>(i,0,-2 * i);
        }
        for (int j = 0; j <= sj.characters.length; j++) {
            M[0][j] = new Pair<Integer>(0,j,-2 * j);
        }

        for (int i = si.characters.length; i > 0; i--) {
            for (int j = sj.characters.length; j > 0; j--) {
                Opt(si, sj, costMatrix, M, known, i, j);
            }
        }

        //System.out.println(si.name + " AND " + sj.name);
        //for (int i = 0; i < M.length; i++) {
        //    //System.out.println(Arrays.toString(M[i]));
        //}

        //var ii = si.characters.length;
        //var ij = sj.characters.length;
        //var str1 = "";
        //var str2 = "";
//
        //while(ii >= 0 && ij >= 0) {
        //    // Get the pair
        //    System.out.println(ii + "::" + ij);
        //    var p = M[ii][ij];
//
        //    str1 += si.characters[ii];
        //    str2 += sj.characters[ij];
//
        //    // Get the next
        //    var next = known.get(p);
        //    ii = next.f;
        //    ij = next.s;
        //}
        //System.out.println(str1);
        //System.out.println(str2);
        //System.out.println("\n===========================\n");
    }

    public static Pair<Integer> Opt(Species si, Species sj, CostMatrix costMatrix, Pair<Integer>[][] M, HashMap<Pair<Integer>, Pair<Integer>> known, int i, int j) {
        if (known.containsKey(M[i][j])) {
            return M[i][j];
        }

        var gap = -4;

        if (i < 1 && j > 0){
            var op = Opt(si, sj, costMatrix, M, known, i, j - 1);
            var cost = gap + op.val;
            var p = new Pair(i, j, cost);
            M[i][j] = p;
            known.put(p, op);
        } else if (j < 1 && i > 0) {
            var op = Opt(si, sj, costMatrix, M, known, i - 1, j);
            var cost = gap + op.val;
            var p = new Pair(i, j, cost);
            M[i][j] = p;
            known.put(p, op);
        } else if (j > 0 && i > 0){
            var c1 = si.characters[i - 1];
            var c2 = sj.characters[j - 1];
            var a = costMatrix.Cost(c1, c2);

            var op1 = Opt(si, sj, costMatrix, M, known, i - 1, j - 1);
            var cost1 = a + op1.val;
            var op2 = Opt(si, sj, costMatrix, M, known, i, j - 1);
            var cost2 = gap + op2.val;
            var op3 = Opt(si, sj, costMatrix, M, known, i - 1, j);
            var cost3 = gap + op3.val;

            var max = Math.max(cost1, Math.max(cost2, cost3));

            if (cost1 == max){
                var p = new Pair(i, j, cost1);
                M[i][j] = p;
                known.put(p, op1);
            } else if (cost2 == max){
                var p = new Pair(i, j, cost2);
                M[i][j] = p;
                known.put(p, op2);
            } else if (cost3 == max){
                var p = new Pair(i, j, cost3);
                M[i][j] = p;
                known.put(p, op3);
            }
        }

        return M[i][j];
    }
}

class Pair<T>{
    public T f;
    public T s;
    public Integer val;

    public Pair(T f, T s, Integer val) {
        this.f = f;
        this.s = s;
        this.val = val;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair p2 = (Pair)o;
        return (p2.f == f && p2.s == s);
    }

    @Override
    public String toString() {
        return "(" + f + "," + s + ": " + val + ")";
    }
}
