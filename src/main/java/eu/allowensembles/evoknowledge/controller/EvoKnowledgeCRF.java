package eu.allowensembles.evoknowledge.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.utils.Alternative;
import eu.allowensembles.utils.ResourceLoader;

public class EvoKnowledgeCRF implements IUtilityParameterEstimator, IEvoKnowledgeCRFModel {
	// Mapping of alternative traces to segments in CRF graph.
	private static Map<String, List<String>> traceMapping;

	// Shared CRF/StreetMap for efficiency.
	private static Graph crfShared;

	// Static constructor is used to initialize all shared members.
	static {
		// Load crf
		URL resource = ResourceLoader.class.getResource("/evo/crf");
		try {
			Path file = Paths.get(resource.toURI());
			crfShared = loadCRFModel(file);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		resource = ResourceLoader.class.getResource("/evo/mapping");
		try {
			Path file = Paths.get(resource.toURI());
			traceMapping = loadTraceMapping(file);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		// Load graphical model.
		/*
		 * String f =
		 * EvoKnowledgeCRF.class.getResource("/evo/crf").getFile().toString();
		 * // f = removeStartingBar(f); Path file = Paths.get(f);
		 * 
		 * // Path modelPath = // Paths.get(
		 * "/Users/Andi/Documents/otp/AllowFinalDemonstrator/src/main/resources/evo/crf"
		 * ); crfShared = loadCRFModel(file);
		 * 
		 * // Load traces. f =
		 * EvoKnowledgeCRF.class.getResource("/evo/mapping").getFile()
		 * .toString(); //f = removeStartingBar(f); file = Paths.get(f); // Path
		 * tracesPath = // Paths.get(
		 * "/Users/Andi/Documents/otp/AllowFinalDemonstrator/src/main/resources/evo/mapping"
		 * ); traceMapping = loadTraceMapping(file);
		 * 
		 * // Load prediction model. // predictionModel = new HashMap<Integer,
		 * Alternative>();
		 */
	}

	private static Graph loadCRFModel(Path path) {
		Graph crfShared = new MultiGraph("EvoKnowlegde CRF");
		crfShared.setStrict(false);
		crfShared.setAutoCreate(true);

		List<String> lines = null;
		try {
			lines = Files.readAllLines(path);

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lines == null)
			return null;
		int offset = 0;

		// Read nodes.
		String headerNodes = lines.get(offset++);
		String tokens[] = headerNodes.split(" ");

		int numberOfNodes = Integer.parseInt(tokens[1]);

		for (int i = 0; i < numberOfNodes; i++) {
			String temp = lines.get(offset++);
			tokens = temp.split(";;");
			Coordinate c = new Coordinate(Double.parseDouble(tokens[1]),
					Double.parseDouble(tokens[2]));
			Node n = crfShared.addNode(tokens[0]);
			n.setAttribute("x", c.x);
			n.setAttribute("y", c.y);
		}
		offset++;

		// Read links.
		String headerLinks = lines.get(offset++);
		tokens = headerLinks.split(" ");
		int numberOfLinks = Integer.parseInt(tokens[1]);

		for (int i = 0; i < numberOfLinks; i++) {
			String temp = lines.get(offset++);
			tokens = temp.split(";;");
			String subSegs[] = tokens[5].split(" ");

			for (int j = 0; j < subSegs.length - 1; j++) {
				Node start = crfShared.getNode(subSegs[j]);
				Node end = crfShared.getNode(subSegs[j + 1]);

				if (start == null || end == null)
					throw new IllegalArgumentException();

				crfShared.addEdge(subSegs[j] + ";;" + subSegs[j + 1], start,
						end);
				crfShared.addEdge(subSegs[j + 1] + ";;" + subSegs[j], end,
						start);
			}
		}
		return crfShared;
	}

	private static String removeStartingBar(String f) {
		if (f.startsWith("/")) {
			f = f.substring(1, f.length());
		}
		return f;
	}

	private static Map<String, List<String>> loadTraceMapping(Path path) {
		Map<String, List<String>> traceMapping = new HashMap<String, List<String>>();
		DirectoryStream<Path> str = null;
		try {
			str = Files.newDirectoryStream(path);

			for (Path file : str) {
				String filename = file.getFileName().toString();
				String tokens[] = filename.split(" ");

				if (!tokens[0].equals("trace"))
					continue;

				traceMapping.put(tokens[1], Files.readAllLines(file));
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {

			if (str != null) {
				try {
					str.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return traceMapping;
	}
	
	// Graphical CRF model (essentially the street graph).
	private Graph crfModel;

	public EvoKnowledgeCRF() {
		crfModel = crfShared;
	}

	public Graph getModel() {
		return crfModel;
	}

	public List<Segment> getSegments(Alternative alt) {
		List<Segment> segments = new ArrayList<Segment>();

		for (int i = 0; i < alt.getLegs().size(); i++) {
			List<String> segs = getTraceMapping(alt.getId() + "," + i);
			List<Segment> legSegments = new ArrayList<Segment>(segs.size());

			for (String seg : segs) {
				String tokens[] = seg.split(";;");
				Node n1 = crfModel.getNode(tokens[0]);
				Coordinate c1 = new Coordinate((double) n1.getAttribute("x"),
						(double) n1.getAttribute("y"));

				Node n2 = crfModel.getNode(tokens[1]);
				Coordinate c2 = new Coordinate((double) n2.getAttribute("x"),
						(double) n2.getAttribute("y"));
				legSegments.add(new Segment(seg, alt.getLegs().get(i)
						.getTransportType().getType(), c1.haversine(c2)));
			}

			double length = getLegLength(legSegments);
			double duration = alt.getLegs().get(i).getDuration();
			double costs = alt.getLegs().get(i).getCost();

			for (Segment s : legSegments) {
				s.setTravelTime(duration / length * s.getLength());
				s.setCosts(costs / length * s.getLength());
			}
			segments.addAll(legSegments);
		}
		return segments;
	}

	private static double getLegLength(List<Segment> leg) {
		double length = 0.0;

		for (Segment l : leg) {
			length += l.getLength();
		}
		return length;
	}

	private static List<String> getTraceMapping(String traceId) {
		List<String> trace = traceMapping.get(traceId);
		return trace;
	}

	private static final double CAR_FACTORS[][] = {
			{ 1.1, 1.5, 1.2, 1.5, 1.2, 1.0 },
			{ 1.15, 1.6, 1.1, 1.4, 1.3, 1.0 },
			{ 1.1, 1.6, 1.2, 1.6, 1.1, 1.0 },
			{ 1.15, 1.4, 1.3, 1.6, 1.3, 1.0 },
			{ 1.05, 1.5, 1.4, 1.3, 1.3, 1.3 },
			{ 1.05, 1.4, 1.5, 1.4, 1.2, 1.3 },
			{ 1.0, 1.3, 1.3, 1.2, 1.2, 1.0 }, };

	private static final double COST_FACTORS[][] = {
			{ 1.0, 1.05, 1.02, 1.05, 1.02, 1.0 },
			{ 1.01, 1.06, 1.01, 1.04, 1.03, 1.0 },
			{ 1.02, 1.06, 1.02, 1.06, 1.01, 1.0 },
			{ 1.02, 1.04, 1.03, 1.06, 1.03, 1.0 },
			{ 1.0, 1.05, 1.04, 1.03, 1.03, 1.03 },
			{ 1.0, 1.04, 1.05, 1.04, 1.02, 1.03 },
			{ 1.0, 1.03, 1.03, 1.02, 1.02, 1.0 }, };

	private static final double BUS_FACTORS[][] = {
			{ 1.0, 1.3, 1.2, 1.2, 1.2, 1.0 },
			{ 1.1, 1.2, 1.2, 1.2, 1.3, 1.05 },
			{ 1.1, 1.3, 1.1, 1.3, 1.3, 1.1 }, { 1.0, 1.3, 1.2, 1.3, 1.2, 1.1 },
			{ 1.15, 1.2, 1.3, 1.3, 1.3, 1.1 },
			{ 1.0, 1.25, 1.2, 1.3, 1.2, 1.3 },
			{ 1.0, 1.2, 1.1, 1.2, 1.2, 1.0 }, };

	private static final double WEATHER_FACTORS_WALK[] = { 1.0, 1.0, 1.05 };

	private static final double WEATHER_FACTORS_BIKE[] = { 1.0, 1.05, 1.1 };

	private static final double WEATHER_FACTORS_DRIVE[] = { 1.0, 1.02, 1.2 };

	private static Leg predictWalkLeg(Leg leg, Context context) {
		Leg predLeg = new Leg();
		int duration = (int) (leg.getDuration() * WEATHER_FACTORS_WALK[context
				.getWeather().ordinal()]);

		predLeg.setCost(leg.getCost());
		predLeg.setDuration(duration);
		predLeg.setGeometry(leg.getGeometry());
		predLeg.setTransportType(leg.getTransportType());
		predLeg.setWalkingDistance(leg.getWalkingDistance());
		return predLeg;
	}

	private static Leg predictBikeLeg(Leg leg, Context context) {
		Leg predLeg = new Leg();
		int duration = (int) (leg.getDuration() * WEATHER_FACTORS_BIKE[context
				.getWeather().ordinal()]);

		predLeg.setCost(leg.getCost());
		predLeg.setDuration(duration);
		predLeg.setGeometry(leg.getGeometry());
		predLeg.setTransportType(leg.getTransportType());
		predLeg.setWalkingDistance(leg.getWalkingDistance());
		return predLeg;
	}

	private static Leg predictCarLeg(Leg leg, Context context) {
		Leg predLeg = new Leg();
		int duration = (int) (leg.getDuration()
				* CAR_FACTORS[context.getWeekday().ordinal()][context
						.getTimeOfDay().ordinal()] * WEATHER_FACTORS_DRIVE[context
				.getWeather().ordinal()]);
		double costs = leg.getCost()
				* COST_FACTORS[context.getWeekday().ordinal()][context
						.getTimeOfDay().ordinal()];

		predLeg.setCost(costs);
		predLeg.setDuration(duration);
		predLeg.setGeometry(leg.getGeometry());
		predLeg.setTransportType(leg.getTransportType());
		predLeg.setWalkingDistance(leg.getWalkingDistance());
		return predLeg;
	}

	private static Leg predictBusLeg(Leg leg, Context context) {
		Leg predLeg = new Leg();
		int duration = (int) (leg.getDuration()
				* BUS_FACTORS[context.getWeekday().ordinal()][context
						.getTimeOfDay().ordinal()] * WEATHER_FACTORS_DRIVE[context
				.getWeather().ordinal()]);
		double costs = leg.getCost();

		predLeg.setCost(costs);
		predLeg.setDuration(duration);
		predLeg.setGeometry(leg.getGeometry());
		predLeg.setTransportType(leg.getTransportType());
		predLeg.setWalkingDistance(leg.getWalkingDistance());
		return predLeg;
	}

	private static Leg predictTrainLeg(Leg leg, Context context) {
		Leg predLeg = new Leg();
		int duration = leg.getDuration();
		double costs = leg.getCost();

		predLeg.setCost(costs);
		predLeg.setDuration(duration);
		predLeg.setGeometry(leg.getGeometry());
		predLeg.setTransportType(leg.getTransportType());
		predLeg.setWalkingDistance(leg.getWalkingDistance());
		return predLeg;
	}

	private static Alternative createPredictedAlternative(Alternative alt,
			Context context) {
		List<Leg> predLegs = new ArrayList<Leg>(alt.getLegs().size());
		int totalDuration = 0;
		double totalCosts = 0.0;

		for (Leg leg : alt.getLegs()) {
			Leg predLeg = null;

			switch (leg.getTransportType().getType()) {

			case "walk":
				predLeg = predictWalkLeg(leg, context);
				break;

			case "car":
			case "carSharing":
			case "carsharing":
				predLeg = predictCarLeg(leg, context);
				break;

			case "bicycle":
				predLeg = predictBikeLeg(leg, context);
				break;

			case "bus":
			case "flexibus":
				predLeg = predictBusLeg(leg, context);
				break;

			case "train":
				predLeg = predictTrainLeg(leg, context);
				break;

			default:
				throw new IllegalArgumentException(
						"Error: Unknown transportation type "
								+ leg.getTransportType().getType());

			}
			totalDuration += predLeg.getDuration();
			totalCosts += predLeg.getCost();
			predLegs.add(predLeg);
		}
		return new Alternative(alt.getId(), alt.getNoOfChanges(),
				alt.getWalkingDistance(), totalDuration, totalCosts,
				alt.getModes(), predLegs, alt.getUtility());
	}
	
	public Alternative getPredictedAlternative(Alternative alt, Context context) {
		return createPredictedAlternative(alt, context);
	}

	@Override
	public double predictTravelTime(Alternative alternative) {
		Alternative temp = createPredictedAlternative(alternative,
				new Context());
		return temp.getTravelTime();
	}

}
