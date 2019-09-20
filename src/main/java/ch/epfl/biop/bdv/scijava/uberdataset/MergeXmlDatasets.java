package ch.epfl.biop.bdv.scijava.uberdataset;

import bdv.spimdata.SpimDataMinimal;
import mpicbg.spim.data.SpimData;
import mpicbg.spim.data.XmlIoSpimData;
import mpicbg.spim.data.generic.base.Entity;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.registration.ViewRegistration;
import mpicbg.spim.data.registration.ViewRegistrations;
import mpicbg.spim.data.registration.ViewTransform;
import mpicbg.spim.data.sequence.*;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Converts a list of Xml Datasets into a single Uber Xml Dataset, which can be used for BigDataViewer and FIJI BIG Plugins
 * Known limitations:
 * - Entities are not cloned, so informations are lost for some class of entities (Angle ?)
 * - Missing Views not taken into account
 * - Cache control not taken into account
 *
 * @author nicolas.chiaruttini@epfl.ch, BIOP, EPFL
 */

@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"Merge Xml Datasets")
public class MergeXmlDatasets implements Command {
    @Parameter(label = "Image Files")
    public File[] inputFiles;

    @Parameter(required=false, label = "output path", style = "directory") // To append datasets potentially
    public File xmlFilePath;

    @Parameter(required=false, label = "output file name") // To append datasets potentially
    public String xmlFileName;

    @Parameter(label = "Merge Channels")
    boolean mergeChannels = false;

    @Parameter(label = "Merge Tiles")
    boolean mergeTiles = false;

    @Parameter(label = "Merge Angles")
    boolean mergeAngles = false;

    @Parameter(label = "Merge Illuminations")
    boolean mergeIlluminations = false;

    @Parameter(label = "Verbose")
    boolean writeInfos = false;

    // Logger
    Consumer<String> log = s -> {};

    // Maps Xml File to the range used by ids of entities (sorted by entity class)
    Map<File,Map<Class, EntityNumberingRange>> mapFileToEntitiesIdRange = new HashMap<>();

    // Class of entities that will be merged
    public HashSet<Class> entitiesToMerge = new HashSet<>();

    // ViewSetups counter
    int viewSetupCounter = 0;

    // Map holding reference to TimePoints, created on demand, see function getTimePoint
    Map<Integer, TimePoint> mapIdToTp = new HashMap<>();

