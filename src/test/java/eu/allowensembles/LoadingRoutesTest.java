package eu.allowensembles;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

import eu.allowensembles.presentation.main.map.Routes;
import eu.allowensembles.presentation.main.map.Routes.Route.Leg;
import eu.allowensembles.presentation.main.map.util.GoogleMapsDecoder;

public class LoadingRoutesTest {

    private static final String ROUTES_FILE = "/map/Storyboard1.xml";
    private File routesFile;
    private JAXBContext context;

    @Before
    public void setup() throws URISyntaxException, JAXBException {
	URL res = this.getClass().getResource(ROUTES_FILE);
	routesFile = new File(res.toURI());

	context = JAXBContext.newInstance(Routes.class);
    }

    @Test
    public void loadRouteTest() throws JAXBException {
	Routes r = (Routes) context.createUnmarshaller().unmarshal(routesFile);

	assertNotNull(r);
	assertNotNull(r.getRoute());
	assertNotNull(r.getRoute().get(0).getLeg());

	List<Leg> leg = r.getRoute().get(0).getLeg();

	assertNotNull(leg.get(0).getGeometry());

	GoogleMapsDecoder.decode(leg.get(0).getGeometry());
    }

}
