package com.ajjpj.ahcmapper;



//TODO AhcDifferMerger?
public interface AhcDiffer {
	/**
	 * Creates a diff between two object graphs 'oldSource' and 'newSource', including exactly those differences
	 *  that are relevant for a mapping to a specific target class.
	 */
//	<S, T> AhcObjectGraphDiff diff(S oldSource, S newSource, Class<T> targetClass);

	
	//TODO diff of top-level collections, i.e. with srcElementClass (and of course targetElementClass)
	
    //TODO apply (src, diff) ?	
}
