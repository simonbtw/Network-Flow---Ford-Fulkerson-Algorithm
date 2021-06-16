package com.company;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Math.min;

public class Main {
    static class pair {
        int first, second;
        public pair(int first, int second) {
            this.first = first;
            this.second = second;
        }
        public Long getHash() {
            return (Long.valueOf(this.first) << 25) + Long.valueOf(this.second);
        }
    }
    // data structures for a network
    public static int n, s, t;
    public static HashMap<Long, Integer> graph = new HashMap<>();
    public static HashMap<Long, Integer> capacity = new HashMap<>();
    static int[] parent = new int[4000002];

    public static int bfs(int s, int t) {
        Arrays.fill(parent, -1);
        parent[s] = -2;
        Queue< int [] > q = new LinkedList<>();
        q.add(new int[]{s, Integer.MAX_VALUE});

        while (!q.isEmpty()) {
            int [] cur = q.remove();
            int u = cur[0];
            int flow = cur[1];

            for (int v = 0; v < n; v++) {
                if(graph.get(new pair(u, v).getHash()) == null) continue;
                if (parent[v] == -1 && capacity.get(new pair(u, v).getHash()) != null && capacity.get(new pair(u, v).getHash()) != 0) {
                    parent[v] = u; // saving shortest augmented path
                    int newFlow = min(flow, capacity.get(new pair(u, v).getHash()));
                    if (v == t) {
                        // found the shorted augmented path, return the flow of the path
                        return newFlow;
                    }
                    q.add(new int[]{v, newFlow});
                }
            }
        }

        return 0;
    }

    public static void maxFlow() {
        int maximumFlow = 0;
        int newFlow = bfs(s, t);
        int flowCount = 0;
        // continue relaxing until there founds an augmented path
        while (newFlow != 0) {
            flowCount += 1;
            maximumFlow += newFlow; // add the flow of the augmented path to the total flow
            ArrayList<Integer> path = new ArrayList<>();
            int cur = t;
            path.add(cur);
            while (cur != s) {
                int prev = parent[cur];
                // changing the residual network and main network through the augmented path
                if(capacity.get(new pair(prev, cur).getHash()) != null) {
                    capacity.put(new pair(prev, cur).getHash(), capacity.get(new pair(prev, cur).getHash()) - newFlow);
                }
                else {
                    capacity.put(new pair(prev, cur).getHash(), -newFlow);
                }
                if(capacity.get(new pair(cur, prev).getHash()) != null) {
                    capacity.put(new pair(cur, prev).getHash(), capacity.get(new pair(cur, prev).getHash()) + newFlow);
                }
                else {
                    capacity.put(new pair(cur, prev).getHash(), newFlow);
                }
                cur = prev;
                path.add(cur);
            }
            System.out.print("Augmenting Path - " + flowCount + ": ");
            for(int i = path.size() - 1; i >= 0; i--) {
                System.out.print(path.get(i) + " ");
            }
            System.out.println();
            System.out.println("Flow - " + flowCount + ": " + newFlow);
            newFlow = bfs(s, t);
        }

        System.out.println("Maximum Flow: " + maximumFlow);
    }

    public static void parser() {
        try {
            File myObj = new File("src/com/company/benchmarks/bridge_7.txt");
            Scanner sc = new Scanner(myObj);
            n = sc.nextInt(); // number of nodes
            while(sc.hasNextInt()) {
                // cap is the capacity of the edge between u and v
                int u = sc.nextInt();
                int v = sc.nextInt();
                int cap = sc.nextInt();
                graph.put(new pair(u, v).getHash(), 1);
                graph.put(new pair(v, u).getHash(), 1);
                if(capacity.get(new pair(u, v).getHash()) == null) {
                    capacity.put(new pair(u, v).getHash(), cap);
                }
                else capacity.put(new pair(u, v).getHash(), capacity.get(new pair(u, v).getHash()) + cap);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("invalid file");
        }
    }

    public static void main(String[] args) throws IOException {
        parser();
        s = 0;
        t = n - 1;
        maxFlow();
    }
}
