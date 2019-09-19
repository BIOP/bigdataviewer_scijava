package ch.epfl.biop.bdv.scijava.uberdataset;

import mpicbg.spim.data.generic.base.Entity;

/**
 *  Class helping the renumbering of entities when building a uber xml dataset
 *  @author nicolas.chiaruttini@epfl.ch, BIOP, EPFL
 */
public class EntityNumberingRange {

    // Original id range of entities
    int ini_idxMin, ini_idxMax; // ini_idxMin included, ini_idxMax excluded

    // Class of entity
    public Class c;

    // offset for renumbering
    int offsetIdx = 0;

    public Entity getRenumberedEntity(Entity e) {
        int newId = e.getId()+offsetIdx;
        try {
            Entity newEntity = (e.getClass().getConstructor(int.class).newInstance(newId));
            return newEntity;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @return offset-ed min id
     */
    public int getIdxMin() {
        return offsetIdx+ini_idxMin;
    }

    /**
     *
     * @return offset-ed max id
     */
    public int getIdxMax() {
        return offsetIdx+ini_idxMax;
    }

    /**
     * Sets offset to avoid an already existing range
     * @param en_to_avoid = range to avoid
     */
    public void setToNextAvailableRange(EntityNumberingRange en_to_avoid) {
        if (this.getIdxMax() <= en_to_avoid.getIdxMin()) {
            return;
        }
        if (this.getIdxMin()>=en_to_avoid.getIdxMax()) {
            return;
        }
        this.offsetIdx = en_to_avoid.getIdxMax()-this.getIdxMin();
    }

    /**
     * Constructor with range specified
     * @param c class of Entity
     * @param min min id index
     * @param max max id index
     */
    public EntityNumberingRange(Class c, int min, int max) {
        this.c = c;
        this.offsetIdx=0;
        this.ini_idxMax=max;
        this.ini_idxMin=min;
    }

    /**
     * Range constructor with a single Entity element
     * @param e Entity
     */
    public EntityNumberingRange(Entity e) {
        this.c = e.getClass();
        ini_idxMin = e.getId();
        ini_idxMax = e.getId()+1;
    }

    /**
     * Merge ranges : used for range reduction of several ranges
     * @param e1 first range
     * @param e2 second range
     * @return a range covering both range, the offset is set to zero
     */
    static public EntityNumberingRange merge(EntityNumberingRange e1, EntityNumberingRange e2) {
        assert e2.c==e1.c;
        int min,max;

        // Keeps the smallest index
        if ((e2.getIdxMin())<(e1.getIdxMin())) {
            min=e2.getIdxMin();
        } else {
            min=e1.getIdxMin();
        }

        // Keep the biggest index
        if ((e2.getIdxMax())>(e1.getIdxMax())) {
            max=e2.getIdxMax();
        } else {
            max=e1.getIdxMax();
        }

        return new EntityNumberingRange(e1.c, min, max);
    }
}
