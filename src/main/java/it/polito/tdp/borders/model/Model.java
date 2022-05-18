package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {

	private BordersDAO bordersDAO;
	private List<Country> countries;
	private CountryIdMap countryIdMap;
	private SimpleGraph<Country, DefaultEdge> graph;

	public Model() {
		bordersDAO = new BordersDAO();
	}

	public void createGraph(int anno) {

		countryIdMap = new CountryIdMap();
		countries = bordersDAO.loadAllCountries(countryIdMap);

		List<Border> confini = bordersDAO.getCountryPairs(countryIdMap, anno);

		if (confini.isEmpty()) {
			throw new RuntimeException("No country pairs for specified year");
		}

		graph = new SimpleGraph<>(DefaultEdge.class);

		for (Border b : confini) {
			graph.addVertex(b.getC1());
			graph.addVertex(b.getC2());
			graph.addEdge(b.getC1(), b.getC2());
		}

		System.out.format("Inseriti: %d vertici, %d archi\n", graph.vertexSet().size(), graph.edgeSet().size());

		// Sort the countries
		countries = new ArrayList<>(graph.vertexSet());
		Collections.sort(countries);
	}

	public List<Country> getCountries() {
		if (countries == null) {
			return new ArrayList<Country>();
		}

		return countries;
	}

	public Map<Country, Integer> getCountryCounts() {
		if (graph == null) {
			throw new RuntimeException("Grafo non esistente");
		}

		Map<Country, Integer> stats = new HashMap<Country, Integer>();
		for (Country country : graph.vertexSet()) {
			stats.put(country, graph.degreeOf(country));
		}
		return stats;
	}

	public int getNumberOfConnectedComponents() {
		if (graph == null) {
			throw new RuntimeException("Grafo non esistente");
		}

		ConnectivityInspector<Country, DefaultEdge> ci = new ConnectivityInspector<Country, DefaultEdge>(graph);
		return ci.connectedSets().size();
	}

	public List<Country> getReachableCountries(Country selectedCountry) {

		if (!graph.vertexSet().contains(selectedCountry)) {
			throw new RuntimeException("Selected Country not in graph");
		}

		List<Country> reachableCountries = this.displayAllNeighboursIterative(selectedCountry);
		System.out.println("Reachable countries: " + reachableCountries.size());
		reachableCountries = this.displayAllNeighboursJGraphT(selectedCountry);
		System.out.println("Reachable countries: " + reachableCountries.size());
		reachableCountries = this.displayAllNeighboursRecursive(selectedCountry);
		System.out.println("Reachable countries: " + reachableCountries.size());

		return reachableCountries;
	}

	/*
	 * VERSIONE ITERATIVA
	 */
	private List<Country> displayAllNeighboursIterative(Country selectedCountry) {

		// Creo due liste: quella dei noti visitati ..
		List<Country> visited = new LinkedList<Country>();

		// .. e quella dei nodi da visitare
		List<Country> toBeVisited = new LinkedList<Country>();

		// Aggiungo alla lista dei vertici visitati il nodo di partenza.
		visited.add(selectedCountry);

		// Aggiungo ai vertici da visitare tutti i vertici collegati a quello inserito
		toBeVisited.addAll(Graphs.neighborListOf(graph, selectedCountry));

		while (!toBeVisited.isEmpty()) {

			// Rimuovi il vertice in testa alla coda
			Country temp = toBeVisited.remove(0);

			// Aggiungi il nodo alla lista di quelli visitati
			visited.add(temp);

			// Ottieni tutti i vicini di un nodo
			List<Country> listaDeiVicini = Graphs.neighborListOf(graph, temp);

			// Rimuovi da questa lista tutti quelli che hai già visitato..
			listaDeiVicini.removeAll(visited);

			// .. e quelli che sai già che devi visitare.
			listaDeiVicini.removeAll(toBeVisited);

			// Aggiungi i rimanenenti alla coda di quelli che devi visitare.
			toBeVisited.addAll(listaDeiVicini);
		}

		// Ritorna la lista di tutti i nodi raggiungibili
		return visited;
	}

	/*
	 * VERSIONE LIBRERIA JGRAPHT
	 */
	private List<Country> displayAllNeighboursJGraphT(Country selectedCountry) {

		List<Country> visited = new LinkedList<Country>();

		// Versione 1 : utilizzo un BreadthFirstIterator
//		GraphIterator<Country, DefaultEdge> bfv = new BreadthFirstIterator<Country, DefaultEdge>(graph,
//				selectedCountry);
//		while (bfv.hasNext()) {
//			visited.add(bfv.next());
//		}

		// Versione 2 : utilizzo un DepthFirstIterator
		GraphIterator<Country, DefaultEdge> dfv = new DepthFirstIterator<Country, DefaultEdge>(graph, selectedCountry);
		while (dfv.hasNext()) {
			visited.add(dfv.next());
		}

		return visited;
	}

	/*
	 * VERSIONE RICORSIVA
	 */
	private List<Country> displayAllNeighboursRecursive(Country selectedCountry) {

		List<Country> visited = new LinkedList<Country>();
		recursiveVisit(selectedCountry, visited);
		return visited;
	}

	private void recursiveVisit(Country n, List<Country> visited) {
		// Do always
		visited.add(n);

		// cycle
		for (Country c : Graphs.neighborListOf(graph, n)) {	
			// filter
			if (!visited.contains(c))
				recursiveVisit(c, visited);
				// DO NOT REMOVE!! (no backtrack)
		}
	}

}
