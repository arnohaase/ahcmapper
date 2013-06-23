package com.ajjpj.ahcmapper.core.diff;

import com.ajjpj.ahcmapper.core.crosscutting.AhcMapperPath;
import com.ajjpj.ahcmapper.core.diff.builder.AhcMapperDiffItem;


//TODO rename this - DiffItem and DiffEntry ?!
public class AhcMapperDiffEntry {
    private final AhcMapperDiffItem item;
    private final AhcMapperPath path;
    
    public AhcMapperDiffEntry(AhcMapperDiffItem item, AhcMapperPath path) {
        this.item = item;
        this.path = path;
    }

    public AhcMapperDiffItem getItem() {
        return item;
    }

    public AhcMapperPath getPath() {
        return path;
    }
    
    @Override
    public String toString() {
        return "DiffEntry [" + item + " @ " + path.getDotSeparatedRepresentation() + "]";
    }
}