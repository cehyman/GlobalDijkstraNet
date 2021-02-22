import java.util.ArrayList;

public class GlobalNet
{
    //creates a global network 
    //O : the original graph
    //regions: the regional graphs
    public static Graph run(Graph O, Graph[] regions)
    {
        ArrayList<Edge> list = new ArrayList<>();
        for (int i = 0; i < regions.length-1; i++) {
            for (int j = i + 1; j < regions.length ; j++) {
                list.addAll(dijkstra(O, regions[i], regions[j]));
            }
        }
        Graph ret = new Graph(O.V());
        ret.setCodes(O.getCodes());
        for (Edge e: list) {
            ret.addEdge(e);
        }
        return ret;
    }

    private static ArrayList<Edge> dijkstra(Graph O, Graph region, Graph region2){
        int V = O.V(); //number of vertices
        int[] src = new int[region.V()]; //sets the source
        int[] dest = new int[region2.V()];
        int[] dist = new int[V];
        int[] prev = new int[V];
        DistQueue Q = new DistQueue(V);
        Graph ret = new Graph(O.V());
        ret.setCodes(O.getCodes());

        for (int v = 0; v < V; v++) {
             dist[v] = Integer.MAX_VALUE;
             prev[v] = -1; //set distance to "undefined"
             Q.insert(v, dist[v]); //add all the vertices into priority queue with distance as the key
        }

        for (int i = 0; i < region.V(); i++) {
            src[i] = O.index(region.getCode(i));
            dist[src[i]] = 0;//sets the distance in the region to 0
            Q.set(src[i], 0);
        }

        for (int i = 0; i < region2.V(); i++) {
            dest[i] = O.index(region2.getCode(i));

        }

        while(!Q.isEmpty()){ //until the priority queue is empty we..
             int u = Q.delMin(); //get min vertex
             ArrayList<Integer> adj = O.adj(u); //gets adjacent vertices of u
             for (int i = 0; i < adj.size(); i++) {
                if(Q.inQueue(adj.get(i))){ //checks if the adj vertexes are still in the queue
                    int alternate;
                    if(prev[u] != -1) { //if the vertex is not source then we get distance and add the new distance to the alternate distance
                        alternate = dist[u] + O.getEdgeWeight(u, adj.get(i));
                    }else { //if vertex is source then we just add edge weight to alternate distance
                        alternate = O.getEdgeWeight(u, adj.get(i));
                    }
                    if(alternate < dist[O.adj(u).get(i)]) { //if the alternate path distance is less than the current path distance then we swap them and update the queue
                        dist[adj.get(i)] = alternate;
                        prev[adj.get(i)] = u;
                        Q.set(adj.get(i), alternate);
                    }
                }
             }
        }

        int []pathW = new int[V];
        int x;
        int path = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < dest.length; i++) {
            x = dest[i];
            pathW[i] = 0;
            while (prev[x] != -1){
                pathW[i] += dist[x] - dist[prev[x]];
                x = prev[x];
            }
            if(pathW[i] < min){
                path = i;
                min = pathW[i];
            }
        }
        x = dest[path];
        while(prev[x] != -1){
            ret.addEdge(prev[x], x, O.getEdgeWeight(prev[x], x));
            x = prev[x];
        }
        ArrayList<Edge> list = ret.edges();
        list.addAll(region.edges());
        list.addAll(region2.edges());
        return list;
    }
}
    
    
    