    @Override
    public void run() {

        // Logs if asked
        if (writeInfos) log = s -> System.out.println(s);

        // To merge entities of class Ent.getClass() based on their ids, add a line : entitiesToMerge.add(Ent.class);
        if (mergeTiles) entitiesToMerge.add(Tile.class);
        if (mergeAngles) entitiesToMerge.add(Angle.class);
        if (mergeChannels) entitiesToMerge.add(Channel.class);
        if (mergeIlluminations) entitiesToMerge.add(Illumination.class);

        // Many View Setups
        List<ViewSetup> viewSetups = new ArrayList<>();

        // Convert array to list
        ArrayList<File> inputFilesArray = new ArrayList<>();
        for (File f:inputFiles) {
            inputFilesArray.add(f);
        }

        // Already constructs  img loader -> uses functions already written inside the UberImgLoader constructor
        UberImgLoader uil = new UberImgLoader(inputFilesArray,null);

        // For each file, collect all entities and find range of ids covered, sorted by Entity class
        inputFilesArray.forEach(f -> {
            log.accept("Getting entities information for File "+f.getAbsolutePath());
            SpimDataMinimal sdm = uil.spimDataFromFiles.get(f);
            HashMap<Class,EntityNumberingRange> classEntityToRange = new HashMap<>();

            Map<Class,List<Entity>> entitiesByClass = sdm.getSequenceDescription()
                    .getViewDescriptions()
                    // Streams viewSetups
                    .values().stream()
                    // Filters if view is present
                    .filter(v -> v.isPresent())
                    // Gets Entities associated to ViewSetup
                    .map(v -> v.getViewSetup().getAttributes().values())
                    // Reduce into a single list and stream
                    .reduce(new ArrayList<>(), (a, b) -> {a.addAll(b); return a;}).stream()
                    // Collected and sorted by class
                    .collect(Collectors.groupingBy(e -> e.getClass(),Collectors.toList()));

            // For all entity classes, finds the min id and max id
            entitiesByClass.keySet().stream().forEach(c -> {
                EntityNumberingRange enr = entitiesByClass.get(c)
                        .stream()
                        .map(e -> new EntityNumberingRange(e))
                        .reduce(EntityNumberingRange::merge).get();
                classEntityToRange.put(c,enr);
            });

            // Stores the numbering scheme into the Hashmap
            mapFileToEntitiesIdRange.put(f,classEntityToRange);
        });

        log.accept("Initial numbering of entities:");
        printEntitiesNumberingInfo();

        // Renumbers entities ids, if necessary ( most probably ):
        HashMap<Class, EntityNumberingRange> currentOccupiedIndexes = new HashMap<>();
        for (File f:inputFiles) {
            mapFileToEntitiesIdRange.get(f).keySet().forEach(c -> {
                if ((currentOccupiedIndexes.containsKey(c))&&(!entitiesToMerge.contains(c))) {
                    // Renumbering if necessary
                    EntityNumberingRange enr = mapFileToEntitiesIdRange.get(f).get(c);
                    enr.setToNextAvailableRange(currentOccupiedIndexes.get(c));
                    // Recomputes current range for entities
                    currentOccupiedIndexes.put(c,EntityNumberingRange.merge(enr,currentOccupiedIndexes.get(c)));
                } else {
                    // No collision but we need to initialize this first entity
                    currentOccupiedIndexes.put(c,mapFileToEntitiesIdRange.get(f).get(c));
                }
            });
        }

        log.accept("Final numbering of entities:");
        printEntitiesNumberingInfo();

        // Needed to keep track of viewsetup renumbering
        Map<File,Map<Integer,Integer>> viewSetupIndexOldNewMap = new HashMap<>();

        // Create the new viewSetups List :
        inputFilesArray.forEach(f -> {
            log.accept("Getting entities information for File "+f.getAbsolutePath());
            viewSetupIndexOldNewMap.put(f,new HashMap<>());
            uil.spimDataFromFiles.get(f).getSequenceDescription().getViewSetupsOrdered().forEach(
                    ovs -> {
                          Map<Class,List<Entity>> newEntities = ovs.getAttributes()
                                        .values()
                                        .stream()
                                        .map(e -> mapFileToEntitiesIdRange.get(f).get(e.getClass()).getRenumberedEntity(e))
                                        .collect(Collectors.groupingBy(e -> e.getClass(),Collectors.toList()));

                        ViewSetup nvs;
                        // For ViewSetup Construction -> because ViewSetup needs to all be of the same sort for a sequence description which is not minimal
                        if (!newEntities.containsKey(Angle.class)) {
                            List<Entity> dummyList = new ArrayList<>();
                           dummyList.add(new Angle(-1));
                           newEntities.put(Angle.class, dummyList);
                        }
                        if (!newEntities.containsKey(Illumination.class)) {
                            List<Entity> dummyList = new ArrayList<>();
                            dummyList.add(new Illumination(-1));
                            newEntities.put(Illumination.class, dummyList);
                        }
                        if (!newEntities.containsKey(Channel.class)) {
                            List<Entity> dummyList = new ArrayList<>();
                            dummyList.add(new Channel(-1));
                            newEntities.put(Channel.class, dummyList);
                        }
                        // We assume all entities are of size 1
                        // TODO : assert ... all entities are of size 1
                        // Different constructors depending on the presence of some entities
                        if (newEntities.containsKey(Tile.class)) {
                            nvs = new ViewSetup(
                                    viewSetupCounter,
                                    ovs.getName(),
                                    ovs.getSize(),
                                    ovs.getVoxelSize(),
                                    (Tile) newEntities.get(Tile.class).get(0),
                                    (Channel) newEntities.get(Channel.class).get(0),
                                    (Angle) newEntities.get(Angle.class).get(0),
                                    (Illumination) newEntities.get(Illumination.class).get(0));
                        } else {
                            nvs = new ViewSetup(
                                    viewSetupCounter,
                                    ovs.getName(),
                                    ovs.getSize(),
                                    ovs.getVoxelSize(),
                                    (Channel) newEntities.get(Channel.class).get(0), // Tile is index of Serie
                                    (Angle) newEntities.get(Angle.class).get(0),
                                    (Illumination) newEntities.get(Illumination.class).get(0));
                        }
                        viewSetupIndexOldNewMap.get(f).put(ovs.getId(),nvs.getId());
                        viewSetups.add(nvs);
                        viewSetupCounter++;
                    });
        });

        final ArrayList<ViewRegistration> registrations = new ArrayList<>();

        // ------------------ Copying ViewRegistrations with appropriate timepoint and viewsetup ids
        inputFilesArray.forEach(f -> {
            log.accept("Getting entities information for File "+f.getAbsolutePath());
            uil.spimDataFromFiles.get(f)
                .getViewRegistrations()
                .getViewRegistrations()
                .forEach((vi,vr) -> {
                    int newViewId = viewSetupIndexOldNewMap.get(f).get(vi.getViewSetupId());
                    TimePoint tp = getTimePoint(f,vi.getTimePointId());
                    registrations.add( new ViewRegistration(
                            tp.getId(),
                            newViewId,
                            (ArrayList<ViewTransform>) vr.getTransformList()// Hmmm let's hope they are ArrayLists ?
                    ));
                });
        });


        // ------------------- BUILDING AND SAVING DATASET
        try {
            // Make Timepoints List
            List<TimePoint> timePoints = new ArrayList<>();
            timePoints.addAll(mapIdToTp.values());

            // Construct Sequence Description
            SequenceDescription sd = new SequenceDescription( new TimePoints( timePoints ), viewSetups , uil, null);

            // Construct spimData
            final SpimData spimData = new SpimData( xmlFilePath, sd, new ViewRegistrations( registrations ) );

            // Save Dataset
            File f = new File(xmlFilePath,xmlFileName);
            log.accept("Saving final uber dataset "+f.getAbsolutePath());
            new XmlIoSpimData().save( spimData, f.getAbsolutePath() );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to get Timepoint objects, avoid creation of new Object if they already exist
     * @param f  xml file within the dataset
     * @param id id of the TimePoint object
     * @return Timepoint object
     */
    public TimePoint getTimePoint(File f, int id) {
        // Optional f parameter in case one need to shift timepoints of one dataset vs other ones
        if (!mapIdToTp.containsKey(id)) {
            TimePoint tp = new TimePoint(id);
            mapIdToTp.put(id,tp);
        }
        return mapIdToTp.get(id);
    }

    /**
     *  Prints the id range of all entities of all files
     */
    public void printEntitiesNumberingInfo() {
        mapFileToEntitiesIdRange.keySet().stream().forEach(f -> {
            log.accept("File "+f.getAbsolutePath()+ " entities info: ");
            mapFileToEntitiesIdRange.get(f).keySet().stream().forEach(k -> {
                EntityNumberingRange enr = mapFileToEntitiesIdRange.get(f).get(k);
                log.accept("\t Entity class : "+k+" id is in range ["+enr.getIdxMin()+" - "+enr.getIdxMax()+"]");
            });
        });
    }

}
