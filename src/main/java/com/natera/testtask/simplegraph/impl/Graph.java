package com.natera.testtask.simplegraph.impl;

import com.natera.testtask.simplegraph.Edge;
import com.natera.testtask.simplegraph.enums.Direction;

import java.util.*;
import java.util.function.Consumer;

public class Graph<T> implements com.natera.testtask.simplegraph.Graph<T> {

    private final Map<T, Set<Edge<T>>> adjacencyMatrix = new HashMap<>();
    private final Set<T> visitedVertices = new HashSet<>();

    @Override
    public void addVertex(T vertex) {
        adjacencyMatrix.putIfAbsent(Optional.ofNullable(vertex).orElseThrow(() -> new NullPointerException("Vertex must be not null")), new HashSet<>());
    }

    @Override
    public void addEdge(Edge<T> edge) {
        Edge<T> notNullableEdge = Optional.ofNullable(edge).orElseThrow(() -> new NullPointerException("Edge must be not null"));
        adjacencyMatrix.computeIfPresent(notNullableEdge.getFromVertex(),
                (vertex, edges) -> {
                    edges.add(edge);
                    return edges;
                });
        if (edge.getDirection() == Direction.UNDIRECTED) {
            adjacencyMatrix.computeIfPresent(notNullableEdge.getToVertex(),
                    (vertex, edges) -> {
                        edges.add(edge);
                        return edges;
                    });
        }
    }

    @Override
    public List<Edge<T>> getPath(T fromVertex, T toVertex) {
        visitedVertices.add(fromVertex);
        List<Edge<T>> result = getPathRecursively(fromVertex, toVertex);
        visitedVertices.clear();
        return result;
    }


    private List<Edge<T>> getPathRecursively(T fromVertex, T toVertex) {
        List<Edge<T>> result = new ArrayList<>();
        System.out.println(String.format("start search path from %s to %s", fromVertex, toVertex));
        System.out.println("take vertex: " + fromVertex);
        for (Edge<T> edge : Optional.ofNullable(adjacencyMatrix.get(fromVertex)).orElse(new HashSet<>())) {
            System.out.println("Visited vertex: " + visitedVertices);
            System.out.println("take edge: " + edge);
            T vertex = edge.getDirection() == Direction.ONE_WAY ?
                    edge.getToVertex() :
                    (edge.getFromVertex().equals(fromVertex) ? edge.getToVertex() : edge.getFromVertex());
            if (!visitedVertices.add(vertex)) {
                continue;
            }

            if (vertex.equals(toVertex)) {
                System.out.println("Find end of path");
                result.add(edge);
                break;
            }

            System.out.println("go deeper");
            List<Edge<T>> path = getPath(vertex, toVertex);
            if (!path.isEmpty()) {
                result.add(edge);
                result.addAll(path);
                break;
            }
        }
        System.out.println("go upper with edges: " + result);
        return result;
    }

}
