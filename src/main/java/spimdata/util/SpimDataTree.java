package spimdata.util;

import mpicbg.spim.data.generic.AbstractSpimData;
import mpicbg.spim.data.generic.base.Entity;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpimDataTree {

    public AbstractSpimData asd;

    public SpimDataTree(AbstractSpimData asd) {
        this.asd = asd;
    }

    public Stream<BasicViewSetup> getViewSetupsStream() {
        return (Stream<BasicViewSetup>) asd.getSequenceDescription().getViewSetups().values().stream();//.collect(Collectors.toList());
    }

    /*public Stream<BasicViewSetup> getViewSetups(Predicate<BasicViewSetup> filter) {
        return (Stream<BasicViewSetup>) asd.getSequenceDescription().getViewSetups().values().stream().filter(
                vs -> filter.test((BasicViewSetup) vs)
        );//.collect(Collectors.toList());
    }*/

    public static List<Integer> viewSetupListToIdList(List<BasicViewSetup> vslist) {
        return vslist.stream().map(vs -> vs.getId()).collect(Collectors.toList());
    }

    public static Predicate<BasicViewSetup> entityFilter(Entity e) {
        return vs -> {
            if (vs.getAttribute(e.getClass())==null) {
                return false;
            } else {
                return vs.getAttribute(e.getClass()).getId()==e.getId();
            }
        };
    }

    public List<Entity> getEntitiesOfClass(Class<Entity> entityClass) {
        getViewSetupsStream()
                .map(bvs -> bvs.getAttributes().values())
                .reduce(new ArrayList<>(), (a, b) -> {a.addAll(b); return a;}).stream()
                .collect(Collectors.groupingBy(e -> e.getClass(),Collectors.toSet()));
        return null;
    }

    public List<Class<Entity>> getClassEntities(Class<Entity> entityClass) {
        //getViewSetupsStream().
        return null;
    }


    /*
    Map<Entity, SpimDataTree> branches = new HashMap<>();

    List<BasicViewSetup> allVSFromBranch;

    List<BasicViewSetup> leavesOnNode;

    Entity rootEntity;

    /**
     * List of all entities found in the SpimData
     */
    //Map<Class, Set<Entity>> entitiesSortedByClass;

    /*public SpimDataTree(List<Class<? extends Entity>> entitiesToSort, Entity rootEntity, List<BasicViewSetup> viewSetups) {
        this.rootEntity = rootEntity;
        allVSFromBranch = viewSetups;
        if (entitiesToSort.size()>0) {
            List<Class<? extends Entity>> entitiesToSortCopy = new ArrayList<>();

            entitiesToSort.forEach(cl -> entitiesToSortCopy.add(cl));

            Class<? extends Entity> entityClass = entitiesToSortCopy.get(entitiesToSort.size()-1);
            boolean stillContainsClass = true;
            while ((stillContainsClass)&&(!entitiesSortedByClass.containsKey(entityClass))) {
                entitiesToSortCopy.remove(entitiesToSort.size()-1);
                stillContainsClass = entitiesToSortCopy.size()>0;
                if (stillContainsClass) {
                    entityClass = entitiesToSortCopy.get(entitiesToSort.size()-1);
                }
            }
            if (stillContainsClass) {
                entitiesToSortCopy.remove(entityClass);
                entitiesSortedByClass.get(entityClass).forEach(
                        e -> {
                            List<BasicViewSetup> lvs = getViewSetupFilteredByEntity(e,allVSFromBranch);
                            if (lvs.size()>0)
                                branches.put(e,new SpimDataTree(entitiesToSortCopy,e,lvs));
                        }
                );
                final Class<? extends Entity> c = entityClass;
                leavesOnNode = allVSFromBranch
                        .stream()
                        .filter(vs -> vs.getAttribute(c)==null)
                        .collect(Collectors.toList());
            } else {
                leavesOnNode = allVSFromBranch;
            }
        } else {
            leavesOnNode = allVSFromBranch;
        }
    }

    public String toString() {
        if (rootEntity!=null) {
            return rootEntity.getClass().getSimpleName().toLowerCase() + ":" + rootEntity.getId()+" ("+allVSFromBranch.size()+")";
        } else {
            return "SpimData";
        }
    }

    public void sortEntitiesByClass() {
        entitiesSortedByClass =
                asd.getSequenceDescription()
                        .getViewSetups()
                        .values().stream()
                        .map(bvs -> bvs.getAttributes().values())
                        .reduce(new ArrayList<>(), (a, b) -> {a.addAll(b); return a;}).stream()
                        .collect(Collectors.groupingBy(e -> e.getClass(),Collectors.toSet()));
    }*/
}
