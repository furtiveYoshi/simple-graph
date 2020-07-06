package com.natera.test;

import com.natera.testtask.simplegraph.enums.Direction;
import com.natera.testtask.simplegraph.Edge;
import com.natera.testtask.simplegraph.impl.concurrent.ImmutableEdge;
import com.natera.testtask.simplegraph.Graph;
import com.natera.testtask.simplegraph.impl.concurrent.ConcurrentGraph;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@RunWith(JUnit4.class)
public class ConcurrentGraphTest {
    String moscow = "Moscow";
    String spb = "Spb";
    String helsinki = "Helsinki";
    String tallinn = "Tallinn";
    String pskov = "Pskov";
    String minsk = "Minsk";

    Edge<String> mToS = new ImmutableEdge<>(moscow, spb, 1, Direction.UNDIRECTED);
    Edge<String> tToMi = new ImmutableEdge<>(tallinn, minsk, 1, Direction.UNDIRECTED);
    Edge<String> sToT = new ImmutableEdge<>(spb, tallinn, 1, Direction.UNDIRECTED);
    Edge<String> tToH = new ImmutableEdge<>(tallinn, helsinki, 1, Direction.UNDIRECTED);
    Edge<String> miToH = new ImmutableEdge<>(minsk, helsinki, 1, Direction.UNDIRECTED);
    Edge<String> sToP = new ImmutableEdge<>(spb, pskov, 1, Direction.UNDIRECTED);
    Edge<String> mToP = new ImmutableEdge<>(moscow, pskov, 1, Direction.UNDIRECTED);

    private class GraphFiller implements Callable<String> {
        ArrayList<String> vertices = new ArrayList<>();
        ArrayList<Edge<String>> edges = new ArrayList<>();
        Graph<String> graph;

        GraphFiller(Graph<String> graph, List<String> vertices, List<Edge<String>> edges) {
            this.graph = graph;
            this.vertices.addAll(vertices);
            this.edges.addAll(edges);
        }

        @Override
        public String call() throws InterruptedException {
            Random rand = new Random();
            for (String vertex : vertices) {
                graph.addVertex(vertex);
                System.out.println("vertex added " + vertex);
                Thread.sleep(rand.nextInt(30) + 50);
            }

            for (Edge<String> edge : edges) {
                graph.addEdge(edge);
                System.out.println("edge added " + edge);
                Thread.sleep(rand.nextInt(30) + 50);
            }
            return "Success";
        }
    }

    private class PathExecutor implements Callable<List<com.natera.testtask.simplegraph.Edge<String>>> {
        Graph<String> graph;
        String startVertex;
        String endVertex;

        public PathExecutor(Graph<String> graph, String startVertex, String endVertex) {
            this.graph = graph;
            this.startVertex = startVertex;
            this.endVertex = endVertex;
        }

        @Override
        public List<com.natera.testtask.simplegraph.Edge<String>> call() throws Exception {
            List<com.natera.testtask.simplegraph.Edge<String>> path = null;
            while (path == null || path.isEmpty()) {
                path = graph.getPath(startVertex, endVertex);

                System.out.println("path = " + path);
                Thread.sleep(30);
            }
            return path;
        }
    }

    @Test
    public void shouldFindPath() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Graph<String> graph = new ConcurrentGraph<>();

        GraphFiller graphFiller1 = new GraphFiller(graph, Arrays.asList(moscow, spb, pskov), Arrays.asList(mToS, mToP, sToP));

        GraphFiller graphFiller2 = new GraphFiller(graph, Arrays.asList(helsinki, tallinn, minsk), Arrays.asList(tToH, miToH, tToMi, sToT));

        PathExecutor pathExecutor1 = new PathExecutor(graph, moscow, spb);
        PathExecutor pathExecutor2 = new PathExecutor(graph, helsinki, tallinn);
        PathExecutor pathExecutor3 = new PathExecutor(graph, pskov, helsinki);

        Future<List<com.natera.testtask.simplegraph.Edge<String>>> path1 = executorService.submit(pathExecutor1);
        Future<List<com.natera.testtask.simplegraph.Edge<String>>> path2 = executorService.submit(pathExecutor2);
        Future<List<com.natera.testtask.simplegraph.Edge<String>>> path3 = executorService.submit(pathExecutor3);
        List<Future<String>> futures = executorService.invokeAll(Arrays.asList(graphFiller1, graphFiller2));

        Assert.assertEquals("Success", futures.get(0).get());
        Assert.assertEquals("Success", futures.get(1).get());

        Assert.assertTrue(
                path1.get().get(0).getFromVertex().equals(moscow) ||
                        path1.get().get(0).getToVertex().equals(moscow));
        Assert.assertTrue(
                path1.get().get(path1.get().size() - 1).getFromVertex().equals(spb) ||
                        path1.get().get(path1.get().size() - 1).getToVertex().equals(spb));

        Assert.assertTrue(
                path2.get().get(0).getFromVertex().equals(helsinki) ||
                        path2.get().get(0).getToVertex().equals(helsinki));
        Assert.assertTrue(
                path2.get().get(path2.get().size() - 1).getFromVertex().equals(tallinn) ||
                        path2.get().get(path2.get().size() - 1).getToVertex().equals(tallinn));

        Assert.assertTrue(
                path3.get().get(0).getFromVertex().equals(pskov) ||
                        path3.get().get(0).getToVertex().equals(pskov));
        Assert.assertTrue(
                path3.get().get(path3.get().size() - 1).getFromVertex().equals(helsinki) ||
                        path3.get().get(path3.get().size() - 1).getToVertex().equals(helsinki));

        executorService.shutdown();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullVertex(){
        com.natera.testtask.simplegraph.impl.Graph<String> graph = new com.natera.testtask.simplegraph.impl.Graph<>();
        graph.addVertex(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullEdge(){
        com.natera.testtask.simplegraph.impl.Graph<String> graph = new com.natera.testtask.simplegraph.impl.Graph<>();
        graph.addEdge(null);
    }
}
