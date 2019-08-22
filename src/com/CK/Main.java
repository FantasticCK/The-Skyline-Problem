package com.CK;

import java.util.*;


public class Main {

    public static void main(String[] args) {
        int[][] buildings = {{2, 9, 10}, {3, 7, 15}, {5, 12, 12}, {15, 20, 10}, {19, 24, 8}};
        new Solution().getSkyline(buildings);
    }
}

//Divide and Conquer
class Solution {
    /**
     * Divide-and-conquer algorithm to solve skyline problem,
     * which is similar with the merge sort algorithm.
     */
    public List<List<Integer>> getSkyline(int[][] buildings) {
        int n = buildings.length;
        List<List<Integer>> output = new ArrayList<List<Integer>>();

        // The base cases
        if (n == 0) return output;
        if (n == 1) {
            int xStart = buildings[0][0];
            int xEnd = buildings[0][1];
            int y = buildings[0][2];

            output.add(new ArrayList<Integer>() {{
                add(xStart);
                add(y);
            }});
            output.add(new ArrayList<Integer>() {{
                add(xEnd);
                add(0);
            }});
            // output.add(new int[]{xStart, y});
            // output.add(new int[]{xEnd, 0});
            return output;
        }

        // If there is more than one building,
        // recursively divide the input into two subproblems.
        List<List<Integer>> leftSkyline, rightSkyline;
        leftSkyline = getSkyline(Arrays.copyOfRange(buildings, 0, n / 2));
        rightSkyline = getSkyline(Arrays.copyOfRange(buildings, n / 2, n));

        // Merge the results of subproblem together.
        return mergeSkylines(leftSkyline, rightSkyline);
    }

    /**
     * Merge two skylines together.
     */
    public List<List<Integer>> mergeSkylines(List<List<Integer>> left, List<List<Integer>> right) {
        int nL = left.size(), nR = right.size();
        int pL = 0, pR = 0;
        int currY = 0, leftY = 0, rightY = 0;
        int x, maxY;
        List<List<Integer>> output = new ArrayList<List<Integer>>();

        // while we're in the region where both skylines are present
        while ((pL < nL) && (pR < nR)) {
            List<Integer> pointL = left.get(pL);
            List<Integer> pointR = right.get(pR);
            // pick up the smallest x
            if (pointL.get(0) < pointR.get(0)) {
                x = pointL.get(0);
                leftY = pointL.get(1);
                pL++;
            } else {
                x = pointR.get(0);
                rightY = pointR.get(1);
                pR++;
            }
            // max height (i.e. y) between both skylines
            maxY = Math.max(leftY, rightY);
            // update output if there is a skyline change
            if (currY != maxY) {
                updateOutput(output, x, maxY);
                currY = maxY;
            }
        }

        // there is only left skyline
        appendSkyline(output, left, pL, nL, currY);

        // there is only right skyline
        appendSkyline(output, right, pR, nR, currY);

        return output;
    }

    /**
     * Update the final output with the new element.
     */
    public void updateOutput(List<List<Integer>> output, int x, int y) {
        // if skyline change is not vertical -
        // add the new point
        if (output.isEmpty() || output.get(output.size() - 1).get(0) != x)
            output.add(new ArrayList<Integer>() {{
                add(x);
                add(y);
            }});
            // if skyline change is vertical -
            // update the last point
        else {
            output.get(output.size() - 1).set(1, y);
        }
    }

    /**
     * Append the rest of the skyline elements with indice (p, n)
     * to the final output.
     */
    public void appendSkyline(List<List<Integer>> output, List<List<Integer>> skyline,
                              int p, int n, int currY) {
        while (p < n) {
            List<Integer> point = skyline.get(p);
            int x = point.get(0);
            int y = point.get(1);
            p++;

            // update output
            // if there is a skyline change
            if (currY != y) {
                updateOutput(output, x, y);
                currY = y;
            }
        }
    }
}

//TreeMap
class Solution2 {
    public List<List<Integer>> getSkyline(int[][] buildings) {
        List<int[]> points = new ArrayList<>();
        for (int[] b : buildings) {
            // start point has negative height value
            points.add(new int[]{b[0], -b[2]});
            // end point has normal height value
            points.add(new int[]{b[1], b[2]});
        }
        Collections.sort(points, (p1, p2) -> {
            if (p1[0] == p2[0]) return p1[1] - p2[1];
            else return p1[0] - p2[0];
        });
        // Use a maxHeap to store possible heights
        // But priority queue does not support remove in lgn time
        // treemap support add, remove, get max in lgn time, so use treemap here
        // key: height, value: number of this height
        List<List<Integer>> result = new ArrayList<>();
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        // Before starting, the previous max height is 0;
        treeMap.put(0, 1);
        int prev = 0;
        for (int[] point : points) {
            if (point[1] < 0)
                treeMap.put(-point[1], treeMap.getOrDefault(-point[1], 0) + 1);//reach a new rectangle, height count+1
            else {
                treeMap.put(point[1], treeMap.getOrDefault(point[1], 0) - 1);//leave a new rectangle, height count-1
                if (treeMap.get(point[1]) == 0) treeMap.remove(point[1]);
            }
            int cur = treeMap.lastKey();
            // compare current max height with previous max height, update result and
            // previous max height if necessary
            if (prev != cur) {
                result.add(new ArrayList<>());
                result.get(result.size() - 1).add(point[0]);
                result.get(result.size() - 1).add(cur);
                prev = cur;
            }
        }
        return result;
    }
}
