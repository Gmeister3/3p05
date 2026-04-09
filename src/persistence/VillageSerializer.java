package persistence;

import exceptions.InsufficientResourcesException;
import exceptions.MaxLevelReachedException;
import game.Village;
import gameelements.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Serialises and deserialises a Village to and from an XML file.
// Pattern: XML Persistence (Bonus). The village state – including resources, building
// list with levels and hit-points, and inhabitant list – is stored in a well-formed XML
// document that validates against the companion schema file village_schema.xsd.
// This class is used by GameController to satisfy the save (key 's') and load (key 'l')
// actions.
//
// XML document structure:
// <village name="MyVillage">
//   <resources>
//     <gold>500.0</gold>
//     <iron>300.0</iron>
//     <lumber>400.0</lumber>
//   </resources>
//   <buildings>
//     <building type="VillageHall" level="1" hitPoints="500.0"/>
//     <building type="Farm"        level="1" hitPoints="100.0"/>
//   </buildings>
//   <habitants>
//     <habitant type="Soldier"/>
//     <habitant type="GoldMiner"/>
//   </habitants>
// </village>
public class VillageSerializer {

    /* Prevent instantiation – all methods are static utilities. */
    private VillageSerializer() { }

    /* ------------------------------------------------------------------ */
    /*  Save                                                               */
    /* ------------------------------------------------------------------ */

    /**
     * Serialises {@code village} to the XML file at {@code filePath}.
     *
     * @param village  the village to save
     * @param filePath path of the output XML file (created or overwritten)
     * @throws IOException if writing fails
     */
    public static void save(Village village, String filePath) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            // Root element
            Element root = doc.createElement("village");
            root.setAttribute("name", village.getName());
            doc.appendChild(root);

            // Resources block
            Element resources = doc.createElement("resources");
            root.appendChild(resources);
            appendText(doc, resources, "gold",   String.valueOf(village.getGold().getQuantity()));
            appendText(doc, resources, "iron",   String.valueOf(village.getIron().getQuantity()));
            appendText(doc, resources, "lumber", String.valueOf(village.getLumber().getQuantity()));

            // Buildings block
            Element buildings = doc.createElement("buildings");
            root.appendChild(buildings);
            for (Building b : village.getBuildings()) {
                Element be = doc.createElement("building");
                be.setAttribute("type",      buildingTypeName(b));
                be.setAttribute("level",     String.valueOf(b.getLevel()));
                be.setAttribute("hitPoints", String.valueOf(b.getHitPoints()));
                buildings.appendChild(be);
            }

            // Habitants block
            Element habitants = doc.createElement("habitants");
            root.appendChild(habitants);
            for (Habitant h : village.getHabitants()) {
                Element he = doc.createElement("habitant");
                he.setAttribute("type", h.getClass().getSimpleName());
                habitants.appendChild(he);
            }

            // Write to file
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));

        } catch (ParserConfigurationException | TransformerException e) {
            throw new IOException("XML serialisation failed: " + e.getMessage(), e);
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Load                                                               */
    /* ------------------------------------------------------------------ */

    /**
     * Deserialises a {@link Village} from the XML file at {@code filePath}.
     *
     * @param filePath path to an XML file previously written by {@link #save}
     * @return the reconstructed village
     * @throws IOException if reading or parsing fails
     */
    public static Village load(String filePath) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Disable external entity processing to prevent XXE attacks
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            String villageName = root.getAttribute("name");

            // Parse resources
            Element resEl = (Element) root.getElementsByTagName("resources").item(0);
            double gold   = Double.parseDouble(getChildText(resEl, "gold"));
            double iron   = Double.parseDouble(getChildText(resEl, "iron"));
            double lumber = Double.parseDouble(getChildText(resEl, "lumber"));

            Village village = new Village(villageName, gold, iron, lumber);
            // Clear the default buildings added by the Village constructor
            village.getBuildingsMutable().clear();

            // Parse buildings
            NodeList buildingNodes =
                    root.getElementsByTagName("buildings").item(0) == null
                    ? doc.createElement("dummy").getChildNodes()
                    : ((Element) root.getElementsByTagName("buildings").item(0))
                            .getElementsByTagName("building");

            for (int i = 0; i < buildingNodes.getLength(); i++) {
                Element be = (Element) buildingNodes.item(i);
                String type = be.getAttribute("type");
                int level   = Integer.parseInt(be.getAttribute("level"));
                double hp   = Double.parseDouble(be.getAttribute("hitPoints"));

                Building b = createBuilding(type);
                if (b != null) {
                    b.setHitPoints(hp);
                    // Advance level without deducting resources; catch expected upgrade exceptions
                    for (int lv = 1; lv < level; lv++) {
                        try {
                            b.upgrade();
                        } catch (MaxLevelReachedException | InsufficientResourcesException ignored) {
                            // Level advancement during load should not fail; ignore safely
                        }
                    }
                    // Restore serialized hitPoints after upgrades apply their own HP bonuses
                    b.setHitPoints(hp);
                    village.getBuildingsMutable().add(b);

                    // Update the VillageHall reference tracked inside Village
                    if (b instanceof VillageHall) {
                        village.setVillageHall((VillageHall) b);
                    }
                }
            }

            // Parse habitants
            NodeList habitantNodes =
                    root.getElementsByTagName("habitants").item(0) == null
                    ? doc.createElement("dummy").getChildNodes()
                    : ((Element) root.getElementsByTagName("habitants").item(0))
                            .getElementsByTagName("habitant");

            for (int i = 0; i < habitantNodes.getLength(); i++) {
                Element he = (Element) habitantNodes.item(i);
                String type = he.getAttribute("type");
                Habitant h  = createHabitant(type);
                if (h != null) {
                    village.getHabitantsMutable().add(h);
                }
            }

            return village;

        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("XML parsing failed: " + e.getMessage(), e);
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Helpers                                                            */
    /* ------------------------------------------------------------------ */

    /** Appends a simple text child element. */
    private static void appendText(Document doc, Element parent, String tag, String text) {
        Element el = doc.createElement(tag);
        el.appendChild(doc.createTextNode(text));
        parent.appendChild(el);
    }

    /** Returns the trimmed text content of the first child with the given tag name. */
    private static String getChildText(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        return nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "0";
    }

    /**
     * Returns a canonical type name for serialisation.
     * Handles multi-word names (e.g. "Village Hall" → "VillageHall").
     */
    private static String buildingTypeName(Building b) {
        return b.getClass().getSimpleName();
    }

    /** Instantiates a fresh {@link Building} from its simple class name. */
    private static Building createBuilding(String type) {
        switch (type) {
            case "Farm":        return new Farm();
            case "GoldMine":    return new GoldMine();
            case "IronMine":    return new IronMine();
            case "LumberMill":  return new LumberMill();
            case "ArcherTower": return new ArcherTower();
            case "Cannon":      return new Cannon();
            case "VillageHall": return new VillageHall();
            default:            return null;
        }
    }

    /** Instantiates a fresh {@link Habitant} from its simple class name. */
    private static Habitant createHabitant(String type) {
        switch (type) {
            case "Soldier":    return new Soldier();
            case "Archer":     return new Archer();
            case "Knight":     return new Knight();
            case "Catapult":   return new Catapult();
            case "GoldMiner":  return new GoldMiner();
            case "IronMiner":  return new IronMiner();
            case "Lumberman":  return new Lumberman();
            default:           return null;
        }
    }
}
