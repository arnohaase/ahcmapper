package com.ajjpj.ahcmapper.mappingdef.builtin.collection;

import java.util.List;


public class ListWithIndexObjectMappingDef extends AbstractListObjectMappingDef {

    @Override
    protected void merge(List<Object> shadowList, List<Object> target) {
        //TODO make this cleaner --> remove elements at their index, minimize add / remove changes
        
        if (shadowList.size() < target.size()) {
            // fewer elements than before
            for(int i=0; i<shadowList.size(); i++) {
                target.set(i, shadowList.get(i));
            }
            while(target.size() > shadowList.size ()) {
                target.remove(shadowList.size ());
            }
        }
        else {
            // more elements than before
            for(int i=0; i<target.size(); i++) {
                target.set(i, shadowList.get(i));
            }
            for(int i=target.size (); i<shadowList.size (); i++) {
                target.add(shadowList.get(i));
            }
        }
    }
}
