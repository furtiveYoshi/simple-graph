package com.natera.test;

import com.natera.testtask.simplegraph.enums.Direction;
import com.natera.testtask.simplegraph.impl.Edge;
import com.natera.testtask.simplegraph.impl.Graph;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class GraphTest {
    String moscow = "Moscow";
    String spb = "Spb";
    String helsinki = "Helsinki";
    String tallinn = "Tallinn";
    String pskov = "Pskov";
    String minsk = "Minsk";

    Edge<String> mToS = new Edge<>(moscow, spb, 1, Direction.UNDIRECTED);
    Edge<String> sToMi = new Edge<>(spb, minsk, 1, Direction.UNDIRECTED);
    Edge<String> tToMi = new Edge<>(tallinn, minsk, 1, Direction.UNDIRECTED);
    Edge<String> sToT = new Edge<>(spb, tallinn, 1, Direction.UNDIRECTED);
    Edge<String> sToH = new Edge<>(spb, helsinki, 1, Direction.UNDIRECTED);
    Edge<String> tToH = new Edge<>(tallinn, helsinki, 1, Direction.UNDIRECTED);
    Edge<String> miToH = new Edge<>(minsk, helsinki, 1, Direction.UNDIRECTED);
    Edge<String> sToP = new Edge<>(spb, pskov, 1, Direction.UNDIRECTED);
    Edge<String> mToP = new Edge<>(moscow, pskov, 1, Direction.UNDIRECTED);
    Edge<String> pToMi = new Edge<>(pskov, minsk, 1, Direction.UNDIRECTED);
    Edge<String> mToMi = new Edge<>(moscow, minsk, 1, Direction.UNDIRECTED);

    Edge<String> pToMiOneWay = new Edge<>(pskov, minsk, 1, Direction.ONE_WAY);
    Edge<String> mToPOneWay = new Edge<>(moscow, pskov, 1, Direction.ONE_WAY);
    Edge<String> sToMiOneWay = new Edge<>(spb, minsk, 1, Direction.ONE_WAY);
    Edge<String> mToSOneWay = new Edge<>(moscow, spb, 1, Direction.ONE_WAY);

    @Test
    public void shouldFindPath(){
        Graph<String> graph = new Graph<>();

        graph.addVertex(moscow);
        graph.addVertex(spb);
        graph.addVertex(helsinki);
        graph.addVertex(tallinn);
        graph.addVertex(pskov);
        graph.addVertex(minsk);

        graph.addEdge(mToS);
        graph.addEdge(sToH);
        graph.addEdge(sToT);
        graph.addEdge(sToP);
        graph.addEdge(mToP);
        graph.addEdge(tToH);
        graph.addEdge(sToMi);
        graph.addEdge(tToMi);
        graph.addEdge(miToH);
        graph.addEdge(pToMi);
        graph.addEdge(mToMi);

        List<com.natera.testtask.simplegraph.Edge<String>> paths = graph.getPath(moscow, tallinn);
        Assert.assertFalse(paths.isEmpty());
        Assert.assertTrue(paths.get(0).getFromVertex().equals(moscow) || paths.get(0).getToVertex().equals(moscow));
        Assert.assertTrue(paths.get(paths.size()-1).getFromVertex().equals(tallinn) || paths.get(paths.size()-1).getToVertex().equals(tallinn));
    }

    @Test
    public void shouldNotFindPathToVertexWithoutEdges(){
        Graph<String> graph = new Graph<>();

        graph.addVertex(moscow);
        graph.addVertex(spb);
        graph.addVertex(helsinki);

        graph.addEdge(mToS);

        List<com.natera.testtask.simplegraph.Edge<String>> paths = graph.getPath(moscow, helsinki);
        Assert.assertTrue(paths.isEmpty());
    }

    @Test
    public void shouldFindPathIndDirectedGraph(){
        Graph<String> graph = new Graph<>();

        graph.addVertex(moscow);
        graph.addVertex(pskov);
        graph.addVertex(minsk);
        graph.addVertex(spb);

        graph.addEdge(pToMiOneWay);
        graph.addEdge(mToPOneWay);
        graph.addEdge(sToMiOneWay);
        graph.addEdge(mToSOneWay);

        List<com.natera.testtask.simplegraph.Edge<String>> paths = graph.getPath(moscow, spb);
        Assert.assertFalse(paths.isEmpty());
        Assert.assertTrue(paths.get(0).getFromVertex().equals(moscow) || paths.get(0).getToVertex().equals(moscow));
        Assert.assertTrue(paths.get(paths.size()-1).getFromVertex().equals(spb) || paths.get(paths.size()-1).getToVertex().equals(spb));
    }

    @Test
    public void shouldNotFindPathIndDirectedGraph(){
        Graph<String> graph = new Graph<>();

        graph.addVertex(moscow);
        graph.addVertex(pskov);
        graph.addVertex(minsk);
        graph.addVertex(spb);

        graph.addEdge(pToMiOneWay);
        graph.addEdge(mToPOneWay);
        graph.addEdge(sToMiOneWay);

        List<com.natera.testtask.simplegraph.Edge<String>> paths = graph.getPath(moscow, spb);
        Assert.assertTrue(paths.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullVertex(){
        Graph<String> graph = new Graph<>();
        graph.addVertex(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullEdge(){
        Graph<String> graph = new Graph<>();
        graph.addEdge(null);
    }
}
