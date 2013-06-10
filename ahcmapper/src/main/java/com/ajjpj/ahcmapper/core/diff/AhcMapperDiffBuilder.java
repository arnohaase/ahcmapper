package com.ajjpj.ahcmapper.core.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperDiffItem;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperRefDiffItem;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperValueDiffItem;
import com.ajjpj.ahcmapper.core.impl.DiffPathMarker;


public class AhcMapperDiffBuilder {
    private final Map<GraphSourcePosition, Collection<AhcMapperDiffItem>> items = new HashMap<GraphSourcePosition, Collection<AhcMapperDiffItem>>();
    private final Map<GraphSourcePosition, Set<StepDetails>> reachedFrom = new HashMap<GraphSourcePosition, Set<StepDetails>>();
            
    public <S> void addValueChange(S parentSource1, S parentSource2, Object targetValue1, Object targetValue2, AhcMapperPath parentPath, String propertyIdentifier) {
        final DiffPathMarker targetParentMarker = (DiffPathMarker) parentPath.getMarker();
        addDiffItem(parentSource1, parentSource2, new AhcMapperValueDiffItem(targetParentMarker.getTargetMarker1(), targetParentMarker.getTargetMarker2(), propertyIdentifier, targetValue1, targetValue2));
    }

    public <S> void addRefChange(S source1, S source2, Object targetRefMarker1, Object targetRefMarker2, AhcMapperPath parentPath, String propertyIdentifier) {
        if(parentPath.isRoot()) {
            // special case: the two graphs have different root objects
            
            addDiffItem(source1, source2, new AhcMapperRefDiffItem(null, null, propertyIdentifier, targetRefMarker1, targetRefMarker2));
        }
        else {
            final DiffPathMarker targetParentMarker = (DiffPathMarker) parentPath.getMarker();
            addDiffItem(source1, source2, new AhcMapperRefDiffItem(targetParentMarker.getTargetMarker1(), targetParentMarker.getTargetMarker2(), propertyIdentifier, targetRefMarker1, targetRefMarker2));
        }
    }
        
    public <S> void addDiffItem (S source1, S source2, AhcMapperDiffItem diffItem) {
        final GraphSourcePosition key = new GraphSourcePosition(source1, source2);
        
        Collection<AhcMapperDiffItem> itemsPerPos = items.get(key);
        if(itemsPerPos == null) {
            itemsPerPos = new ArrayList<AhcMapperDiffItem>();
            items.put(key, itemsPerPos);
        }
        itemsPerPos.add(diffItem);
    }

    /**
     * @return true iff the combination of source1 and source2 was visited before
     */
    public <S> boolean markVisited(AhcMapperPath path) {
        final DiffPathMarker curMarker = (DiffPathMarker) path.getMarker();
        final DiffPathMarker parentMarker = (DiffPathMarker) path.tail().getMarker();

        final GraphSourcePosition key = new GraphSourcePosition(curMarker.getSource1(), curMarker.getSource2());
        
        if(parentMarker == null) {
            return false;
        }
        
        Set<StepDetails> coll = reachedFrom.get(key);
        if(coll == null) {
            coll = new HashSet<StepDetails>();
            coll.add(new StepDetails(parentMarker, curMarker));
            reachedFrom.put(key, coll);
            return false;
        }
        else {
            coll.add(new StepDetails(parentMarker, curMarker));
            return true;
        }
    }

    private Collection<AhcMapperPath> getPathsToRoot(GraphSourcePosition pos, Set<GraphSourcePosition> visited) {
        if(visited.contains(pos)) {
            return Collections.emptySet();
        }
        
        final Collection<AhcMapperPath> result = new ArrayList<AhcMapperPath>();

        if(! reachedFrom.containsKey(pos)) {
            // this is the root
            result.add(AhcMapperPath.ROOT);
            return result;
        }
        
        for(StepDetails m: reachedFrom.get(pos)) {
            final GraphSourcePosition parentPos = new GraphSourcePosition(m.getParentSource1(), m.getParentSource2());
            final Set<GraphSourcePosition> newVisited = new HashSet<AhcMapperDiffBuilder.GraphSourcePosition>(visited);
            newVisited.add(pos);
            for(AhcMapperPath parentPath: getPathsToRoot(parentPos, newVisited)) {
                result.add(parentPath.withSegment(m.getPropertyName(), m));
            }
        }
        
        return result;
    }
    
    public AhcMapperDiff build() {
        final Collection<AhcMapperDiffEntry> entries = new ArrayList<AhcMapperDiffEntry>();

        for(GraphSourcePosition key: items.keySet()) {
            for(AhcMapperPath path: getPathsToRoot(key, new HashSet<GraphSourcePosition>())) {
                for(AhcMapperDiffItem item: items.get(key)) {
                    final StepDetails finalStepDetails = new StepDetails(key.source1, key.source2, item);
                    entries.add(new AhcMapperDiffEntry(item, path.withSegment(item.getPropertyIdentifier(), finalStepDetails)));
                }
            }
        }
        
        return new AhcMapperDiff(entries);
    }
    
    private static class GraphSourcePosition {
        private final Object source1;
        private final Object source2;
        
        public GraphSourcePosition(Object source1, Object source2) {
            this.source1 = source1;
            this.source2 = source2;
        }

        @Override
        public String toString() {
            return "Pos (" + source1 + " / " + source2 + ")";
        }
        
        @Override
        public boolean equals(Object obj) {
            final GraphSourcePosition other = (GraphSourcePosition) obj;
            return source1 == other.source1 && source2 == other.source2;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(source1) ^ System.identityHashCode(source2);
        }
    }
}
