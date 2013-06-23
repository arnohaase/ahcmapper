package com.ajjpj.ahcmapper.core.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class AhcMapperDiff {
    private final Collection<AhcMapperDiffEntry> allEntries;
    private final Map<String, Collection<AhcMapperDiffEntry>> byPathToProperty = new HashMap<String, Collection<AhcMapperDiffEntry>>();
    private final Map<PathAndParent, Collection<AhcMapperDiffEntry>> byOldParent = new HashMap<PathAndParent, Collection<AhcMapperDiffEntry>>();
    private final Map<PathAndParent, Collection<AhcMapperDiffEntry>> byNewParent = new HashMap<PathAndParent, Collection<AhcMapperDiffEntry>>();

    public AhcMapperDiff(Collection<AhcMapperDiffEntry> allEntries) {
        this.allEntries = allEntries;
        
        for (AhcMapperDiffEntry entry: allEntries) {
            final String dotSeparated = entry.getPath().getDotSeparatedRepresentation();
            
            if(byPathToProperty.containsKey(dotSeparated)) {
                byPathToProperty.get(dotSeparated).add(entry);
            }
            else {
                final Collection<AhcMapperDiffEntry> entriesForPath = new ArrayList<AhcMapperDiffEntry>();
                entriesForPath.add(entry);
                byPathToProperty.put(dotSeparated, entriesForPath);
            }

            registerPathAndParent(new PathAndParent(dotSeparated, entry.getItem().getOldTargetMarker()), entry, byOldParent);
            registerPathAndParent(new PathAndParent(dotSeparated, entry.getItem().getNewTargetMarker()), entry, byNewParent);
        }
    }

    private static void registerPathAndParent(PathAndParent pap, AhcMapperDiffEntry entry, Map<PathAndParent, Collection<AhcMapperDiffEntry>> coll) {
        if(coll.containsKey(pap)) {
            coll.get(pap).add(entry);
        }
        else {
            final Collection<AhcMapperDiffEntry> entries = new ArrayList<AhcMapperDiffEntry>();
            entries.add(entry);
            coll.put(pap, entries);
        }
    }

    
    public Collection<AhcMapperDiffEntry> getEntries() {
        return allEntries;
    }
    
    public Collection<AhcMapperDiffEntry> getEntries(String pathToProperty) {
        final Collection<AhcMapperDiffEntry> result = byPathToProperty.get(pathToProperty);
        if(result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    public AhcMapperDiffEntry getSingleEntry(String pathToProperty) {
        return unique(getEntries(pathToProperty));
    }

    private AhcMapperDiffEntry unique(Collection<AhcMapperDiffEntry> entries) {
        switch(entries.size()) {
            case 0: return null;
            case 1: return entries.iterator().next();
            default: throw new IllegalStateException("not unique");
        }
    }

    public Collection<AhcMapperDiffEntry> getEntries(String pathToProperty, Object newTargetParent) {
        final Collection<AhcMapperDiffEntry> result = byNewParent.get(new PathAndParent(pathToProperty, newTargetParent));
        if(result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public Collection<AhcMapperDiffEntry> getEntriesForOldParent(String pathToProperty, Object oldTargetParent) {
        final Collection<AhcMapperDiffEntry> result = byOldParent.get(new PathAndParent(pathToProperty, oldTargetParent));
        if(result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public AhcMapperDiffEntry getSingleEntry(String pathToProperty, Object newTargetParent) {
        return unique(getEntries(pathToProperty, newTargetParent));
    }

    public AhcMapperDiffEntry getSingleEntryForOldParent(String pathToProperty, Object oldTargetParent) {
        return unique(getEntriesForOldParent(pathToProperty, oldTargetParent));
    }

    private static class PathAndParent {
        private final String path;
        private final Object parent;

        private PathAndParent(String path, Object parent) {
            this.path = path;
            this.parent = parent;
        }

        private String getPath() {
            return path;
        }

        private Object getParent() {
            return parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PathAndParent that = (PathAndParent) o;

            if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
            if (path != null ? !path.equals(that.path) : that.path != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = path != null ? path.hashCode() : 0;
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            return result;
        }
    }
}