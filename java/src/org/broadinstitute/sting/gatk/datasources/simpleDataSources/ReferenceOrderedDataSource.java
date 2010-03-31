package org.broadinstitute.sting.gatk.datasources.simpleDataSources;

import org.broadinstitute.sting.gatk.datasources.shards.Shard;
import org.broadinstitute.sting.gatk.refdata.SeekableRODIterator;
import org.broadinstitute.sting.gatk.refdata.tracks.RMDTrack;
import org.broadinstitute.sting.gatk.refdata.utils.FlashBackIterator;
import org.broadinstitute.sting.gatk.refdata.utils.LocationAwareSeekableRODIterator;
import org.broadinstitute.sting.gatk.walkers.ReadWalker;
import org.broadinstitute.sting.gatk.walkers.Walker;
import org.broadinstitute.sting.utils.GenomeLoc;
import org.broadinstitute.sting.utils.StingException;

import java.util.List;
/**
 * User: hanna
 * Date: May 21, 2009
 * Time: 10:04:12 AM
 * BROAD INSTITUTE SOFTWARE COPYRIGHT NOTICE AND AGREEMENT
 * Software and documentation are copyright 2005 by the Broad Institute.
 * All rights are reserved.
 *
 * Users acknowledge that this software is supplied without any warranty or support.
 * The Broad Institute is not responsible for its use, misuse, or
 * functionality.
 */

/**
 * A data source which provides a single type of reference-ordered data.
 */
public class ReferenceOrderedDataSource implements SimpleDataSource {
    /**
     * The reference-ordered data itself.
     */
    private final RMDTrack rod;

    /**
     * A pool of iterators for navigating through the genome.
     */
    private ReferenceOrderedDataPool iteratorPool = null;

    /**
     * Create a new reference-ordered data source.
     * @param rod
     */
    public ReferenceOrderedDataSource( Walker walker, RMDTrack rod) {
        this.rod = rod;
        // if (!rod.supportsQuery()) // TODO: Aaron turn on to enable Tribble searches
        this.iteratorPool = new ReferenceOrderedDataPool( walker, rod );
    }

    /**
     * Return the name of the underlying reference-ordered data.
     * @return Name of the underlying rod.
     */
    public String getName() {
        return this.rod.getName().toLowerCase(); // TODO: Aaron fix this.  this is a hack, because RODs always lowercased their names, but in for consistency for now
    }

    /**
     * Return the underlying reference-ordered data.
     * @return the underlying rod.
     */
    public RMDTrack getReferenceOrderedData() {
        return this.rod;
    }

    /**
     * Seek to the specified position and return an iterator through the data.
     * @param shard Shard that points to the selected position.
     * @return Iterator through the data.
     */
    public LocationAwareSeekableRODIterator seek( Shard shard ) {
        DataStreamSegment dataStreamSegment = shard.getGenomeLocs().size() != 0 ? new MappedStreamSegment(shard.getGenomeLocs().get(0)) : new EntireStream();
        LocationAwareSeekableRODIterator RODIterator = iteratorPool.iterator(dataStreamSegment);
        return RODIterator;
    }

    /**
     * Seek to the specified position and return an iterator through the data.
     *
     * @param loc GenomeLoc that points to the selected position.
     *
     * @return Iterator through the data.
     */
    public LocationAwareSeekableRODIterator seek(GenomeLoc loc) {
        DataStreamSegment dataStreamSegment = loc != null ? new MappedStreamSegment(loc) : new EntireStream();
        LocationAwareSeekableRODIterator RODIterator = iteratorPool.iterator(dataStreamSegment);
        return RODIterator;
    }


    /**
     * Close the specified iterator, returning it to the pool.
     * @param iterator Iterator to close.
     */
    public void close( LocationAwareSeekableRODIterator iterator ) {
        this.iteratorPool.release(iterator);        
    }

}

/**
 * A pool of reference-ordered data iterators.
 */
class ReferenceOrderedDataPool extends ResourcePool<LocationAwareSeekableRODIterator, LocationAwareSeekableRODIterator> {
    private final RMDTrack rod;
    boolean flashbackData = false;
    public ReferenceOrderedDataPool( Walker walker, RMDTrack rod ) {
        if (walker instanceof ReadWalker) flashbackData = true; // && (rod.getType() != IntervalRod.class)
        this.rod = rod;
    }

    /**
     * Create a new iterator from the existing reference-ordered data.  This new iterator is expected
     * to be completely independent of any other iterator.
     * @return The newly created resource.
     */
    public LocationAwareSeekableRODIterator createNewResource() {
        LocationAwareSeekableRODIterator iter = new SeekableRODIterator(rod.getIterator());
        return (flashbackData) ? new FlashBackIterator(iter) : iter;
    }

    /**
     * Finds the best existing ROD iterator from the pool.  In this case, the best existing ROD is defined as
     * the first one encountered that is at or before the given position.
     * @param segment @{inheritedDoc}
     * @param resources @{inheritedDoc}
     * @return @{inheritedDoc}
     */
    public LocationAwareSeekableRODIterator selectBestExistingResource( DataStreamSegment segment, List<LocationAwareSeekableRODIterator> resources ) {
        if(segment instanceof MappedStreamSegment) {
            GenomeLoc position = ((MappedStreamSegment)segment).getFirstLocation();

            for( LocationAwareSeekableRODIterator RODIterator : resources ) {

                if( (RODIterator.position() == null && RODIterator.hasNext()) ||
                    (RODIterator.position() != null && RODIterator.position().isBefore(position)) )
                    return RODIterator;
                if (RODIterator.position() != null && RODIterator instanceof FlashBackIterator && ((FlashBackIterator)RODIterator).canFlashBackTo(position)) {
                    ((FlashBackIterator)RODIterator).flashBackTo(position);
                    return RODIterator;
                }

            }
            return null;
        }
        else if(segment instanceof EntireStream) {
            // Asking for a segment over the entire stream, so by definition, there is no best existing resource.
            // Force the system to create a new one.
            return null;
        }
        else {
            throw new StingException("Unable to find a ROD iterator for segments of type " + segment.getClass());
        }
    }

    /**
     * In this case, the iterator is the resource.  Pass it through.
     */
    public LocationAwareSeekableRODIterator createIteratorFromResource( DataStreamSegment segment, LocationAwareSeekableRODIterator resource ) {
        return resource;
    }

    /**
     * kill the buffers in the iterator
     */
    public void closeResource( LocationAwareSeekableRODIterator resource ) {
        if (resource instanceof FlashBackIterator) ((FlashBackIterator)resource).close();            
    }
}


