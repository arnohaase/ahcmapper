package com.ajjpj.ahcmapper.core.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class AhcMapperDiff {
    private final Collection<AhcMapperDiffEntry> allEntries;
    private final Map<String, Collection<AhcMapperDiffEntry>> byPathToProperty = new HashMap<String, Collection<AhcMapperDiffEntry>>();
    
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
        }
    }

    
    public Collection<AhcMapperDiffEntry> getEntries() {
        return allEntries;
    }
    
    //TODO getEntriesForParent
    
    public Collection<AhcMapperDiffEntry> getEntries(String pathToProperty) {
        final Collection<AhcMapperDiffEntry> result = byPathToProperty.get(pathToProperty);
        if(result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    public AhcMapperDiffEntry getSingleEntry(String pathToProperty) {
        final Collection<AhcMapperDiffEntry> entries = getEntries(pathToProperty);
        switch(entries.size()) {
        case 0: return null;
        case 1: return entries.iterator().next();
        default: throw new IllegalStateException("not unique");
        }
    }
}