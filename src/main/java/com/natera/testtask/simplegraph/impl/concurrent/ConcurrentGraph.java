package com.natera.testtask.simplegraph.impl.concurrent;

import com.natera.testtask.simplegraph.enums.Direction;
import com.natera.testtask.simplegraph.Edge;
import com.natera.testtask.simplegraph.Graph;
import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConcurrentGraph<T extends Serializable> implements Graph<T> {

    private final ConcurrentHashMap<T, Set<Edge<T>>> adjacencyMatrix = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    @Override
    public void addVertex(T vertex) {
        T copyOfVertex = (T) SerializationUtils.clone(Optional.ofNullable(vertex).orElseThrow(() -> new NullPointerException("Vertex must be not null")));
        synchronized (lock) {
            adjacencyMatrix.putIfAbsent(copyOfVertex, ConcurrentHashMap.newKeySet());
        }
    }

    @Override
    public void addEdge(Edge<T> edge) {
        Edge<T> notNullableEdge = Optional.ofNullable(edge).orElseThrow(() -> new NullPointerException("Edge must be not null"));
        synchronized (lock) {
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
    }

    @Override
    public List<Edge<T>> getPath(T fromVertex, T toVertex) {
        Set<T> visitedVertices = ConcurrentHashMap.newKeySet();
        Map<T, Set<Edge<T>>> matrixCopy = getCopyOfAdjacencyMatrix();
        return getPathRecursively(fromVertex, toVertex, visitedVertices, matrixCopy);
    }

    private List<Edge<T>> getPathRecursively(T fromVertex, T toVertex, Set<T> visitedVertices, Map<T, Set<Edge<T>>> matrix) {
        List<Edge<T>> result = new ArrayList<>();
        System.out.println(String.format("start search path from %s to %s", fromVertex, toVertex));
        System.out.println("take vertex: " + fromVertex);
        visitedVertices.add(fromVertex);
        for (Edge<T> edge : Optional.ofNullable(matrix.get(fromVertex)).orElse(ConcurrentHashMap.newKeySet())) {
            System.out.println("Visited vertex: " + visitedVertices);
            System.out.println("take edge: " + edge);
            T edgeFromVertex = edge.getFromVertex();
            T edgeToVertex = edge.getToVertex();
            T vertex = edge.getDirection() == Direction.ONE_WAY ?
                    edgeToVertex :
                    (edgeFromVertex.equals(fromVertex) ? edgeToVertex : edgeFromVertex);
            if (!visitedVertices.add(vertex)) {
                continue;
            }

            if (vertex.equals(toVertex)) {
                System.out.println("Find end of path:" + vertex);
                result.add(edge);
                break;
            }

            System.out.println("go deeper");
            List<Edge<T>> path = getPathRecursively(vertex, toVertex, visitedVertices, matrix);
            if (!path.isEmpty()) {
                result.add(edge);
                result.addAll(path);
                break;
            }
        }
        System.out.println("go upper with edges: " + result);
        return result;
    }

    private Map<T, Set<Edge<T>>> getCopyOfAdjacencyMatrix (){
        synchronized (lock) {
            return adjacencyMatrix.entrySet().parallelStream()
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    value -> {
                                        Set<Edge<T>> result = ConcurrentHashMap.newKeySet();
                                        result.addAll(value.getValue());
                                        return result;
                                    }
                            )
                    );
        }
    }
}
